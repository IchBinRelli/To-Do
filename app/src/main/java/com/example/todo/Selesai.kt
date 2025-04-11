package com.example.todo

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todo.adapter.AdapterRepo
import com.example.todo.databinding.ActivitySelesaiBinding
import com.example.todo.databinding.ItemSelesaiBinding
//import com.example.todo.utils.PreHisRepo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class Selesai : AppCompatActivity() {
    private lateinit var binding: ActivitySelesaiBinding
    private lateinit var selesaiAdapter: AdapterRepo
    private lateinit var taskRepo: TaskRepo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelesaiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val database = FirebaseDatabase.getInstance().getReference("tasks")
        taskRepo = TaskRepo(this, database)

        // Setup Adapter
        selesaiAdapter = AdapterRepo(
            mutableListOf(),
            layoutInflater = { inflater, parent, attachToParent ->
                ItemSelesaiBinding.inflate(inflater, parent, attachToParent)
            },
            bindView = { binding, task ->
                (binding as ItemSelesaiBinding).taskTitleSelesai.text = task.title
            }
        )

        binding.recyclerViewSelesai.apply {
            layoutManager = LinearLayoutManager(this@Selesai)
            adapter = selesaiAdapter
        }

        fetchData()
        binding.back.setOnClickListener { finish() }


    }

    private fun fetchData() {
        taskRepo.fetchFilteredTasks("SELESAI") { tasks ->
            selesaiAdapter.updateTasks(tasks)
        }
    }


}