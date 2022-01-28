package com.example.casttotv.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.isNotEmpty
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.casttotv.R
import com.example.casttotv.databinding.LayoutContainerItemBinding
import com.example.casttotv.utils.Pref.getPrefs
import com.example.casttotv.utils.THEME_DARK


class TutorialPagerAdapter(private var context: Context) :
    ListAdapter<View, TutorialPagerAdapter.Holder>(DIF_UTIL) {

    companion object {
        val DIF_UTIL = object : DiffUtil.ItemCallback<View>() {
            override fun areItemsTheSame(oldItem: View, newItem: View) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: View, newItem: View) = oldItem.id == newItem.id
        }
    }

    class Holder(private val binding: LayoutContainerItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(context: Context, view: View) {
            if (binding.tutorialViewContainer.isNotEmpty()) {
                binding.tutorialViewContainer.removeView(view)
            }
            binding.tutorialViewContainer.addView(view)
            val dark = context.getPrefs(THEME_DARK, false)

            when (absoluteAdapterPosition) {
                3 -> {
                    val imageView = view.rootView.findViewById<ImageView>(R.id.image_view)
                    if (dark) {
                        imageView.setImageResource(R.drawable.ic_scan_code_dark)
                    } else {
                        imageView.setImageResource(R.drawable.ic_scan_code_light)
                    }
                }
                4 -> {
                    val imageView = view.rootView.findViewById<ImageView>(R.id.image_view)
                    if (dark) {
                        imageView.setImageResource(R.drawable.ic_support_dark)
                    } else {
                        imageView.setImageResource(R.drawable.ic_support_light)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            LayoutContainerItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(context, getItem(holder.absoluteAdapterPosition))
    }
}