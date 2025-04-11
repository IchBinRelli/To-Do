//package com.example.todo
//
//import android.content.BroadcastReceiver
//import android.content.Context
//import android.content.Intent
//import android.util.Log
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.database.FirebaseDatabase
//import java.text.SimpleDateFormat
//import java.util.Calendar
//import java.util.Locale
//
//class DeadlineReceiver : BroadcastReceiver() {
//    override fun onReceive(context: Context?, intent: Intent?) {
//        Log.d("DeadlineReceiver", "Receiver triggered!")
//
//        if (context != null) {
//            val repo = TaskRepo(context, FirebaseDatabase.getInstance().getReference("tasks"))
//
//            // Kamu bisa fetch semua task user di sini atau looping user list
//            // Asumsikan hanya 1 user untuk contoh ini
//            val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
//
//            val userRef = FirebaseDatabase.getInstance().getReference("tasks/$uid")
//            repo.fetchTasks(
//                uid,
//                onTasksFetched = { tasks ->
//                    val formatter = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
//                    val now = Calendar.getInstance().time
//                    for (task in tasks) {
//                        if (task.tenggat.isNotEmpty()) {
//                            try {
//                                val tenggatDate = formatter.parse(task.tenggat)
//                                if (tenggatDate != null && tenggatDate.before(now) && !task.isCompleted) {
//                                    kirimNotifikasiDeadline(context, task.title, task.tenggat)
//                                }
//                            } catch (e: Exception) {
//                                e.printStackTrace()
//                            }
//                        }
//                    }
//                },
//                onFailure = {}
//            )
//        }
//    }
//}
