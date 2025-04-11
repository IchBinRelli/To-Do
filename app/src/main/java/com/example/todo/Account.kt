package com.example.todo

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Account : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_account)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val email = intent.getStringExtra("email")
        val userEmailTextView = findViewById<TextView>(R.id.userEmail)
        userEmailTextView.text = email ?: "Email tidak ditemukan"

//        navigasi start

        val toDashboard = findViewById<ImageView>(R.id.close)
        val toKelola = findViewById<CardView>(R.id.cardKelola)
//        val toSetting = findViewById<CardView>(R.id.cardSetting)

        toDashboard.setOnClickListener {
            finish()

        }

        toKelola.setOnClickListener {
            val dashboard = Intent(this, KelolaAccout::class.java)
            startActivity(dashboard)
        }

//        toSetting.setOnClickListener {
//            val setting = Intent(this, Setting::class.java)
//            startActivity(setting)
//        }
//        navigsi end

        val emailPengguna = intent.getStringExtra("email")
        val textViewEmail = findViewById<TextView>(R.id.userEmail)
        textViewEmail.text = emailPengguna ?: "Email tidak ditemukan"
    }
}