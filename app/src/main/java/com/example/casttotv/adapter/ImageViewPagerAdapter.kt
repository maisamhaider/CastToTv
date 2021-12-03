package com.example.casttotv.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.casttotv.databinding.ImageSlidingBinding
import com.example.casttotv.models.FileModel


class ImageViewPagerAdapter(
    private var context: Context, private val onItemClick: () -> Unit,
) : ListAdapter<FileModel, ImageViewPagerAdapter.Holder>(DIF_UTIL) {

    companion object {
        val DIF_UTIL = object : DiffUtil.ItemCallback<FileModel>() {
            override fun areItemsTheSame(oldItem: FileModel, newItem: FileModel): Boolean {
                return oldItem.filePath == newItem.filePath
            }

            override fun areContentsTheSame(oldItem: FileModel, newItem: FileModel): Boolean {
                return oldItem == newItem
            }

        }
    }

    class Holder(private val binding: ImageSlidingBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(context: Context, fileModel: FileModel) {
            Glide.with(context).load(fileModel.filePath).into(binding.imageView)
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
        holder.bind(context, getItem(holder.absoluteAdapterPosition))
    }

}