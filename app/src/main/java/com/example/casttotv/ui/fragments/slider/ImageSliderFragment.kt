package com.example.casttotv.ui.fragments.slider

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.casttotv.R
import com.example.casttotv.adapter.FolderAdapter
import com.example.casttotv.adapter.ImageVideosAdapter
import com.example.casttotv.adapter.SelectedImagesAdapter
import com.example.casttotv.databinding.FragmentImageSliderBinding
import com.example.casttotv.models.FileModel
import com.example.casttotv.models.FolderModel
import com.example.casttotv.utils.MySingleton
import com.example.casttotv.utils.MySingleton.toastLong
import com.example.casttotv.utils.MySingleton.toastShort
import com.example.casttotv.viewmodel.SharedViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect

class ImageSliderFragment : Fragment() {

    val TAG = "ImageSliderFragment"

    private lateinit var _binding: FragmentImageSliderBinding
    private val binding get() = _binding

    private val sharedViewModel: SharedViewModel by activityViewModels {
        SharedViewModel.SharedViewModelFactory(requireContext())
    }

    private lateinit var folderAdapter: FolderAdapter
    private lateinit var imageVideosAdapter: ImageVideosAdapter
    private lateinit var selectedImagesAdapter: SelectedImagesAdapter


    var folderPath = ""

    private val selectedImageList: MutableList<FileModel> = ArrayList()

    private val coroutineScopeFolders = CoroutineScope(Job())
    private val coroutineScopeImages = CoroutineScope(Job())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentImageSliderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        folderAdapter = FolderAdapter(::onFolderClick, requireContext(), MySingleton.IMAGE)
        imageVideosAdapter = ImageVideosAdapter(::onImageClick, requireContext(), MySingleton.IMAGE)
        selectedImagesAdapter =
            SelectedImagesAdapter(::onImageRemoveClick, requireContext())

        binding.recyclerviewFolder.adapter = folderAdapter

        binding.recyclerviewImages.adapter = imageVideosAdapter

        binding.recyclerviewSelected.adapter = selectedImagesAdapter

        _binding.apply {
            lifecycleOwner = viewLifecycleOwner
            shareViewModel = sharedViewModel
            thisFragment = this@ImageSliderFragment
        }

        CoroutineScope(Dispatchers.IO).launch {
            loadFolders()
            delay(500)
            loadImages(folderPath)
        }

    }

    private fun loadFolders() = coroutineScopeFolders.launch(Dispatchers.IO) {
        sharedViewModel.imagesFolderFlow.collect {
            launch(Dispatchers.Main) {
                if (!it.isNullOrEmpty()) {
                    folderPath = it[0].folderPath
                } else {
                    requireContext().toastLong("Image not found.")
                }
            }
        }
    }


    private fun loadImages(path: String) = coroutineScopeImages.launch(Dispatchers.IO) {
        sharedViewModel.imagesByFolder(path).collect {
            launch(Dispatchers.Main) {
                if (!it.isNullOrEmpty()) {
                    imageVideosAdapter.submitList(it)
                } else {
                    requireContext().toastLong("Image not found.")
                }
            }
        }
    }


    var i = 0
    private fun onFolderClick(folderPath: FolderModel) {
        try {
            if (loadImages(folderPath.folderPath).isCompleted) {
                loadImages(folderPath.folderPath)
                requireContext().toastShort("${i++}")
            }

        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())

        }
    }

    private fun onImageClick(fileModel: FileModel, int: Int) {
        if (!selectedImageList.contains(fileModel)) {
            selectedImageList.add(0, fileModel)
            sharedViewModel.selectImages(selectedImageList)
        } else {
            selectedImageList.remove(fileModel)
            sharedViewModel.selectImages(selectedImageList)
        }
    }

    private fun onImageRemoveClick(fileModel: FileModel) {
        selectedImageList.remove(fileModel)
        sharedViewModel.selectImages(selectedImageList)
    }

    fun next() {
        findNavController().navigate(R.id.action_imageSliderFragment_to_imageSliderViewerFragment)
    }

    fun back() {
        findNavController().navigate(R.id.action_imageSliderFragment_to_homeFragment)
    }
}