package com.example.casttotv.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.casttotv.R
import com.example.casttotv.databinding.TabItemBinding
import com.example.casttotv.dataclasses.Tabs
import com.example.casttotv.viewmodel.BrowserViewModel

class TabsAdapter(
    val onItemClick: (int: Int) -> Unit,
    val browserVm: BrowserViewModel,
    private var context: Context,
) :
    ListAdapter<Tabs, TabsAdapter.Holder>(DIF_UTIL) {

    companion object {
        val DIF_UTIL = object : DiffUtil.ItemCallback<Tabs>() {
            override fun areItemsTheSame(oldItem: Tabs, newItem: Tabs): Boolean {
                return oldItem.webView.id == newItem.webView.id
            }

            override fun areContentsTheSame(oldItem: Tabs, newItem: Tabs): Boolean {
                return oldItem == newItem
            }

        }
    }

    class Holder(private val binding: TabItemBinding) : RecyclerView.ViewHolder(binding.root) {
        var i = 0
        fun bind(context: Context, tab: Tabs, browserVm: BrowserViewModel) {
            binding.textview.text = tab.webView.title ?: "empty"
            Glide.with(context).load(tab.webView.url).placeholder(R.drawable.ic_browser)
                .into(binding.imageviewTabSiteLogo)
            i = i.inc()
            binding.imageviewRemove.setOnClickListener {
                browserVm.closTabDialog(this.absoluteAdapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val viewHolder = Holder(
            TabItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
        viewHolder.itemView.setOnClickListener {
            onItemClick(viewHolder.absoluteAdapterPosition)
        }
        return viewHolder
    }


    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(context, getItem(holder.absoluteAdapterPosition), browserVm)
    }

}