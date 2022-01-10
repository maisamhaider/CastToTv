package com.example.casttotv.adapter

import android.content.Context
import android.content.ContextWrapper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.casttotv.R
import com.example.casttotv.databinding.LanguageItemBinding
import com.example.casttotv.models.SearchEngine
import com.example.casttotv.utils.MySingleton.resolveColorAttr
import com.example.casttotv.utils.MySingleton.setAppLocale
import com.example.casttotv.utils.Pref.getPrefs
import com.example.casttotv.utils.Pref.putPrefs
import com.example.casttotv.utils.SELECTED_ENGINE

class SearchEngineAdapter(private var context: Context) :
    ListAdapter<SearchEngine, SearchEngineAdapter.Holder>(DIF_UTIL) {

    companion object {
        val DIF_UTIL = object : DiffUtil.ItemCallback<SearchEngine>() {
            override fun areItemsTheSame(
                oldItem: SearchEngine,
                newItem: SearchEngine,
            ): Boolean {
                return oldItem.name == newItem.name
            }

            override fun areContentsTheSame(
                oldItem: SearchEngine,
                newItem: SearchEngine,
            ): Boolean {
                return oldItem == newItem
            }

        }
    }

    class Holder(private val binding: LanguageItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(searchEngine: SearchEngine, context: Context) {
            binding.textview.text = searchEngine.name
            binding.checkbox.isChecked =
                context.getPrefs(SELECTED_ENGINE, "google") == searchEngine.name.lowercase()



            if (context.getPrefs(SELECTED_ENGINE, "google") == searchEngine.name.lowercase()) {
                binding.textview.setTextColor(ContextCompat.getColor(context,
                    R.color.cr_dodger_blue_light_2))
            } else {
                @ColorInt val color = context.resolveColorAttr(R.attr.attr_lblack_dwhite_80)
                binding.textview.setTextColor(color)
            }
        }

        fun click(
            click: (SearchEngine, Holder) -> Unit,
            searchEngine: SearchEngine,
            context: Context,
        ) {
            bind(searchEngine, context)
            binding.checkbox.setOnClickListener {
                click(searchEngine, this)
            }
            binding.root.setOnClickListener {
                click(searchEngine, this)
            }
        }
    }

    lateinit var holder: Holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        holder = Holder(
            LanguageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

        return holder
    }


    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(holder.absoluteAdapterPosition), context)
        holder.click(::click, getItem(holder.absoluteAdapterPosition), context)

    }

    private fun click(searchEngine: SearchEngine, holder: Holder) {
        context.putPrefs(SELECTED_ENGINE, searchEngine.name.lowercase())
//        if (holder.absoluteAdapterPosition != -1) {
        holder.bind(getItem(holder.absoluteAdapterPosition), context)
        notifyDataSetChanged()
        ContextWrapper(context.setAppLocale(context.getPrefs(SELECTED_ENGINE, "google")))
//        }


    }

}