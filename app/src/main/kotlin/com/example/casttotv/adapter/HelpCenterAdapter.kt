package com.example.casttotv.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.casttotv.databinding.HelpCenterItemBinding
import com.example.casttotv.models.QuestionAnswer

val visibilityList: MutableList<Pair<Int, Boolean>> = ArrayList()

class HelpCenterAdapter: ListAdapter<QuestionAnswer, HelpCenterAdapter.Holder>(DIF_UTIL) {

    companion object {
        val DIF_UTIL = object : DiffUtil.ItemCallback<QuestionAnswer>() {
            override fun areItemsTheSame(
                oldItem: QuestionAnswer,
                newItem: QuestionAnswer,
            ): Boolean {
                return oldItem.question == newItem.question
            }

            override fun areContentsTheSame(
                oldItem: QuestionAnswer,
                newItem: QuestionAnswer,
            ): Boolean {
                return oldItem == newItem
            }

        }
    }

    class Holder(private val binding: HelpCenterItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(questionAnswer: QuestionAnswer) {
            binding.includeClickable.textviewQuestion.text = questionAnswer.question
            binding.includeClickable.viewColor.setBackgroundResource(questionAnswer.color)
            val adapter = HelpCenterChildAdapter()
            binding.includeRecyclerview.recyclerView.adapter = adapter
            questionAnswer.list.let { adapter.submitList(it) }
            visibility()
            binding.includeClickable.clItem1.setOnClickListener {
                viewVisibility()
            }
        }

        private fun viewVisibility() {
            if (!visibilityList.contains(absoluteAdapterPosition to true)) {
                binding.includeRecyclerview.clItem2.visibility = View.VISIBLE
                visibilityList.add(absoluteAdapterPosition to true)
                binding.includeClickable.imageViewArrow.rotation = 180f

            } else {
                binding.includeRecyclerview.clItem2.visibility = View.GONE
                visibilityList.remove(absoluteAdapterPosition to true)
                binding.includeClickable.imageViewArrow.rotation = 0f
            }
        }

        private fun visibility() {
            if (!visibilityList.contains(absoluteAdapterPosition to true)) {
                binding.includeRecyclerview.clItem2.visibility = View.GONE
                binding.includeClickable.imageViewArrow.rotation = 0f
            } else {
                binding.includeRecyclerview.clItem2.visibility = View.VISIBLE
                binding.includeClickable.imageViewArrow.rotation = 180f
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            HelpCenterItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }


    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(holder.absoluteAdapterPosition))
    }

}