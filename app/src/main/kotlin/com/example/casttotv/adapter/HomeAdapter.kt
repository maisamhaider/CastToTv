package com.example.casttotv.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.casttotv.database.entities.HomeEntity
import com.example.casttotv.databinding.HomeItemBinding

class HomeAdapter(
    val onItemClicked: (HomeEntity) -> Unit,
    private var context: Context,
) :
    ListAdapter<HomeEntity, HomeAdapter.Holder>(DIF_UTIL) {

    companion object {
        val DIF_UTIL = object : DiffUtil.ItemCallback<HomeEntity>() {
            override fun areItemsTheSame(oldItem: HomeEntity, newItem: HomeEntity): Boolean {
                return oldItem.date == newItem.date
            }

            override fun areContentsTheSame(oldItem: HomeEntity, newItem: HomeEntity): Boolean {
                return oldItem == newItem
            }

        }
    }

    class Holder(private val binding: HomeItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(homeEntity: HomeEntity) {
            binding.mTextView.text = homeEntity.link
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val viewHolder = Holder(
            HomeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
        viewHolder.itemView.setOnClickListener {
            onItemClicked(getItem(viewHolder.absoluteAdapterPosition))
        }

        return viewHolder
    }


    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(holder.absoluteAdapterPosition))
    }

}