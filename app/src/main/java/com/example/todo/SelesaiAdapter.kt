//package com.example.todo.adapter
//
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.RadioButton
//import android.widget.TextView
//import androidx.recyclerview.widget.RecyclerView
//import com.example.todo.R
//import com.example.todo.Task
//import com.example.todo.databinding.ActivitySelesaiBinding
//import com.example.todo.databinding.ItemSelesaiBinding
//
//class SelesaiAdapter(
//    private val taskList: MutableList<Task>
//) : RecyclerView.Adapter<SelesaiAdapter.SelesaiViewHolder>() {
//
//    private var filteredTask: MutableList<Task> = mutableListOf()
//
//    init {
//        updateTasks(taskList)
//    }
//
//    inner class SelesaiViewHolder(val binding: ItemSelesaiBinding) : RecyclerView.ViewHolder(binding.root)
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelesaiViewHolder {
//        val binding = ItemSelesaiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        return SelesaiViewHolder(binding)
//    }
//
//    override fun onBindViewHolder(holder: SelesaiViewHolder, position: Int) {
//        val task = filteredTask[position]
//        holder.binding.taskTitleSelesai.text = task.title
//    }
//
//    override fun getItemCount(): Int = filteredTask.size
//
//    fun updateTasks(newTaskList: List<Task>) {
//        taskList.clear()
//        taskList.addAll(newTaskList)
//
//        filteredTask = taskList.filter { it.isCompleted == true }.toMutableList()
//
//
//        notifyDataSetChanged()
//    }
//}
