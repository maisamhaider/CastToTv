package com.example.casttotv.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.casttotv.databinding.HelpItemChildItemBinding

class HelpCenterChildAdapter : ListAdapter<String, HelpCenterChildAdapter.Holder>(DIF_UTIL) {

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

    class Holder(private val binding: HelpItemChildItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(answer: String) {
            binding.textview.text = answer
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            HelpItemChildItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(holder.absoluteAdapterPosition))
    }
}