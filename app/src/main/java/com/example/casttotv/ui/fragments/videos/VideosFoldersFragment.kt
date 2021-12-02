package com.example.casttotv.ui.fragments.videos

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.casttotv.R
import com.example.casttotv.adapter.FolderAdapter
import com.example.casttotv.databinding.FragmentVideosFoldersBinding
import com.example.casttotv.models.FolderModel
import com.example.casttotv.utils.MySingleton.VIDEO
import com.example.casttotv.utils.MySingleton.folder_path
import com.example.casttotv.utils.MySingleton.toastLong
import com.example.casttotv.viewmodel.SharedViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class VideosFoldersFragment : Fragment() {


    private var _binding: FragmentVideosFoldersBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModel by activityViewModels {
        SharedViewModel.SharedViewModelFactory(requireContext())
    }

    private lateinit var adapter: FolderAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentVideosFoldersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = FolderAdapter(::onItemClick, requireContext(), VIDEO)
        binding.recyclerView.adapter = adapter
        CoroutineScope(Dispatchers.IO).launch {
            loadVideos()
        }
    }

    private suspend fun loadVideos() {
        sharedViewModel.videosFoldersFlow.collect {
            CoroutineScope(Dispatchers.Main).launch {
                if (!it.isNullOrEmpty()) {
                    adapter.submitList(it)
                } else {
                    requireContext().toastLong("Image not found.")
                }
            }
        }
    }

    private fun onItemClick(folderPath: FolderModel) {
        folder_path = folderPath.folderPath
        findNavController().navigate(R.id.action_videosFoldersFragment_to_videosFragment)
    }


}