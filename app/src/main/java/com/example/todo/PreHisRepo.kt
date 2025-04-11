//package com.example.todo.utils
//
//import android.util.Log
//import android.widget.Toast
//import com.example.todo.Task
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.database.*
//
//class PreHisRepo(private val database: DatabaseReference) {
//
//    fun fetchTasks(callback: (List<Task>) -> Unit) {
//        database.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val taskList = mutableListOf<Task>()
//
//                for (listSnapshot in snapshot.children) { // Langsung ambil listId
//                    val tasksNode = listSnapshot.child("tasks")
//                    for (taskSnapshot in tasksNode.children) { // Ambil taskId
//                        val task = taskSnapshot.getValue(Task::class.java)
//                        if (task != null) {
//                            taskList.add(task)
//                        }
//                    }
//                }
//
//                Log.d("FirebaseData", "Total tasks fetched: ${taskList.size}") // Debugging cek jumlah task
//                callback(taskList)
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                Log.e("FirebaseData", "Gagal mengambil data: ${error.message}")
//            }
//        })
//    }
//}
