package com.example.todo

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

data class Task(
    val id_task: String = "",
    val title: String = "",
    val tenggat: String = "",
    var isFavorite: Boolean = false,
    var isCompleted: Boolean = false,
)


class TodoList : AppCompatActivity() {

    private lateinit var listId: String
    private lateinit var taskRepo: TaskRepo
    private val todoItems = mutableListOf<Task>()
    private lateinit var adapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_todo_list)

        // Ambil UID dari Intent
        listId = intent.getStringExtra("uid") ?: run {
            Toast.makeText(this, "UID tidak valid", Toast.LENGTH_SHORT).show()
            finish()
            return
        }


        val title = intent.getStringExtra("title") ?: "Tanpa Judul"

        // Set judul di layout
        findViewById<TextView>(R.id.title).text = title

        // Inisialisasi repository Firebase
        val databaseReference = FirebaseDatabase.getInstance().getReference("tasks/$listId")
        taskRepo = TaskRepo(this, databaseReference)

        // Inisialisasi adapter RecyclerView
        adapter = TaskAdapter(todoItems,
            { task -> hapusTask(task) },
            { task -> editDialog(task) },
            {task, view -> tampilPopup(view,task)},
            {task -> fav(listId, task)},
            {task -> comp(task)},
        )
        findViewById<RecyclerView>(R.id.recyclerViewTodo).apply {
            layoutManager = LinearLayoutManager(this@TodoList)
            adapter = this@TodoList.adapter
        }

        // Ambil data tasks
        fetchTasks()

        // Tambah task baru
        findViewById<ImageView>(R.id.add).setOnClickListener {
            tampilDialog()
        }

        val balik = findViewById<ImageView>(R.id.back)
        balik.setOnClickListener {
            finish()
        }

        val moreBtn = findViewById<ImageView>(R.id.more)
        moreBtn.setOnClickListener {
            tampilPopup(it, Task())
        }

        listenRealtimeTasks()

