package com.example.casttotv.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.casttotv.R
import com.example.casttotv.databinding.ImagesItem2Binding
import com.example.casttotv.models.FileModel

class ImageHorizonAdapter2(
    val onItemClicked: (FileModel, Int) -> Unit,
    private var context: Context,
    private val isVideo: Boolean,
) : ListAdapter<FileModel, ImageHorizonAdapter2.Holder>(DIF_UTIL) {

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
        fun bind(context: Context, fileModel: FileModel, isVideo: Boolean) {
            if (isVideo) {
                binding.imageViewPlay.visibility = View.VISIBLE
            }
            Glide.with(context).load(fileModel.filePath).into(binding.imageView)
        }

        fun setBack(context: Context, boolean: Boolean) {
            if (boolean) {
                binding.constraintLayout.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.black_50
                    )
                )
            } else {
                binding.constraintLayout.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.white
                    )
                )
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

    var selected = ""
    override fun onBindViewHolder(holder: Holder, position: Int) {

        holder.bind(context, getItem(holder.absoluteAdapterPosition), isVideo)
        holder.setBack(context, getItem(position).filePath == selected)


    }


}