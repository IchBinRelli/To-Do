package com.example.todo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.databinding.ItemTaskBinding

class ListAdapter(
    private val listItems: List<Daftar>,
    private val onDelete: (Daftar) -> Unit,
    private val onEditClick: (Daftar) -> Unit,
    private val onItemClick: (Daftar) -> Unit,
    private val onPopupClick: (Daftar, View) -> Unit
) : RecyclerView.Adapter<ListAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.taskTitle)
        val delete: ImageView = itemView.findViewById(R.id.delete)
        val edit: ImageView = itemView.findViewById(R.id.edit)

        fun bind(item: Daftar) {
            title.text = item.title
            itemView.setOnClickListener {
                onItemClick(item) // Trigger click event
            }
            itemView.setOnLongClickListener { view ->
                onPopupClick(item, view)
                true
            }
            delete.setOnClickListener { onDelete(item) }
            edit.setOnClickListener { onEditClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listItems[position])
    }

    override fun getItemCount(): Int = listItems.size
}
