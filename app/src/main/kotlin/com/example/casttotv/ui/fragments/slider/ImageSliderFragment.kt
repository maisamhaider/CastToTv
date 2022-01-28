package com.example.casttotv.ui.fragments.slider

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.casttotv.R
import com.example.casttotv.adapter.ImageVideosAdapter
import com.example.casttotv.databinding.FragmentImageSliderBinding
import com.example.casttotv.dataclasses.FileModel
import com.example.casttotv.utils.MySingleton.enablingWiFiDisplay
import com.example.casttotv.utils.SLIDE
import com.example.casttotv.utils.singletonFolderModel
import com.example.casttotv.viewmodel.SharedViewModel

class ImageSliderFragment : Fragment() {
    private lateinit var _binding: FragmentImageSliderBinding
    private val binding get() = _binding

    private val sharedViewModel: SharedViewModel by activityViewModels {
        SharedViewModel.SharedViewModelFactory(requireContext())
    }

    private lateinit var imageVideosAdapter: ImageVideosAdapter

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

        imageVideosAdapter = ImageVideosAdapter(::onImageClick, SLIDE)
        imageVideosAdapter.setVM(sharedViewModel)

        _binding.apply {
            lifecycleOwner = viewLifecycleOwner
            shareViewModel = sharedViewModel
            thisFragment = this@ImageSliderFragment
            recyclerView.adapter = imageVideosAdapter
        }
        loadImages(singletonFolderModel.folderPath)
    }

    private fun loadImages(path: String) {
        sharedViewModel.imagesByFolder(path).observe(viewLifecycleOwner) {
            it?.let { imageVideosAdapter.submitList(it) }
        }
    }

    private fun onImageClick(fileModel: FileModel, int: Int) {}

    fun enablingWiFiDisplay() {
        requireContext().enablingWiFiDisplay()
    }

    fun next() {
        if (sharedViewModel.selectedImages.value != null) {
            findNavController().navigate(R.id.action_imageSliderFragment_to_imageSliderViewerFragment)
        }
    }

    fun back() {
        findNavController().navigate(R.id.action_imageSliderFragment_to_sliderImagesFoldersFragment)
    }
}