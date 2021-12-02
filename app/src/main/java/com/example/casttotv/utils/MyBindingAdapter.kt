package com.example.casttotv.utils

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.casttotv.adapter.FolderAdapter
import com.example.casttotv.adapter.SelectedImagesAdapter
import com.example.casttotv.models.FileModel
import com.example.casttotv.models.FolderModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


@BindingAdapter("dataList")
fun setList(recyclerView: RecyclerView, list: Flow<List<FolderModel>>) {
    try {
        val adapter = recyclerView.adapter as FolderAdapter
        CoroutineScope(Dispatchers.IO).launch {
            list.collect {
                launch(Dispatchers.Main) {
                    adapter.submitList(it)
                }
            }
        }
    } catch (e: Exception) {
        e.stackTrace
    }

}

@BindingAdapter("sliderImagesList")
fun sliderImagesList(recyclerView: RecyclerView, list: List<FileModel>?) {
    try {
        val adapter = recyclerView.adapter as SelectedImagesAdapter
        CoroutineScope(Dispatchers.Main).launch {
            adapter.submitList(list)
            adapter.notifyDataSetChanged()
        }
    } catch (e: Exception) {
        e.stackTrace
    }

}