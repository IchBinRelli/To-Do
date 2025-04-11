//package com.example.todo
//
//import android.app.*
//import android.content.Context
//import android.os.Build
//import androidx.core.app.NotificationCompat
//import com.example.todo.R
//
//fun kirimNotifikasiDeadline(context: Context, title: String, tenggat: String) {
//    val channelId = "deadline_channel"
//    val notificationManager =
//        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//    // Buat channel kalau versi Android >= Oreo
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//        val channelName = "Deadline Reminder"
//        val channel = NotificationChannel(
//            channelId,
//            channelName,
//            NotificationManager.IMPORTANCE_HIGH
//        ).apply {
//            description = "Notifikasi untuk tugas yang sudah lewat tenggat"
//        }
//        notificationManager.createNotificationChannel(channel)
//    }
//
//    // Bangun notifikasi
//    val builder = NotificationCompat.Builder(context, channelId)
//        .setSmallIcon(R.drawable.ic_notification) // pastiin icon ini ada
//        .setContentTitle("Tenggat Tugas Terlewat!")
//        .setContentText("Tugas \"$title\" lewat tenggat: $tenggat")
//        .setPriority(NotificationCompat.PRIORITY_HIGH)
//        .setAutoCancel(true)
//
//    // Tampilkan notif
//    notificationManager.notify(title.hashCode(), builder.build())
//}
