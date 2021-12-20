package com.example.casttotv.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.casttotv.databinding.TabItemBinding
import com.example.casttotv.models.Tabs
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
        fun bind(tab: Tabs, browserVm: BrowserViewModel) {
            binding.mTextView.text = tab.webView.title ?: "empty"
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
        holder.bind(getItem(holder.absoluteAdapterPosition), browserVm)
    }

}