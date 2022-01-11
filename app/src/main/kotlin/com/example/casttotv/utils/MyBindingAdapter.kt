package com.example.casttotv.utils

import android.widget.CheckBox
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.asFlow
import androidx.recyclerview.widget.RecyclerView
import com.example.casttotv.adapter.FolderAdapter
import com.example.casttotv.adapter.SelectedImagesAdapter
import com.example.casttotv.models.FileModel
import com.example.casttotv.models.FolderModel
import com.example.casttotv.viewmodel.BrowserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


@BindingAdapter("dataList")
fun setList(recyclerView: RecyclerView, list: LiveData<List<FolderModel>>) {
    try {
        val adapter = recyclerView.adapter as FolderAdapter
        CoroutineScope(Dispatchers.IO).launch {
            list.asFlow().collect {
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





