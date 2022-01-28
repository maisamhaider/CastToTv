package com.example.casttotv.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.casttotv.databinding.ImageSlidingBinding
import com.example.casttotv.dataclasses.FileModel


class ImageViewPagerAdapter(
    private val onItemClick: () -> Unit,
) : ListAdapter<FileModel, ImageViewPagerAdapter.Holder>(DIF_UTIL) {

    companion object {
        val DIF_UTIL = object : DiffUtil.ItemCallback<FileModel>() {
            override fun areItemsTheSame(oldItem: FileModel, newItem: FileModel) =
                oldItem.filePath == newItem.filePath

            override fun areContentsTheSame(oldItem: FileModel, newItem: FileModel) =
                oldItem == newItem
        }
    }

    class Holder(private val binding: ImageSlidingBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(fileModel: FileModel) {
            Glide.with(itemView).load(fileModel.filePath).into(binding.imageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = Holder(
            ImageSlidingBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
        view.itemView.setOnClickListener {
            onItemClick()
        }
        return view
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(holder.absoluteAdapterPosition))
    }
}