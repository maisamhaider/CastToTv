package com.example.casttotv.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.casttotv.R
import com.example.casttotv.databinding.ImagesVideosItemBinding
import com.example.casttotv.dataclasses.FileModel
import com.example.casttotv.utils.IMAGE
import com.example.casttotv.utils.SLIDE
import com.example.casttotv.utils.VIDEO
import com.example.casttotv.viewmodel.SharedViewModel

class ImageVideosAdapter(
    val onItemClicked: (FileModel, Int) -> Unit,
    private val mimType: String,
) :
    ListAdapter<FileModel, ImageVideosAdapter.Holder>(DIF_UTIL) {
    private val sliderImageList: MutableList<FileModel> = ArrayList()
    lateinit var sharedViewModel: SharedViewModel

    companion object {
        val DIF_UTIL = object : DiffUtil.ItemCallback<FileModel>() {
            override fun areItemsTheSame(oldItem: FileModel, newItem: FileModel) =
                oldItem == newItem

            override fun areContentsTheSame(oldItem: FileModel, newItem: FileModel) =
                oldItem == newItem
        }
    }

    class Holder(private val binding: ImagesVideosItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            fileModel: FileModel,
            mimType: String,
            sliderImageList: MutableList<FileModel>,
            onItemClicked: (FileModel, Int) -> Unit,
        ) {
            when (mimType) {
                VIDEO -> {
                    binding.imageViewPlay.visibility = View.VISIBLE
                    Glide.with(itemView).load(fileModel.filePath).into(binding.imageView)
                }
                IMAGE -> {
                    binding.imageViewPlay.visibility = View.GONE
                    Glide.with(itemView).load(fileModel.filePath).into(binding.imageView)
                }
                SLIDE -> {
                    binding.checkBox.visibility = View.VISIBLE
                    binding.checkBox.isChecked = sliderImageList.contains(fileModel)
                    binding.imageViewPlay.visibility = View.GONE
                    Glide.with(itemView).load(fileModel.filePath).into(binding.imageView)
                    binding.checkBox.setOnClickListener {
                        onItemClicked(fileModel, absoluteAdapterPosition)
                    }
                }
                else -> {
                    Glide.with(itemView).load(R.drawable.ic_launcher_background)
                        .into(binding.imageView)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val viewHolder = Holder(
            ImagesVideosItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ))
        viewHolder.itemView.setOnClickListener {
            if (mimType == SLIDE) {
                onLocalItemClicked(getItem(viewHolder.absoluteAdapterPosition),
                    viewHolder.absoluteAdapterPosition)
            } else {
                onItemClicked(
                    getItem(viewHolder.absoluteAdapterPosition),
                    viewHolder.absoluteAdapterPosition
                )
            }
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(
            getItem(holder.absoluteAdapterPosition),
            mimType,
            sliderImageList,
            ::onLocalItemClicked)
    }

    fun setVM(sharedViewModel: SharedViewModel) {
        this.sharedViewModel = sharedViewModel
    }

    private fun onLocalItemClicked(fileModel: FileModel, int: Int) {
        if (!sliderImageList.contains(fileModel)) {
            sliderImageList.add(0, fileModel)
            sharedViewModel.selectImages(sliderImageList)
        } else {
            sliderImageList.remove(fileModel)
            sharedViewModel.selectImages(sliderImageList)
        }
        notifyItemChanged(int)
    }
}