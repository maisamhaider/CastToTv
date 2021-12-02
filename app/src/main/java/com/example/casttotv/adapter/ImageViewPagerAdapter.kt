package com.example.casttotv.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.casttotv.R
import com.example.casttotv.databinding.ImageSlidingBinding
import com.example.casttotv.models.FileModel


class ImageViewPagerAdapter(
    private var context: Context,
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

            val animSlideDown: Animation =
                AnimationUtils.loadAnimation(context.applicationContext, R.anim.slide_down)
            binding.imageView.startAnimation(animSlideDown)

        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            ImageSlidingBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(context, getItem(holder.absoluteAdapterPosition))
    }

}