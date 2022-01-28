package com.example.casttotv.ui.fragments.images

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.casttotv.R
import com.example.casttotv.adapter.ImageVideosAdapter
import com.example.casttotv.databinding.FragmentImagesBinding
import com.example.casttotv.dataclasses.FileModel
import com.example.casttotv.utils.IMAGE
import com.example.casttotv.utils.MySingleton.enablingWiFiDisplay
import com.example.casttotv.utils.MySingleton.toastLong
import com.example.casttotv.utils.playingFileModel
import com.example.casttotv.utils.singletonFolderModel
import com.example.casttotv.viewmodel.SharedViewModel

class ImagesFragment : Fragment() {

    private var _binding: FragmentImagesBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentImagesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = ImageVideosAdapter(::onItemClick, IMAGE)

        binding.apply {
            imagesFrag = this@ImagesFragment
            recyclerView.adapter = adapter
        }
        sharedViewModel.imagesByFolder(singletonFolderModel.folderPath)
            .observe(viewLifecycleOwner) {
                if (!it.isNullOrEmpty()) {
                    adapter.submitList(it)
                }
            }
    }

    fun back() {
        findNavController().navigate(R.id.action_imagesFragment_to_imagesFoldersFragment)
    }

    fun enablingWiFiDisplay() {
        requireContext().enablingWiFiDisplay()
    }

    private fun onItemClick(fileModel: FileModel, int: Int) {
        Bundle().apply { playingFileModel = fileModel }

        findNavController().navigate(R.id.action_imagesFragment_to_viewImagesFragment)
        requireContext().toastLong(singletonFolderModel.filePath)
    }
}