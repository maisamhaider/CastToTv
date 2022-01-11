package com.example.casttotv.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.PopupWindow
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.casttotv.R
import com.example.casttotv.database.entities.BookmarkEntity
import com.example.casttotv.databinding.BookmarkItemBinding
import com.example.casttotv.databinding.PopUpMenuBinding


class BookmarkAdapter(
    val onItemClicked: (BookmarkEntity, Boolean) -> Unit,
    private var context: Context,
) : ListAdapter<BookmarkEntity, BookmarkAdapter.Holder>(DIF_UTIL) {

    companion object {
        val DIF_UTIL = object : DiffUtil.ItemCallback<BookmarkEntity>() {
            override fun areItemsTheSame(
                oldItem: BookmarkEntity,
                newItem: BookmarkEntity,
            ): Boolean {
                return oldItem.date == newItem.date
            }

            override fun areContentsTheSame(
                oldItem: BookmarkEntity,
                newItem: BookmarkEntity,
            ): Boolean {
                return oldItem == newItem
            }

        }
    }

    class Holder(private val binding: BookmarkItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(context: Context, bookmarkEntity: BookmarkEntity) {
            binding.textViewText.text = bookmarkEntity.title
            binding.textViewTextUrl.text = bookmarkEntity.link
            Glide.with(context).load("www.google.com")
                .placeholder(R.drawable.ic_browser)
                .into(binding.imageview)

            binding.viewMenuClick.setOnClickListener {
                 val window = PopupWindow(context)
                // inflate your layout or dynamically add view
                val bindingView = PopUpMenuBinding.inflate(LayoutInflater.from(context), null, false)

                window.width = WindowManager.LayoutParams.WRAP_CONTENT
                window.height = WindowManager.LayoutParams.WRAP_CONTENT
                window.isFocusable = true
                window.contentView = bindingView.root
//            window.contentView = view
                window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                window.showAsDropDown(binding.viewMenuClick)

                val popup = PopupMenu(context, binding.viewMenuClick)
                popup.inflate(R.menu.options_menu)
                popup.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.item_edit ->                         //handle menu1 click
                            true
//                        com.example.casttotv.R.id.menu2 ->                         //handle menu2 click
//                            true
//                        com.example.casttotv.R.id.menu3 ->                         //handle menu3 click
//                            true
                        else -> false
                    }
                }
                //displaying the popup
                //displaying the popup

//                popup.show()
            }
        }




    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val viewHolder = Holder(
            BookmarkItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
        viewHolder.itemView.setOnClickListener {
            onItemClicked(getItem(viewHolder.absoluteAdapterPosition), false)
        }
        viewHolder.itemView.setOnLongClickListener {
            onItemClicked(getItem(viewHolder.absoluteAdapterPosition), true)
            true
        }


        return viewHolder
    }


    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(context, getItem(holder.absoluteAdapterPosition))
    }

}