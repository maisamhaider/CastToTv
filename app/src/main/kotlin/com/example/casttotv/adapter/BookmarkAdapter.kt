package com.example.casttotv.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.casttotv.database.entities.BookmarkEntity
import com.example.casttotv.databinding.BookmarkItemBinding

class BookmarkAdapter(
    val onItemClicked: (BookmarkEntity, Boolean) -> Unit,
    private var context: Context,
) : ListAdapter<BookmarkEntity, BookmarkAdapter.Holder>(DIF_UTIL) {

    companion object {
        val DIF_UTIL = object : DiffUtil.ItemCallback<BookmarkEntity>() {
            override fun areItemsTheSame(
                oldItem: BookmarkEntity,
                newItem: BookmarkEntity,
            ): Boolean {
                return oldItem.date == newItem.date
            }

            override fun areContentsTheSame(
                oldItem: BookmarkEntity,
                newItem: BookmarkEntity,
            ): Boolean {
                return oldItem == newItem
            }

        }
    }

    class Holder(private val binding: BookmarkItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(bookmarkEntity: BookmarkEntity) {
            binding.textViewText.text = bookmarkEntity.title
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val viewHolder = Holder(
            BookmarkItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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