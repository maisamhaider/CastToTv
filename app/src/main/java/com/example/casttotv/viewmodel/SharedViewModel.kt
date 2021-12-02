package com.example.casttotv.viewmodel

import android.content.Context
import androidx.lifecycle.*
import com.example.casttotv.models.FileModel
import com.example.casttotv.repositories.SharedRepository

class SharedViewModel(context: Context) : ViewModel() {
    private val sharedRepository = SharedRepository(context)

    val imagesFolderFlow = sharedRepository.imageFolderFlow
    fun imagesByFolder(image: String) = sharedRepository.getImagesByBucket(image)

    val videosFoldersFlow = sharedRepository.videosFoldersFlow
    fun videosByBucket(video: String) = sharedRepository.videosByBucket(video)

    val audioFoldersFlow = sharedRepository.audioFoldersFlow
    fun audiosByBucket(audio: String) = sharedRepository.audiosByBucket(audio)

    private var _speed: MutableLiveData<Int> = MutableLiveData(100)

    val speed: LiveData<Int> = _speed

    val speedX: LiveData<String> = Transformations.map(_speed) {
        String.format("%.1fx", it / 100f)
    }

    private var _selectedImages: MutableLiveData<MutableList<FileModel>> = MutableLiveData()


    val selectedImages: LiveData<MutableList<FileModel>> = _selectedImages


    fun adjustPlayerSpeed(value: Int) {
        _speed.value = value
    }

    fun selectImages(value: MutableList<FileModel>) {
        _selectedImages.value = value

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