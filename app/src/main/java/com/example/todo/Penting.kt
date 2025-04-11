package com.example.todo

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todo.adapter.AdapterRepo
import com.example.todo.databinding.ActivityPentingBinding
import com.example.todo.databinding.ItemPrioritasBinding
//import com.example.todo.utils.PreHisRepo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class Penting : AppCompatActivity() {
    private lateinit var binding: ActivityPentingBinding
    private lateinit var prioritasAdapter: AdapterRepo
    private lateinit var taskRepo: TaskRepo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPentingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val database = FirebaseDatabase.getInstance().getReference("tasks")
        taskRepo = TaskRepo(this, database)

        // Setup Adapter
        prioritasAdapter = AdapterRepo(
            mutableListOf(),
            layoutInflater = { inflater, parent, attachToParent ->
                ItemPrioritasBinding.inflate(inflater, parent, attachToParent)
            },
            bindView = { binding, task ->
                (binding as ItemPrioritasBinding).taskTitlePriority.text = task.title
            }
        )

//        prioritasAdapter = AdapterRepo(
//            mutableListOf(),
//            layoutInflater = { inflater, parent, attachToParent ->
//                ItemPrioritasBinding.inflate(inflater, parent, attachToParent)
//            },
//            bindView = { binding, task ->
//                val itemBinding = binding as ItemPrioritasBinding
//
//                itemBinding.taskTitlePriority.text = task.title
//                itemBinding.asaltask.text = "Mengambil..."
//
//                val userId = FirebaseAuth.getInstance().currentUser?.uid
//                val listId = task.listId // pastikan field ini ada di model Task kamu
//
//                if (userId != null && listId != null) {
//                    val listRef = FirebaseDatabase.getInstance()
//                        .getReference("lists/$userId/$listId/title")
//
//                    listRef.addListenerForSingleValueEvent(object : ValueEventListener {
//                        override fun onDataChange(snapshot: DataSnapshot) {
//                            val title = snapshot.getValue(String::class.java) ?: "Tidak diketahui"
//                            itemBinding.asaltask.text = "Dari: $title"
//                        }
//
//                        override fun onCancelled(error: DatabaseError) {
//                            itemBinding.asaltask.text = "Dari: (gagal ambil)"
//                        }
//                    })
//                } else {
//                    itemBinding.asaltask.text = "Dari: (tidak tersedia)"
//                }
//            }
//        )


        binding.recyclerViewPenting.apply {
            layoutManager = LinearLayoutManager(this@Penting)
            adapter = prioritasAdapter
        }

        fetchData()
        binding.back.setOnClickListener { finish() }
    }

    private fun fetchData() {
        taskRepo.fetchFilteredTasks("PRIORITAS") { tasks ->
            prioritasAdapter.updateTasks(tasks)
        }
    }

}

