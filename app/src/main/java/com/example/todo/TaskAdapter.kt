package com.example.todo

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.databinding.ItemTaskBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class TaskAdapter(
    private val taskList: MutableList<Task>,
    private val onDeleteClick: (Task) -> Unit,
    private val onEditClick: (Task) -> Unit,
    private val onPopupClick: (Task, View) -> Unit,
    private val onFavoriteClick: (Task) -> Unit,
    private val onCompletedClick: (Task) -> Unit
) : RecyclerView.Adapter<TaskAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkBtn: ImageView = itemView.findViewById(R.id.check)
        val taskTitleList: TextView = itemView.findViewById(R.id.taskTitleList)
        val editButton: ImageView = itemView.findViewById(R.id.edit)
        val deleteButton: ImageView = itemView.findViewById(R.id.delete)
        val favoriteBtn: ImageView = itemView.findViewById(R.id.prioritas)
        val tanggal: TextView = itemView.findViewById(R.id.tgl)

        fun bind(item: Task) {
            taskTitleList.text = item.title

            // Update strike-through text jika tugas selesai
            taskTitleList.paintFlags = if (item.isCompleted) {
                taskTitleList.paintFlags or android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                taskTitleList.paintFlags and android.graphics.Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }

            // Icon check button
            checkBtn.setImageResource(
                if (item.isCompleted) R.drawable.check_round
                else R.drawable.empty_round
            )

            checkBtn.setOnClickListener {
                onCompletedClick(item)
                notifyItemChanged(adapterPosition)
            }

            // Icon favorite button
            favoriteBtn.setImageResource(
                if (item.isFavorite) R.drawable.solidstar
                else R.drawable.outlinestar
            )

            favoriteBtn.setOnClickListener {
                onFavoriteClick(item)
                notifyItemChanged(adapterPosition)
            }

            itemView.setOnLongClickListener { view ->
                onPopupClick(item, view)
                true
            }

            deleteButton.setOnClickListener { onDeleteClick(item) }
            editButton.setOnClickListener { onEditClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false)
        return ViewHolder(view)
    }

//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val task = taskList[position]
//        holder.bind(task)
//
//        holder.tanggal.text = if (task.tenggat.isNotEmpty()) task.tenggat else "Tidak ada tenggat"
//    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val task = taskList[position]
        holder.bind(task)

        holder.tanggal.text = if (task.tenggat.isNotEmpty()) task.tenggat else "Tidak ada tenggat"

        val formatter = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        val tenggatDate = try {
            formatter.parse(task.tenggat)
        } catch (e: Exception) {
            null
        }

        val today = Calendar.getInstance().time

        if (tenggatDate != null && tenggatDate.before(today)) {
            holder.tanggal.setTextColor(Color.RED)
        } else {
            holder.tanggal.setTextColor(Color.BLACK)
        }
    }


    override fun getItemCount(): Int = taskList.size

    fun sortTasks() {
        taskList.sortBy { it.isCompleted }
        notifyDataSetChanged()
    }
}

