package com.example.todo

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class KelolaAccout : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var out: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kelola_accout)

        firebaseAuth = FirebaseAuth.getInstance()

        // Tombol kembali
        val backbtn = findViewById<ImageView>(R.id.back)
        backbtn.setOnClickListener {
            finish() // Kembali ke halaman sebelumnya
        }

        // Tombol Logout
        out = findViewById(R.id.logout)
        out.setOnClickListener {
            firebaseAuth.signOut() // Logout dari Firebase
            val intent = Intent(this, Login::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }
}
