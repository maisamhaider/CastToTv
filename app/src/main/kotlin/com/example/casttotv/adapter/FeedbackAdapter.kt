package com.example.casttotv.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.casttotv.R
import com.example.casttotv.databinding.FeedbackItemBinding
import com.example.casttotv.models.Feedback
import com.example.casttotv.utils.Pref.getPrefs
import com.example.casttotv.utils.THEME_DARK


class FeedbackAdapter(val context: Context, val onItemClicked: (Feedback) -> Unit) :
    ListAdapter<Feedback, FeedbackAdapter.Holder>(DIF_UTIL) {

    companion object {
        val DIF_UTIL = object : DiffUtil.ItemCallback<Feedback>() {
            override fun areItemsTheSame(
                oldItem: Feedback,
                newItem: Feedback,
            ): Boolean {
                return oldItem.uri == newItem.uri
            }

            override fun areContentsTheSame(
                oldItem: Feedback,
                newItem: Feedback,
            ): Boolean {
                return oldItem == newItem
            }

        }
    }

    class Holder(private val binding: FeedbackItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(context: Context, feedback: Feedback, int: Int) {
            when {
                int == 0 -> {
                    binding.cvAdd.visibility = View.VISIBLE
                    val dark = context.getPrefs(THEME_DARK, false)

                    if (dark) {
                        binding.imageviewAdd.setImageResource(R.drawable.ic_add_dark)
                    } else {
                        binding.imageviewAdd.setImageResource(R.drawable.ic_add_light)
                    }
                }
                feedback.uri == null -> {
                    binding.container.background =
                        ContextCompat.getDrawable(context, R.drawable.shape_stroke_doted)
                }
                else -> {
                    binding.container.background =
                        ContextCompat.getDrawable(context,
                            R.drawable.shape_background_feedback_images)
                    Glide.with(context).load(feedback.uri).into(binding.imageView)

                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val viewHolder = Holder(
            FeedbackItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
        viewHolder.itemView.setOnClickListener {
            if (viewHolder.absoluteAdapterPosition == 0) {
                onItemClicked(getItem(viewHolder.absoluteAdapterPosition))
            }
        }

        return viewHolder
    }


    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(context,
            getItem(holder.absoluteAdapterPosition),
            holder.absoluteAdapterPosition)
    }
}
