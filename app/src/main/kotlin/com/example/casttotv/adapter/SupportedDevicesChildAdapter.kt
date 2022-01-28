package com.example.casttotv.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.casttotv.databinding.SupportedDevicesChildItemBinding

class SupportedDevicesChildAdapter :
    ListAdapter<String, SupportedDevicesChildAdapter.Holder>(DIF_UTIL) {

    companion object {
        val DIF_UTIL = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(
                oldItem: String,
                newItem: String,
            ) = oldItem == newItem

            override fun areContentsTheSame(
                oldItem: String,
                newItem: String,
            ) = oldItem == newItem
        }
    }

    class Holder(private val binding: SupportedDevicesChildItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(text: String) {
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            SupportedDevicesChildItemBinding.inflate(LayoutInflater.from(parent.context),
                parent,
                false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(holder.absoluteAdapterPosition))
    }
}