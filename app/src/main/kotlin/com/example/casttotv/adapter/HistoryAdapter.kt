package com.example.casttotv.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.casttotv.database.entities.HistoryEntity
import com.example.casttotv.database.entities.getDate
import com.example.casttotv.databinding.HistoryItemBinding

class HistoryAdapter(
    val onItemClicked: (HistoryEntity, Boolean) -> Unit,
    private var context: Context,
) : ListAdapter<HistoryEntity, HistoryAdapter.Holder>(DIF_UTIL) {

    companion object {
        val DIF_UTIL = object : DiffUtil.ItemCallback<HistoryEntity>() {
            override fun areItemsTheSame(
                oldItem: HistoryEntity,
                newItem: HistoryEntity,
            ): Boolean {
                return oldItem.date == newItem.date
            }

            override fun areContentsTheSame(
                oldItem: HistoryEntity,
                newItem: HistoryEntity,
            ): Boolean {
                return oldItem == newItem
            }

        }
    }

    class Holder(private val binding: HistoryItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(historyEntity: HistoryEntity) {
            binding.textViewText.text = historyEntity.title
            binding.textViewDate.text = historyEntity.getDate()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val viewHolder = Holder(
            HistoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
        viewHolder.itemView.setOnClickListener {
            onItemClicked(getItem(viewHolder.absoluteAdapterPosition), false)
        }
        viewHolder.itemView.setOnLongClickListener {
            onItemClicked(getItem(viewHolder.absoluteAdapterPosition), true)
            true
        }

        return viewHolder
    }


    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(holder.absoluteAdapterPosition))
    }

}