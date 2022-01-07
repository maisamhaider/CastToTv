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
import com.example.casttotv.models.Lang
import com.example.casttotv.utils.LOCALE_LANGUAGE
import com.example.casttotv.utils.MySingleton.resolveColorAttr
import com.example.casttotv.utils.MySingleton.setAppLocale
import com.example.casttotv.utils.Pref.getPrefs
import com.example.casttotv.utils.Pref.putPrefs

class LanguagesAdapter(
    private var context: Context,
) : ListAdapter<Lang, LanguagesAdapter.Holder>(DIF_UTIL) {

    companion object {
        val DIF_UTIL = object : DiffUtil.ItemCallback<Lang>() {
            override fun areItemsTheSame(
                oldItem: Lang,
                newItem: Lang,
            ): Boolean {
                return oldItem.code == newItem.code
            }

            override fun areContentsTheSame(
                oldItem: Lang,
                newItem: Lang,
            ): Boolean {
                return oldItem == newItem
            }

        }
    }

    class Holder(private val binding: LanguageItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(lang: Lang, context: Context) {
            binding.textview.text = lang.name
            binding.checkbox.isChecked = context.getPrefs(LOCALE_LANGUAGE, "en") == lang.code



            if (context.getPrefs(LOCALE_LANGUAGE, "en") == lang.code) {
                binding.textview.setTextColor(ContextCompat.getColor(context,
                    R.color.cr_dodger_blue_light_2))
            } else {
                @ColorInt val color = context.resolveColorAttr(R.attr.attr_lblack_dwhite_80)
                binding.textview.setTextColor(color)
            }
        }

        fun click(click: (Lang) -> Unit, lang: Lang, context: Context) {
            bind(lang, context)
            binding.checkbox.setOnClickListener {
                click(lang)
            }
            binding.root.setOnClickListener {
                click(lang)
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

    private fun click(lang: Lang) {
        context.putPrefs(LOCALE_LANGUAGE, lang.code)
//        if (holder.absoluteAdapterPosition != -1) {
            holder.bind(getItem(holder.absoluteAdapterPosition), context)
            notifyDataSetChanged()
            ContextWrapper(context.setAppLocale(context.getPrefs(LOCALE_LANGUAGE, "en")))
//        }


    }

}