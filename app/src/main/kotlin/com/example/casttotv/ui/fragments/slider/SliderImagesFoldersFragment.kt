package com.example.casttotv.ui.fragments.slider

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.casttotv.R
import com.example.casttotv.adapter.FolderAdapter
import com.example.casttotv.databinding.FragmentSliderImagesFoldersBinding
import com.example.casttotv.models.FolderModel
import com.example.casttotv.utils.IMAGE
import com.example.casttotv.utils.MySingleton.enablingWiFiDisplay
import com.example.casttotv.utils.singletonFolderModel
import com.example.casttotv.viewmodel.SharedViewModel


class SliderImagesFoldersFragment : Fragment() {

    private var _binding: FragmentSliderImagesFoldersBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModel by activityViewModels {
        SharedViewModel.SharedViewModelFactory(requireContext())
    }

    private lateinit var adapter: FolderAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentSliderImagesFoldersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = FolderAdapter(::onItemClick, requireContext(), IMAGE)

        binding.apply {
            thisFrag = this@SliderImagesFoldersFragment
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
        findNavController().navigate(R.id.action_sliderImagesFoldersFragment_to_homeFragment)
    }

    private fun onItemClick(folderPath: FolderModel) {
        singletonFolderModel = folderPath
         findNavController().navigate(R.id.action_sliderImagesFoldersFragment_to_imageSliderFragment)
    }
}