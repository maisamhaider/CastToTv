package com.example.casttotv.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.casttotv.databinding.ItemMoreAppsBinding
import com.example.casttotv.dataclasses.ModelMoreApps

class MoreAppsAdapter(private var context: Context) :
    ListAdapter<ModelMoreApps, MoreAppsAdapter.Holder>(DIF_UTIL) {

    companion object {
        val DIF_UTIL = object : DiffUtil.ItemCallback<ModelMoreApps>() {
            override fun areItemsTheSame(
                oldItem: ModelMoreApps,
                newItem: ModelMoreApps,
            ) = oldItem.app_package == newItem.app_package

            override fun areContentsTheSame(
                oldItem: ModelMoreApps,
                newItem: ModelMoreApps,
            ) = oldItem == newItem
        }
    }

    class Holder(private val binding: ItemMoreAppsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(modelMoreApps: ModelMoreApps) {
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            ItemMoreAppsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(holder.absoluteAdapterPosition))
    }
}