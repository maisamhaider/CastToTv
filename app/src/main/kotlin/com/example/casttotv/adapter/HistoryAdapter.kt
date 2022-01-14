package com.example.casttotv.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.casttotv.R
import com.example.casttotv.database.entities.HistoryEntity
import com.example.casttotv.databinding.HistoryItemBinding
import com.example.casttotv.dataclasses.History
import com.example.casttotv.interfaces.OptionMenuListener
import java.text.SimpleDateFormat
import java.util.*

class HistoryAdapter(
    private val onClick: (HistoryEntity) -> Unit,
    private var context: Context,
    private val optionMenuListener: OptionMenuListener,
) : ListAdapter<History, HistoryAdapter.Holder>(DIF_UTIL) {

    companion object {
        val DIF_UTIL = object : DiffUtil.ItemCallback<History>() {
            override fun areItemsTheSame(
                oldItem: History,
                newItem: History,
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: History,
                newItem: History,
            ): Boolean {
                return oldItem == newItem
            }

        }
    }

    class Holder(
        private val binding: HistoryItemBinding,
        private val optionMenuListener: OptionMenuListener,
        private val onClick: (HistoryEntity) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root),
        OptionMenuListener {
        fun bind(context: Context, history: History) {
            if (history.date == today()) {
                binding.textViewText.text = context.getString(R.string.today)
            } else {
                binding.textViewText.text = history.date
            }
            val adapter = HistoryGroupsAdapter(::onClickLocal, context, this)
            binding.recyclerView.adapter = adapter
            adapter.submitList(history.list)
//            binding.textViewDate.text = historyEntity.getDate()
        }

        fun today(): String {
            val sFormat = SimpleDateFormat("DD.MM.yyyy")
            sFormat.isLenient = false

            return sFormat.format(Date())
        }

        fun onClickLocal(item: HistoryEntity) {
            onClick(item)
        }

        override fun <T> item(itemId: Int, dataClass: T) {
            val historyEntity: HistoryEntity = dataClass as HistoryEntity
            optionMenuListener.item(itemId, historyEntity)
        }

    }

    fun onClickLocal(item: HistoryEntity) {
        onClick(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {

        return Holder(
            HistoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            optionMenuListener, ::onClickLocal)
    }


    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(context, getItem(holder.absoluteAdapterPosition))
    }


}