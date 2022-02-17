package com.example.casttotv.viewmodel

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import androidx.lifecycle.*
import com.example.casttotv.dataclasses.FileModel
import com.example.casttotv.repositories.SharedRepository

class SharedViewModel(context: Context) : ViewModel() {
    private val sharedRepository = SharedRepository(context)

    val imagesFolderFlow = sharedRepository.imageFolderFlow.asLiveData()
    fun imagesByFolder(image: String) = sharedRepository.getImagesByBucket(image).asLiveData()

    val videosFoldersFlow = sharedRepository.videosFoldersFlow
    fun videosByBucket(video: String) = sharedRepository.videosByBucket(video)

    val audioFoldersFlow = sharedRepository.audioFoldersFlow
    fun audiosByBucket(audio: String) = sharedRepository.audiosByBucket(audio)


    fun pagerAnimations() = sharedRepository.pagerAnimations()

    private var _speed: MutableLiveData<Int> = MutableLiveData(100)
    private var _mTimeLeftInMillis: MutableLiveData<Long> = MutableLiveData(0)
    private var _playingVideoCurrentPos: MutableLiveData<Int> = MutableLiveData(0)
    private var _playingVideoCurrentPosBeforeDestroy: MutableLiveData<Int> = MutableLiveData(0)
    private var _wifiConnection: MutableLiveData<Boolean> = MutableLiveData(false)
    val wifiConnection: LiveData<Boolean> = _wifiConnection

    val speed: LiveData<Int> = _speed
    val mTimeLeftInMillis: LiveData<Long> = _mTimeLeftInMillis
    val playingVideoCurrentPos: LiveData<Int> = _playingVideoCurrentPos
    val playingVideoCurrentPosBeforeDestroy: LiveData<Int> = _playingVideoCurrentPosBeforeDestroy

    val speedX: LiveData<String> = Transformations.map(_speed) {
        String.format("%.1fx", it / 100f)
    }

    private var _selectedImages: MutableLiveData<MutableList<FileModel>> = MutableLiveData()

    val selectedImages: LiveData<MutableList<FileModel>> = _selectedImages

    private var _play: MutableLiveData<Boolean> = MutableLiveData(false)

    val play: LiveData<Boolean> = _play


    fun playPause() {
        _play.value = !_play.value!!
    }

    fun playPause(value: Boolean) {
        _play.value = value
    }

    fun adjustPlayerSpeed(value: Int) {
        _speed.value = value
    }

    fun selectImages(value: MutableList<FileModel>) {
        _selectedImages.value = value
    }


    fun setTimeLeftInMillis(time: Long) {
        _mTimeLeftInMillis.value = time
    }

    fun setPlayingVideoCurrentPos(time: Int) {
        _playingVideoCurrentPos.value = time
    }

    fun playingVideoCurrentPosBeforeDestroy(time: Int) {
        _playingVideoCurrentPosBeforeDestroy.value = time
    }

    fun orientation(context: Activity): Int {
        return context.resources.configuration.orientation
    }

    fun wifiCon(value: Boolean) {
        _wifiConnection.value = value
    }


    @SuppressLint("SourceLockedOrientationActivity")
    fun setOrientation(context: Activity) {
        if (orientation(context) == Configuration.ORIENTATION_LANDSCAPE) {
            context.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        } else {
            context.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
    }

    fun setActivityOrientation(activity: Activity) {
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
    }

    fun setActivityOrientationPot(activity: Activity) {
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    class SharedViewModelFactory(
        private val context: Context,
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SharedViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return SharedViewModel(context) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}