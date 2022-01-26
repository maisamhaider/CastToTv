package com.example.casttotv.adapter


import android.content.Context
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isNotEmpty
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.casttotv.R
import com.example.casttotv.databinding.LayoutContainerItemBinding
import com.example.casttotv.utils.MySingleton.toastLong
import com.example.casttotv.utils.Pref.getPrefs
import com.example.casttotv.utils.THEME_DARK


class TutorialPagerAdapter(private var context: Context) :
    ListAdapter<View, TutorialPagerAdapter.Holder>(DIF_UTIL) {

    companion object {
        val DIF_UTIL = object : DiffUtil.ItemCallback<View>() {
            override fun areItemsTheSame(oldItem: View, newItem: View): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: View, newItem: View): Boolean {
                return oldItem.id == newItem.id
            }

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
                    val textView = view.rootView.findViewById<TextView>(R.id.textview_body)
                    if (dark) {
                        imageView.setImageResource(R.drawable.ic_support_dark)
                    } else {
                        imageView.setImageResource(R.drawable.ic_support_light)
                    }
                    onContactClick(context, textView)

                }
            }
        }

        private fun onContactClick(context: Context, textView: TextView) {
//            val text: String = context.getString(R.string.tutorial_support_body)

//            val spannableString = SpannableString(text)

//            val notUnderlineSpanAndClick: ClickableSpan = object : ClickableSpan() {
//                override fun onClick(widget: View) {
//                    context.toastLong("clicked")
//
//                }
//
//                override fun updateDrawState(ds: TextPaint) { // override updateDrawState
//                    ds.isUnderlineText = false // set to false to remove underline
//                }
//
//            }


//            val color =
//                ForegroundColorSpan(ContextCompat.getColor(context, R.color.cr_dodger_blue_light_2))
//            spannableString.setSpan(color, 166, 177, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
//            spannableString.setSpan(notUnderlineSpanAndClick,
//                166,
//                177,
//                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
//            textView.text = spannableString
//            textView.movementMethod = LinkMovementMethod.getInstance()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            LayoutContainerItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(context, getItem(holder.absoluteAdapterPosition))
    }

}