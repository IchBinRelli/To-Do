package com.example.todo

import android.app.Application
import com.google.firebase.database.FirebaseDatabase

class OffClass : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }
}
