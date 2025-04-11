package com.example.todo

import android.content.Intent
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todo.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

data class Daftar(
    val id_list: String = "",
    val title: String = ""
)

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var listRepo: ListRepo
    private val listItems = mutableListOf<Daftar>()
    private lateinit var adapter: ListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listRepo = ListRepo(this)

        // Setup RecyclerView
        adapter = ListAdapter(
            listItems,
            { list -> deleteList(list) },
            { list -> editDialog(list) },
            { list -> openTodoList(list) },
            {list, view -> tampilPopup(view,list)}
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        // Fetch Data
        fetchLists()

        // Tombol Tambah
        binding.tambah.setOnClickListener {
            showAddDialog()
        }

        val moreBtn = findViewById<ImageView>(R.id.more)
        moreBtn.setOnClickListener {
            tampilPopup(it, Daftar())
        }

        val prioritybtn = findViewById<CardView>(R.id.cardPenting)
        prioritybtn.setOnClickListener {
            val intent = Intent (this, Penting ::class.java)
            startActivity(intent)
        }

        val profileBtn = findViewById<ImageView>(R.id.profile)
        profileBtn.setOnClickListener {
            val intent = Intent(this, Account::class.java)
            startActivity(intent)
        }

        val selesaiBtn = findViewById<CardView>(R.id.cardSelesai)
        selesaiBtn.setOnClickListener {
            val intent = Intent(this, Selesai::class.java)
            startActivity(intent)
        }

        val rencanaBtn = findViewById<CardView>(R.id.cardRencana)
        rencanaBtn.setOnClickListener {
            val intent = Intent(this, Rencana::class.java)
            startActivity(intent)
        }

        val btnAccount = findViewById<ImageView>(R.id.profile)
        val user = FirebaseAuth.getInstance().currentUser
        val textViewEmail = findViewById<TextView>(R.id.namaPengguna)
        val email = user?.email
        textViewEmail.text = email ?: "Email tidak ditemukan"
        btnAccount.setOnClickListener {
            val intent = Intent(this, Account::class.java)
            intent.putExtra("email", email)
            startActivity(intent)
        }
    }

    private fun fetchLists() {
        listRepo.fetchLists(
            onListsFetched = { lists ->
                listItems.clear()
                listItems.addAll(lists)
                adapter.notifyDataSetChanged()
            },
            onFailure = { message ->
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        )
    }

//    private fun deleteList(list: Daftar) {
//        listRepo.hapus(
//            list.id_list,
//            onSuccess = {
//                listItems.remove(list)
//                adapter.notifyDataSetChanged()
//            },
//            onFailure = { message ->
//                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
//            }
//        )
//    }

    private fun deleteList(list: Daftar) {
        listRepo.hapus(
            list.id_list,
            onSuccess = {
                listItems.remove(list)
                adapter.notifyDataSetChanged()
            },
            onFailure = { message ->
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        )
    }



    private fun showAddDialog() {
        val dialogView = layoutInflater.inflate(R.layout.addlist, null)
        val dialog = AlertDialog.Builder(this).setView(dialogView).create()

        val inputTitle = dialogView.findViewById<EditText>(R.id.inputList)
        val submitButton = dialogView.findViewById<ImageView>(R.id.submitList)

        submitButton.setOnClickListener {
            val title = inputTitle.text.toString()

            if (title.isNotEmpty()) {
                listRepo.tambah(title,
                    onSuccess = {
                        fetchLists()
                        dialog.dismiss()
                    },
                    onFailure = { message ->
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    }
                )
            } else {
                inputTitle.error = "Judul tidak boleh kosong"
            }
        }

        dialog.show()
    }

    private fun editDialog(list: Daftar) {
        val dialogView = layoutInflater.inflate(R.layout.addlist, null)
        val dialog = AlertDialog.Builder(this).setView(dialogView).create()

        val inputTitle = dialogView.findViewById<EditText>(R.id.inputList)
        val submitButton = dialogView.findViewById<ImageView>(R.id.submitList)

        inputTitle.setText(list.title)

        submitButton.setOnClickListener {
            val newTitle = inputTitle.text.toString()

            if (newTitle.isNotEmpty()) {
                listRepo.edit(list.id_list, newTitle,
                    onSuccess = {
                        fetchLists()
                        dialog.dismiss()
                    },
                    onFailure = { message ->
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    }
                )
            } else {
                inputTitle.error = "Judul tidak boleh kosong"
            }
        }

        dialog.show()
    }

    private fun openTodoList(list: Daftar) {
        val intent = Intent(this, TodoList::class.java)
        intent.putExtra("uid", list.id_list)
        intent.putExtra("title", list.title)
        startActivity(intent)
    }

    private fun tampilPopup(view: View, selectedList: Daftar) {
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
                        .setPositiveButton("Ya") { _, _ ->
                            deleteList(selectedList)
                        }
                        .setNegativeButton("Tidak", null)
                        .show()
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }
}
