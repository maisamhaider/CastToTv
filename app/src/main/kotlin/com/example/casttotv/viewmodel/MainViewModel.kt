package com.example.casttotv.viewmodel

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.casttotv.models.Lang
import com.example.casttotv.utils.MySingleton


class MainViewModel : ViewModel() {

    private val viewLanguage: MutableLiveData<View> = MutableLiveData()

    fun setView(view: View) {
        viewLanguage.value = view
    }

    fun viewVisibility(visible: Boolean) {
        if (!visible) viewLanguage.value?.visibility =
            View.GONE else viewLanguage.value?.visibility = View.VISIBLE
    }

    fun list(): MutableList<Lang> {
        return MySingleton.listOfLanguages
    }
}