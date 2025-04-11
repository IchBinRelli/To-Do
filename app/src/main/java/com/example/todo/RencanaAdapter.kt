//package com.example.todo
//
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import androidx.recyclerview.widget.RecyclerView
//import com.example.todo.databinding.ItemRencanaBinding
//
//class RencanaAdapter(
//    private var taskList: MutableList<Task>
//) : RecyclerView.Adapter<RencanaAdapter.RencanaViewHolder>() {
//
//    private var filteredList: MutableList<Task> = mutableListOf()
//
//    init {
//        updateTasks(taskList)
//    }
//
//    inner class RencanaViewHolder(val binding: ItemRencanaBinding) : RecyclerView.ViewHolder(binding.root)
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RencanaViewHolder {
//        val binding = ItemRencanaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        return RencanaViewHolder(binding)
//    }
//
//    override fun onBindViewHolder(holder: RencanaViewHolder, position: Int) {
//        val task = filteredList[position]
//        holder.binding.taskTitleRencana.text = task.title
//        holder.binding.tgl.text = if (task.tenggat.isNotEmpty()) task.tenggat else "Tidak ada tenggat"
//    }
//
//    override fun getItemCount(): Int = filteredList.size
//
//    fun updateTasks(newTaskList: List<Task>) {
//        taskList.clear()
//        taskList.addAll(newTaskList)
//
//        filteredList = taskList.filter {
//            val tenggatTrimmed = it.tenggat.trim()
//            tenggatTrimmed.isNotEmpty() && tenggatTrimmed != "Tetapkan tenggat waktu"
//        }.toMutableList()
//
//        notifyDataSetChanged()
//    }
//}
