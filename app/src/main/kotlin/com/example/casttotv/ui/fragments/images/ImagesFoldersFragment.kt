package com.example.casttotv.ui.fragments.images

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.casttotv.R
import com.example.casttotv.adapter.FolderAdapter
import com.example.casttotv.databinding.ImagesFoldersFragmentBinding
import com.example.casttotv.dataclasses.FolderModel
import com.example.casttotv.utils.IMAGE
import com.example.casttotv.utils.MySingleton.enablingWiFiDisplay
import com.example.casttotv.utils.MySingleton.toastLong
import com.example.casttotv.utils.singletonFolderModel
import com.example.casttotv.viewmodel.SharedViewModel


class ImagesFoldersFragment : Fragment() {

    private var _binding: ImagesFoldersFragmentBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModel by activityViewModels {
        SharedViewModel.SharedViewModelFactory(requireContext())
    }

    private lateinit var adapter: FolderAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = ImagesFoldersFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = FolderAdapter(::onItemClick, requireContext(), IMAGE)

        binding.apply {
            imageFolderFrag = this@ImagesFoldersFragment
            recyclerView.adapter = adapter
        }
        loadImageFolder()
    }

    private fun loadImageFolder() {
        sharedViewModel.imagesFolderFlow.observe(this.viewLifecycleOwner) {
            it?.let {
                adapter.submitList(it)
            }
        }
    }

    fun enablingWiFiDisplay() {
        requireContext().enablingWiFiDisplay()
    }

    fun back() {
        findNavController().navigate(R.id.action_imagesFoldersFragment_to_homeFragment)
    }

    private fun onItemClick(folderPath: FolderModel) {
        singletonFolderModel = folderPath
        findNavController().navigate(R.id.action_imagesFoldersFragment_to_imagesFragment)
        requireContext().toastLong(folderPath.folderPath)
    }

}