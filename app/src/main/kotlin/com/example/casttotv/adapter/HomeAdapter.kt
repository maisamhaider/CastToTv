package com.example.casttotv.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.casttotv.R
import com.example.casttotv.database.entities.HomeEntity
import com.example.casttotv.databinding.BrowserShortcutsItemBinding

class HomeAdapter(
    val onItemClicked: (HomeEntity, Boolean) -> Unit,
    private var context: Context,
) :
    ListAdapter<HomeEntity, HomeAdapter.Holder>(DIF_UTIL) {
    companion object {
        val DIF_UTIL = object : DiffUtil.ItemCallback<HomeEntity>() {
            override fun areItemsTheSame(oldItem: HomeEntity, newItem: HomeEntity) =
                oldItem.date == newItem.date

            override fun areContentsTheSame(oldItem: HomeEntity, newItem: HomeEntity) =
                oldItem == newItem
        }
    }

    class Holder(private val binding: BrowserShortcutsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(context: Context, homeEntity: HomeEntity) {

            binding.textview.text = homeEntity.title

            if (homeEntity.id == -1) {
                binding.imageviewPlus.visibility = View.VISIBLE
            } else {
                binding.imageviewPlus.visibility = View.GONE
                Glide.with(context).load(homeEntity.link).placeholder(R.drawable.ic_browser)
                    .into(binding.imageview)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val viewHolder = Holder(
            BrowserShortcutsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
        holder.bind(context, getItem(holder.absoluteAdapterPosition))
    }
}