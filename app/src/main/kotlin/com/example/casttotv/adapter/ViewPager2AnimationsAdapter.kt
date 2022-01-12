package com.example.casttotv.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.casttotv.R
import com.example.casttotv.databinding.ViewPager2AnimationItemBinding
import com.example.casttotv.dataclasses.PagerAnimation

class ViewPager2AnimationsAdapter(private val onItemClick: (PagerAnimation?, Int) -> Unit) :
    ListAdapter<PagerAnimation, ViewPager2AnimationsAdapter.Holder>(DIF_UTIL) {


    companion object {
        val DIF_UTIL = object : DiffUtil.ItemCallback<PagerAnimation>() {
            override fun areItemsTheSame(
                oldItem: PagerAnimation,
                newItem: PagerAnimation,
            ): Boolean {
                return oldItem.int == newItem.int
            }

            override fun areContentsTheSame(
                oldItem: PagerAnimation,
                newItem: PagerAnimation,
            ): Boolean {
                return oldItem == newItem
            }

        }
    }

    class Holder(private val binding: ViewPager2AnimationItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(animation: PagerAnimation) {
            binding.materialTextView.text = animation.int.toString()
        }

        fun setBack(b: Boolean) {
            if (b) {
                binding.imageviewSelect.setImageResource(R.drawable.ic_checked)
            } else {
                binding.imageviewSelect.setImageBitmap(null)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {

        val viewHolder =
            Holder(ViewPager2AnimationItemBinding.inflate(LayoutInflater.from(parent.context),
                parent,
                false))
        viewHolder.itemView.setOnClickListener {
            onItemClick(getItem(viewHolder.absoluteAdapterPosition),
                viewHolder.absoluteAdapterPosition)
        }

        return viewHolder
    }

    var selected = 0
    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(holder.absoluteAdapterPosition))
        holder.setBack(holder.absoluteAdapterPosition == selected)

    }
    fun setSelect(selected: Int) {
        this.selected = selected
    }
}