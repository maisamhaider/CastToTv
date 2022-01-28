package com.example.casttotv.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.casttotv.databinding.SelectedImagesItemBinding
import com.example.casttotv.dataclasses.FileModel

class SelectedImagesAdapter(
    val onItemClicked: (FileModel) -> Unit, private var context: Context,
) : ListAdapter<FileModel, SelectedImagesAdapter.Holder>(DIF_UTIL) {

    companion object {
        val DIF_UTIL = object : DiffUtil.ItemCallback<FileModel>() {
            override fun areItemsTheSame(oldItem: FileModel, newItem: FileModel) =
                oldItem.filePath == newItem.filePath

            override fun areContentsTheSame(oldItem: FileModel, newItem: FileModel) =
                oldItem == newItem
        }
    }

    class Holder(val binding: SelectedImagesItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(context: Context, fileModel: FileModel) {
            Glide.with(context).load(fileModel.filePath).into(binding.imageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val viewHolder = Holder(SelectedImagesItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)
        )

        viewHolder.binding.imageviewRemove.setOnClickListener {
            onItemClicked(getItem(viewHolder.absoluteAdapterPosition))
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(context, getItem(holder.absoluteAdapterPosition))
    }
}