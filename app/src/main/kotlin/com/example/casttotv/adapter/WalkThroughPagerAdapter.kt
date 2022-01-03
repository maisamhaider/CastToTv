package com.example.casttotv.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isNotEmpty
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.casttotv.databinding.LayoutContainerItemBinding


class WalkThroughPagerAdapter(private var context: Context) :
    ListAdapter<View, WalkThroughPagerAdapter.Holder>(DIF_UTIL) {

    companion object {
        val DIF_UTIL = object : DiffUtil.ItemCallback<View>() {
            override fun areItemsTheSame(oldItem: View, newItem: View): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: View, newItem: View): Boolean {
                return oldItem.id == newItem.id
            }

        }
    }

    class Holder(private val binding: LayoutContainerItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(context: Context, view: View) {
            if (binding.tutorialViewContainer.isNotEmpty()) {
                binding.tutorialViewContainer.removeView(view)
            }
            binding.tutorialViewContainer.addView(view)
        }


    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {

        return Holder(
            LayoutContainerItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(context, getItem(holder.absoluteAdapterPosition))
    }

}