//        cekDeadlineLewatDanNotif(this, todoItems)
//
//        setDailyAlarm(this)

    }

    private fun listenRealtimeTasks() {
        taskRepo.fetchTasks(
            listId = listId,
            useLiveListener = true, // realtime listener
            onTasksFetched = { tasks ->
                todoItems.clear()
                todoItems.addAll(tasks)
//                adapter.sortTasks()
                adapter.notifyDataSetChanged()
            },
            onFailure = { message ->
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        )
    }


    private fun fetchTasks() {
        taskRepo.fetchTasks(
            listId,
            onTasksFetched = { tasks ->
                todoItems.clear()
                todoItems.addAll(tasks)

//                // Sorting biar task selesai di bawah
//                todoItems.sortBy { it.isCompleted }

                adapter.notifyDataSetChanged()
            },
            onFailure = { message ->
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        )
    }


    private fun hapusTask(task: Task) {
        if (task.id_task.isNullOrEmpty()) {
            Toast.makeText(this, "Task ID tidak valid", Toast.LENGTH_SHORT).show()
            return
        }

        // Hapus dari Firebase
        taskRepo.hapus(listId, task.id_task,
            onSuccess = {
                todoItems.remove(task)
                adapter.notifyDataSetChanged()
            },
            onFailure = { message ->
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        )
    }


    private fun tampilDialog() {
        val dialogView = layoutInflater.inflate(R.layout.add_title, null)
        val dialog = AlertDialog.Builder(this).setView(dialogView).create()

        val inputTitle = dialogView.findViewById<EditText>(R.id.inputTitle)
        val submitButton = dialogView.findViewById<ImageView>(R.id.submit)
        val calendarButton = dialogView.findViewById<ImageView>(R.id.calendar)
        val tenggatText = dialogView.findViewById<TextView>(R.id.tenggat)

        calendarButton.setOnClickListener {
            showDatePicker(tenggatText)
        }

        submitButton.setOnClickListener {
            val taskTitle = inputTitle.text.toString()
            val tgl = tenggatText.text.toString()

            if (taskTitle.isNotEmpty()) {
                taskRepo.tambah(listId, taskTitle, tgl,
                    onSuccess = {
//                        fetchTasks() // Refresh data setelah tambah
                        dialog.dismiss()
                    },
                    onFailure = { message ->
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    }
                )
            } else {
                inputTitle.error = "Judul tugas tidak boleh kosong"
            }
        }

        dialog.show()
    }

    private fun editDialog(task: Task) {
        val dialogView = layoutInflater.inflate(R.layout.add_title, null)
        val dialog = AlertDialog.Builder(this).setView(dialogView).create()

        val inputTitle = dialogView.findViewById<EditText>(R.id.inputTitle)
        val submitButton = dialogView.findViewById<ImageView>(R.id.submit)
        val calendarButton = dialogView.findViewById<ImageView>(R.id.calendar)
        val tenggatText = dialogView.findViewById<TextView>(R.id.tenggat)

        inputTitle.setText(task.title)
        tenggatText.text = task.tenggat

        calendarButton.setOnClickListener {
            showDatePicker(tenggatText)
        }

        submitButton.setOnClickListener {
            val newTitle = inputTitle.text.toString()
            val newDate = tenggatText.text.toString()

            if (newTitle.isNotEmpty()) {
                taskRepo.edit(listId, task.id_task, newTitle, newDate,
                    onSuccess = { fetchTasks(); dialog.dismiss() },
                    onFailure = { message -> Toast.makeText(this, message, Toast.LENGTH_SHORT).show() }
                )
            } else {
                inputTitle.error = "Judul tidak boleh kosong"
            }
        }

        dialog.show()
    }

    private fun showDatePicker(tenggatText: TextView) {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, month, dayOfMonth)

                val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
                tenggatText.text = dateFormat.format(selectedDate.time)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.show()
    }

    private fun tampilPopup(view: View, selectedList: Task) {
        val wrapper = ContextThemeWrapper(view.context, R.style.PopupMenuStyle) // Pakai style custom
        val popupMenu = androidx.appcompat.widget.PopupMenu(wrapper, view)

        popupMenu.menuInflater.inflate(R.menu.popupmenu, popupMenu.menu)

        popupMenu.gravity = android.view.Gravity.END

        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.editLT -> {
                    editDialog(selectedList)
                    true
                }
                R.id.deleteLT -> {
                    AlertDialog.Builder(view.context)
                        .setTitle("Hapus Daftar")
                        .setMessage("Apakah kamu yakin ingin menghapus daftar ini?")
                        .setPositiveButton("Ya") { _, _ -> hapusTask(selectedList) }
                        .setNegativeButton("Tidak", null)
                        .show()
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }

//    fun cekDeadlineLewatDanNotif(context: Context, taskList: List<Task>) {
//        val formatter = java.text.SimpleDateFormat("dd MMMM yyyy", java.util.Locale.getDefault())
//        val now = java.util.Calendar.getInstance().time
//
//        for (task in taskList) {
//            if (task.tenggat.isNotEmpty()) {
//                try {
//                    val tenggatDate = formatter.parse(task.tenggat)
//                    if (tenggatDate != null && tenggatDate.before(now) && !task.isCompleted) {
//                        kirimNotifikasiDeadline(context, task.title, task.tenggat)
//                    }
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//            }
//        }
//    }
//
//    fun setDailyAlarm(context: Context) {
//        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//        val intent = Intent(context, DeadlineReceiver::class.java)
//        val pendingIntent = PendingIntent.getBroadcast(
//            context,
//            0,
//            intent,
//            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//        )
//
////        val calendar = Calendar.getInstance().apply {
////            set(Calendar.HOUR_OF_DAY, 7)
////            set(Calendar.MINUTE, 0)
////            set(Calendar.SECOND, 0)
////        }
//
//        val calendar = Calendar.getInstance().apply {
//            add(Calendar.MINUTE, 1) // testing 1 menit ke depan
//        }
//
////        alarmManager.setInexactRepeating(
////            AlarmManager.RTC_WAKEUP,
////            calendar.timeInMillis,
////            AlarmManager.INTERVAL_DAY,
////            pendingIntent
////        )
//
//        alarmManager.setExactAndAllowWhileIdle(
//            AlarmManager.RTC_WAKEUP,
//            calendar.timeInMillis,
//            pendingIntent
//        )
//
//    }

    private fun fav(listId: String, task: Task) {
        taskRepo.toggleFavorite(
            listId,
            task,
            onSuccess = { adapter.notifyDataSetChanged() },
            onFailure = { message -> Toast.makeText(this, message, Toast.LENGTH_SHORT).show() }
        )
    }

    private fun comp(task: Task) {
        taskRepo.toggleCompleted(
            listId,
            task,
            onSuccess = { adapter.notifyDataSetChanged() },
            onFailure = { message -> Toast.makeText(this, message, Toast.LENGTH_SHORT).show() }
        )
    }

//    override fun onResume() {
//        super.onResume()
////        fetchTasks()
//        adapter.sortTasks()
//    }

}
