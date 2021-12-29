package com.example.casttotv.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.casttotv.databinding.SupportedDevicesItemBinding

class SupportedDevicesAdapter : ListAdapter<String, SupportedDevicesAdapter.Holder>(DIF_UTIL) {

    companion object {
        val DIF_UTIL = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(
                oldItem: String,
                newItem: String,
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: String,
                newItem: String,
            ): Boolean {
                return oldItem == newItem
            }

        }
    }

    class Holder(private val binding: SupportedDevicesItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(bookmarkEntity: String) {
//            binding.textViewText.text = bookmarkEntity.title
            val adapter = SupportedDevicesChildAdapter()
            binding.recyclerView.adapter = adapter
            listOf("1").let { adapter.submitList(it) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            SupportedDevicesItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }


    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(holder.absoluteAdapterPosition))
    }

}