package com.example.casttotv.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.casttotv.R


class TutorialViewModel(context: Context) : ViewModel() {

    private val _nextButtonText: MutableLiveData<String> =
        MutableLiveData(context.getString(R.string.next))
    val nextButtonText: LiveData<String> = _nextButtonText

    fun nextButtonProperties(string: String) {
        _nextButtonText.value = string
    }

    fun nextOrFinished(position: Int, viewPager2: ViewPager2) {
        viewPager2.setCurrentItem(position.plus(1), true)

    }


    class TutorialViewModelFactory(private val context: Context) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TutorialViewModel::class.java)) {
                return TutorialViewModel(context = context) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

}