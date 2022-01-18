package com.example.casttotv.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
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

        @SuppressLint("SetJavaScriptEnabled")
        fun bind(context: Context, tab: Tabs, browserVm: BrowserViewModel) {
            binding.apply {
                textview.text = tab.webView.title ?: "empty"
                Glide.with(context).load(tab.webView.url).placeholder(R.drawable.ic_browser)
                    .into(imageviewTabSiteLogo)

                val dimen144dp: Int =
                    context.resources.getDimensionPixelSize(R.dimen.layout_width_144dp)
                val dimen108dp: Int =
                    context.resources.getDimensionPixelSize(R.dimen.layout_height_108dp)

                val width: Float =
                    context.resources.getDimensionPixelSize(R.dimen.layout_width_144dp).toFloat()
                val height: Float =
                    context.resources.getDimensionPixelSize(R.dimen.layout_height_108dp).toFloat()

                tab.webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)

                val sBitmap = Bitmap.createBitmap(dimen144dp, dimen108dp, Bitmap.Config.RGB_565)

                val sCanvas = Canvas(sBitmap)

                val left: Int = tab.webView.left
                val top: Int = tab.webView.top
                val status = sCanvas.save()
                sCanvas.translate(-left.toFloat(), -top.toFloat())

                val scale: Float = width / tab.webView.width
                val scale2: Float = height / tab.webView.height
                sCanvas.scale(scale, scale, scale2, scale2)

                tab.webView.draw(sCanvas)
                sCanvas.restoreToCount(status)

                imageview.setImageBitmap(sBitmap)

                if (browserVm._currentTabIndex == absoluteAdapterPosition) {
                    cl.setBackgroundResource(R.drawable.selected_tab_bg)
                    textview.setTextColor(ContextCompat.getColor(context, R.color.cr_white))
                    imageviewRemove.setColorFilter(ContextCompat.getColor(context,
                        R.color.cr_white))
                }
                imageviewRemoveClick.setOnClickListener {
                    browserVm.closTabDialog(absoluteAdapterPosition)
                }
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