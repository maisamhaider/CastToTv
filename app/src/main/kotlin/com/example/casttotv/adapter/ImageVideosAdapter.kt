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
import com.example.casttotv.databinding.ImagesVideosItemBinding
import com.example.casttotv.models.FileModel
import com.example.casttotv.utils.IMAGE
import com.example.casttotv.utils.VIDEO

class ImageVideosAdapter(
    val onItemClicked: (FileModel, Int) -> Unit,
    private var context: Context,
    private val mimType: String,
) :
    ListAdapter<FileModel, ImageVideosAdapter.Holder>(DIF_UTIL) {

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

    class Holder(private val binding: ImagesVideosItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(context: Context, fileModel: FileModel, mimType: String) {
            when (mimType) {
                VIDEO -> {
                    binding.mTextView.visibility = View.GONE
                    binding.imageViewPlay.visibility = View.VISIBLE
                    Glide.with(context).load(fileModel.filePath).into(binding.imageView)
                }
                IMAGE -> {
                    binding.imageViewPlay.visibility = View.GONE
                    binding.mTextView.visibility = View.GONE
                    Glide.with(context).load(fileModel.filePath).into(binding.imageView)
                }
                else -> {
                    binding.mTextView.text = fileModel.fileName
                    Glide.with(context).load(R.drawable.ic_launcher_background)
                        .into(binding.imageView)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val viewHolder = Holder(
            ImagesVideosItemBinding.inflate(
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

        holder.bind(context, getItem(holder.absoluteAdapterPosition), mimType)
    }

}