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
import com.example.casttotv.databinding.ImagesItem2Binding
import com.example.casttotv.dataclasses.FileModel

class ImageHorizonAdapter2(
    val onItemClicked: (FileModel, Int) -> Unit,
     private val isSlider: Boolean,
) : ListAdapter<FileModel, ImageHorizonAdapter2.Holder>(DIF_UTIL) {
    private var selected = 0

    companion object {
        val DIF_UTIL = object : DiffUtil.ItemCallback<FileModel>() {
            override fun areItemsTheSame(oldItem: FileModel, newItem: FileModel): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: FileModel, newItem: FileModel): Boolean {
                return oldItem == newItem
            }

        }
    }

    class Holder(private val binding: ImagesItem2Binding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(fileModel: FileModel, isSlider: Boolean) {
            Glide.with(itemView).load(fileModel.filePath).into(binding.imageView)
            if (isSlider) {
                binding.imageviewSelect.setImageResource(R.drawable.ic_eye)
            } else {
                binding.imageviewSelect.setImageResource(R.drawable.ic_tick)
            }
        }

        fun setBack(boolean: Boolean) {
            if (boolean) {
                binding.clSelect.visibility = View.VISIBLE
            } else {
                binding.clSelect.visibility = View.GONE
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val viewHolder = Holder(
            ImagesItem2Binding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
        viewHolder.itemView.setOnClickListener {
            onItemClicked(
                getItem(viewHolder.absoluteAdapterPosition),
                viewHolder.absoluteAdapterPosition
            )
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(holder.absoluteAdapterPosition), isSlider)
        holder.setBack(holder.absoluteAdapterPosition == selected)


    }

    fun setSelect(selected: Int) {
        this.selected = selected
    }

}