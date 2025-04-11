package com.example.todo

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class TaskRepo(private val context: Context, private var database: DatabaseReference) {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    init {
        database = FirebaseDatabase.getInstance().getReference("tasks")
    }

    fun tambah(
        listId: String,
        taskTitle: String,
        tenggat: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val taskId = database.child(listId).child("tasks").push().key ?: return
        val task = Task(taskId, taskTitle, tenggat)

        database.child(listId).child("tasks").child(taskId).setValue(task)
            .addOnSuccessListener {
                Toast.makeText(context, "Tugas berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                onSuccess()
            }
            .addOnFailureListener { e ->
                onFailure(e.message ?: "Gagal menambahkan tugas")
            }
    }

    fun hapus(listId: String, taskId: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        database.child(listId).child("tasks").child(taskId).removeValue()
            .addOnSuccessListener {
                Toast.makeText(context, "Tugas berhasil dihapus", Toast.LENGTH_SHORT).show()
                onSuccess()
            }
            .addOnFailureListener { e ->
                onFailure(e.message ?: "Gagal menghapus tugas")
            }
    }

    fun edit(
        listId: String,
        taskId: String,
        newTitle: String,
        newDate: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {

        val taskUpdates = mapOf(
            "title" to newTitle,
            "tenggat" to newDate
        )

        val taskRef = database.child(listId).child("tasks").child(taskId)

        taskRef.updateChildren(taskUpdates)
            .addOnSuccessListener {
                Toast.makeText(context, "Tugas berhasil diperbarui", Toast.LENGTH_SHORT).show()
                onSuccess()
            }
            .addOnFailureListener { e ->
                onFailure(e.message ?: "Gagal memperbarui tugas")
            }
    }

//    fun toggleFavorite(
//        listId: String,
//        task: Task,
//        onSuccess: () -> Unit,
//        onFailure: (String) -> Unit
//    ) {
//        val taskRef = database.child(listId).child("tasks").child(task.id_task)
//
//        val newFavoriteStatus = !task.isFavorite
//        taskRef.child("favorite").setValue(newFavoriteStatus)
//            .addOnSuccessListener {
//                task.isFavorite = newFavoriteStatus
//                Toast.makeText(context, "Status favorite diperbarui", Toast.LENGTH_SHORT).show()
//                onSuccess()
//            }
//            .addOnFailureListener { e ->
//                onFailure(e.message ?: "Gagal memperbarui status favorite")
//            }
//    }

    fun toggleFavorite(
        listId: String,
        task: Task,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val newFavoriteStatus = !task.isFavorite

        val updates = mutableMapOf<String, Any>(
            "favorite" to newFavoriteStatus
        )

        // Kalau task dijadikan favorite, pastikan tidak completed
        if (newFavoriteStatus) {
            updates["completed"] = false
            task.isCompleted = false // biar langsung kelihatan di UI
        }

        database.child(listId).child("tasks").child(task.id_task!!)
            .updateChildren(updates)
            .addOnSuccessListener {
                task.isFavorite = newFavoriteStatus
                onSuccess()
            }
            .addOnFailureListener { e ->
                onFailure(e.message ?: "Gagal memperbarui status favorite")
            }
    }


//    fun toggleCompleted(
//        listId: String,
//        task: Task,
//        onSuccess: () -> Unit,
//        onFailure: (String) -> Unit
//    ) {
//        val taskRef = database.child(listId).child("tasks").child(task.id_task)
//
//        val newCompletedStatus = !task.isCompleted
//        taskRef.child("completed").setValue(newCompletedStatus)
//            .addOnSuccessListener {
//                task.isCompleted = newCompletedStatus
//                Toast.makeText(context, "Status selesai diperbarui", Toast.LENGTH_SHORT).show()
//                onSuccess()
//            }
//            .addOnFailureListener { e ->
//                onFailure(e.message ?: "Gagal memperbarui status selesai")
//            }
//    }

    fun toggleCompleted(
        listId: String,
        task: Task,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val updatedStatus = !task.isCompleted
        val updates = mutableMapOf<String, Any>(
            "completed" to updatedStatus
        )

        if (updatedStatus) {
            updates["favorite"] = false
            task.isFavorite = false
        }

        database.child(listId).child("tasks").child(task.id_task!!)
            .updateChildren(updates)
            .addOnSuccessListener {
                task.isCompleted = updatedStatus
                onSuccess()
            }
            .addOnFailureListener {
                onFailure(it.message ?: "Gagal update status")
            }
    }

    fun fetchTasks(
        listId: String? = null,
        onTasksFetched: (List<Task>) -> Unit,
        onFailure: (String) -> Unit,
        useLiveListener: Boolean = false // <- tambah opsi realtime
    ) {
        val ref = if (listId != null) {
            database.child(listId).child("tasks")
        } else {
            database
        }

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val taskList = mutableListOf<Task>()

                if (listId == null) {
                    for (listSnapshot in snapshot.children) {
                        val tasksNode = listSnapshot.child("tasks")
                        for (taskSnapshot in tasksNode.children) {
                            val task = taskSnapshot.getValue(Task::class.java)
                            if (task != null) taskList.add(task)
                        }
                    }
                } else {
                    for (taskSnapshot in snapshot.children) {
                        val task = taskSnapshot.getValue(Task::class.java)
                        if (task != null) taskList.add(task)
                    }
                }

                onTasksFetched(taskList)
            }

            override fun onCancelled(error: DatabaseError) {
//                onFailure("Gagal memuat tugas: ${error.message}")
            }
        }

        // Tambahan penting: pilih pakai realtime listener atau sekali fetch
        if (useLiveListener) {
            ref.addValueEventListener(listener) // <- ini dia tambahannya
        } else {
            ref.addListenerForSingleValueEvent(listener) // bawaan lama
        }
    }

    fun fetchTasksForUser(
        userId: String,
        onTasksFetched: (List<Task>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val listsRef = FirebaseDatabase.getInstance().getReference("lists/$userId")

        listsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    onTasksFetched(emptyList())
                    return
                }

                val idListCollection = snapshot.children.mapNotNull { it.key }
                val allTasks = mutableListOf<Task>()
                var processedLists = 0

                for (idList in idListCollection) {
                    val tasksRef = FirebaseDatabase.getInstance().getReference("tasks/$idList/tasks")
                    tasksRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(taskSnapshot: DataSnapshot) {
                            for (taskNode in taskSnapshot.children) {
                                val task = taskNode.getValue(Task::class.java)
                                if (task != null) {
                                    allTasks.add(task)
                                }
                            }
                            processedLists++
                            if (processedLists == idListCollection.size) {
                                onTasksFetched(allTasks)
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
//                            onFailure("Gagal memuat tugas dari list: ${error.message}")
                        }
                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {
//                onFailure("Gagal mengambil daftar list: ${error.message}")
            }
        })
    }


    // Fetch task dari id_list yang ditemukan
//    fun fetchTasksFromList(
//        idList: String,
//        onTasksFetched: (List<Task>) -> Unit,
//        onFailure: (String) -> Unit
//    ) {
//        val tasksRef = FirebaseDatabase.getInstance().getReference("tasks/$idList/tasks")
//
//        tasksRef.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val taskList = mutableListOf<Task>()
//                for (taskSnapshot in snapshot.children) {
//                    val task = taskSnapshot.getValue(Task::class.java)
//                    if (task != null) {
//                        taskList.add(task)
//                    }
//                }
//                onTasksFetched(taskList)
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                onFailure("Gagal memuat tugas: ${error.message}")
//            }
//        })
//    }

    fun fetchFilteredTasks(filterType: String, callback: (List<Task>) -> Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return callback(emptyList())

        fetchTasksForUser(userId, onTasksFetched = { taskList ->
            val filteredTasks = when (filterType) {
                "SELESAI" -> taskList.filter { it.isCompleted }
                "RENCANA" -> taskList.filter { it.tenggat.trim().isNotEmpty() && it.tenggat != "Tetapkan tenggat waktu" }
                "PRIORITAS" -> taskList.filter { it.isFavorite }
                else -> taskList
            }
            callback(filteredTasks)
        }, onFailure = {
            callback(emptyList())
        })
    }
}
