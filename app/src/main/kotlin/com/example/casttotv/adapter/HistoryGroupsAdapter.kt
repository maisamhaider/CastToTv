package com.example.casttotv.adapter

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.casttotv.R
import com.example.casttotv.database.entities.HistoryEntity
import com.example.casttotv.databinding.BookmarkItemBinding
import com.example.casttotv.interfaces.OptionMenuListener

class HistoryGroupsAdapter(
    private val onClick: (HistoryEntity) -> Unit,
    private var context: Context, private val optionMenuListener: OptionMenuListener,
) :
    ListAdapter<HistoryEntity, HistoryGroupsAdapter.Holder>(DIF_UTIL) {

    companion object {
        val DIF_UTIL = object : DiffUtil.ItemCallback<HistoryEntity>() {
            override fun areItemsTheSame(
                oldItem: HistoryEntity,
                newItem: HistoryEntity,
            ) = oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: HistoryEntity,
                newItem: HistoryEntity,
            ) = oldItem == newItem
        }
    }

    class Holder(private val binding: BookmarkItemBinding) : RecyclerView.ViewHolder(binding.root) {

        private var _listener: ((int: Int) -> Unit)? = null

        fun removeAtPos(removeAt: (int: Int) -> Unit) {
            _listener = removeAt
        }

        fun bind(
            context: Context, history: HistoryEntity, optionMenuListener: OptionMenuListener,
        ) {
            binding.textViewText.text = history.title
            binding.textViewTextUrl.text = history.link
            Glide.with(context).load("www.google.com")
                .placeholder(R.drawable.ic_browser)
                .into(binding.imageview)

            binding.viewMenuClick.setOnClickListener {
                val popup = PopupMenu(context,
                    binding.appCompatImageView12,
                    Gravity.NO_GRAVITY,
                    R.attr.actionOverflowMenuStyle,
                    0)

                popup.inflate(R.menu.history_menu)
                popup.setOnMenuItemClickListener { item ->
                    optionMenuListener.item(item.itemId, history)
                    if (item.itemId == R.id.item_delete) {
                        _listener?.invoke(absoluteAdapterPosition)
                    }
                    true
                }
                popup.show()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val hold = Holder(
            BookmarkItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
        hold.itemView.setOnClickListener {
            onClick(getItem(hold.absoluteAdapterPosition))
        }
        return hold
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(context, getItem(holder.absoluteAdapterPosition), optionMenuListener)
        holder.removeAtPos(::removeAtPos)
    }

    private fun removeAtPos(pos: Int) {
        notifyItemRemoved(pos)
    }
}