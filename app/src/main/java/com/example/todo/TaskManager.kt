//package com.example.todo
//
//import android.content.Context
//import android.util.Log
//import com.google.android.gms.tasks.Task
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.database.*
//
//class TaskManager(private val context: Context) {
//
//    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
//    private val database: DatabaseReference
//
//    init {
//        val pengguna = firebaseAuth.currentUser
//        if (pengguna != null) {
//            database = FirebaseDatabase.getInstance().getReference("tasks/${pengguna.uid}")
//        } else {
//            throw IllegalStateException("User is not logged in")
//        }
//    }
//
//    fun addTask(task: Task, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
//        val taskId = database.push().key ?: ""
//        val newTask = task.copy(id = taskId)
//
//        database.child(taskId).setValue(newTask)
//            .addOnSuccessListener {
//                Log.d("TaskManager", "Task added: $newTask")
//                onSuccess()
//            }
//            .addOnFailureListener { e ->
//                Log.e("TaskManager", "Failed to add task: ${e.message}")
//                onFailure(e.message ?: "Error adding task")
//            }
//    }
//
//    fun deleteTask(task: Task, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
//        database.child(task.id).removeValue()
//            .addOnSuccessListener {
//                Log.d("TaskManager", "Task deleted: ${task.id}")
//                onSuccess()
//            }
//            .addOnFailureListener { e ->
//                Log.e("TaskManager", "Failed to delete task: ${e.message}")
//                onFailure(e.message ?: "Error deleting task")
//            }
//    }
//
//    fun fetchTasks(onResult: (List<Task>) -> Unit, onError: (String) -> Unit) {
//        database.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val tasks = mutableListOf<Task>()
//                for (taskSnapshot in snapshot.children) {
//                    taskSnapshot.getValue(Task::class.java)?.let { tasks.add(it) }
//                }
//                onResult(tasks)
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                onError("Failed to load tasks: ${error.message}")
//            }
//        })
//    }
//}
