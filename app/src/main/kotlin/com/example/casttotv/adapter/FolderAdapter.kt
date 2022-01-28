package com.example.casttotv.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.casttotv.R
import com.example.casttotv.databinding.FolderItemBinding
import com.example.casttotv.dataclasses.FolderModel
import com.example.casttotv.utils.IMAGE
import com.example.casttotv.utils.VIDEO

class FolderAdapter(
    val onItemClicked: (FolderModel) -> Unit,
    private var context: Context,
    private val mimType: String,
) : ListAdapter<FolderModel, FolderAdapter.Holder>(DIF_UTIL) {

    companion object {
        val DIF_UTIL = object : DiffUtil.ItemCallback<FolderModel>() {
            override fun areItemsTheSame(oldItem: FolderModel, newItem: FolderModel) =
                oldItem.folderPath == newItem.folderPath

            override fun areContentsTheSame(oldItem: FolderModel, newItem: FolderModel) =
                oldItem == newItem
        }
    }

    class Holder(private val binding: FolderItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(context: Context, folderModel: FolderModel, mimType: String) {

            when (mimType) {
                VIDEO -> {
                    Glide.with(context).load(folderModel.filePath).into(binding.imageView)
                }
                IMAGE -> {
                    Glide.with(context).load(folderModel.filePath).into(binding.imageView)
                }
                else -> {
                    Glide.with(context).load(R.drawable.ic_launcher_background)
                        .into(binding.imageView)
                }
            }
            binding.mTextView.text = folderModel.folderName
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val viewHolder = Holder(
            FolderItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
        viewHolder.itemView.setOnClickListener {
            onItemClicked(getItem(viewHolder.absoluteAdapterPosition))
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(context, getItem(holder.absoluteAdapterPosition), mimType)
    }

}