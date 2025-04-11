package com.example.todo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.todo.Task
import com.example.todo.databinding.ItemPrioritasBinding

class AdapterRepo(
    private var taskList: MutableList<Task>,
    private val layoutInflater: (LayoutInflater, ViewGroup, Boolean) -> ViewBinding,
    private val bindView: (ViewBinding, Task) -> Unit
) : RecyclerView.Adapter<AdapterRepo.TaskViewHolder>() {

    inner class TaskViewHolder(val binding: ViewBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = layoutInflater(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = taskList[position]
        bindView(holder.binding, task)
    }

    override fun getItemCount(): Int = taskList.size

    fun updateTasks(newTaskList: List<Task>) {
        taskList.clear()
        taskList.addAll(newTaskList)
        notifyDataSetChanged()
    }
}