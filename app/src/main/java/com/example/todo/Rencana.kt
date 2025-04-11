package com.example.todo

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todo.adapter.AdapterRepo
import com.example.todo.databinding.ActivityRencanaBinding
import com.example.todo.databinding.ItemRencanaBinding
//import com.example.todo.utils.PreHisRepo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class Rencana : AppCompatActivity() {
    private lateinit var binding: ActivityRencanaBinding
    private lateinit var rencanaAdapter: AdapterRepo
    private lateinit var taskRepo: TaskRepo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRencanaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val database = FirebaseDatabase.getInstance().getReference("tasks")
        taskRepo = TaskRepo(this, database)

        // Setup Adapter
        rencanaAdapter = AdapterRepo(
            mutableListOf(),
            layoutInflater = { inflater, parent, attachToParent ->
                ItemRencanaBinding.inflate(inflater, parent, attachToParent)
            },
            bindView = { binding, task ->
                (binding as ItemRencanaBinding).taskTitleRencana.text = task.title
                binding.tgl.text = if (task.tenggat.isNotEmpty()) task.tenggat else "Tidak ada tenggat"
            }
        )

        binding.recyclerViewRencana.apply {
            layoutManager = LinearLayoutManager(this@Rencana)
            adapter = rencanaAdapter
        }

        fetchData()
        binding.back.setOnClickListener { finish() }
    }

    private fun fetchData() {
        taskRepo.fetchFilteredTasks("RENCANA") { tasks ->
            rencanaAdapter.updateTasks(tasks)
        }
    }

}

