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
import com.example.casttotv.database.entities.FavoritesEntity
import com.example.casttotv.databinding.BookmarkItemBinding
import com.example.casttotv.interfaces.OptionMenuListener


class FavoriteAdapter(
    val onItemClicked: (FavoritesEntity, Boolean) -> Unit,
    private var context: Context,
    private val optionMenuListener: OptionMenuListener,
) : ListAdapter<FavoritesEntity, FavoriteAdapter.Holder>(DIF_UTIL) {

    companion object {
        val DIF_UTIL = object : DiffUtil.ItemCallback<FavoritesEntity>() {
            override fun areItemsTheSame(
                oldItem: FavoritesEntity,
                newItem: FavoritesEntity,
            ): Boolean {
                return oldItem.date == newItem.date
            }

            override fun areContentsTheSame(
                oldItem: FavoritesEntity,
                newItem: FavoritesEntity,
            ): Boolean {
                return oldItem == newItem
            }

        }
    }

    class Holder(private val binding: BookmarkItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            context: Context,
            favoritesEntity: FavoritesEntity,
            optionMenuListener: OptionMenuListener,
        ) {
            binding.textViewText.text = favoritesEntity.title
            binding.textViewTextUrl.text = favoritesEntity.link
            Glide.with(context).load("www.google.com")
                .placeholder(R.drawable.ic_browser)
                .into(binding.imageview)

            binding.viewMenuClick.setOnClickListener {
                val popup = PopupMenu(context,
                    binding.appCompatImageView12,
                    Gravity.NO_GRAVITY,
                    R.attr.actionOverflowMenuStyle,
                    0)

                popup.inflate(R.menu.favorite_options_menu)
                popup.setOnMenuItemClickListener { item ->
                    optionMenuListener.item(item.itemId, favoritesEntity)
                    true
                }

                popup.show()
            }
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
        holder.bind(context, getItem(holder.absoluteAdapterPosition), optionMenuListener)
    }

}