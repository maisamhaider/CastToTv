package com.example.casttotv.ui.fragments.videos

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.casttotv.R
import com.example.casttotv.adapter.ImageVideosAdapter
import com.example.casttotv.databinding.FragmentVideosBinding
import com.example.casttotv.models.FileModel
import com.example.casttotv.utils.MySingleton.toastLong
import com.example.casttotv.utils.VIDEO
import com.example.casttotv.utils.folder_path
import com.example.casttotv.utils.playingFileModel
import com.example.casttotv.utils.playingFileName
import com.example.casttotv.viewmodel.SharedViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class VideosFragment : Fragment() {

    private var _binding: FragmentVideosBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentVideosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = ImageVideosAdapter(::onItemClick, requireContext(), VIDEO)

        binding.recyclerView.adapter = adapter

        CoroutineScope(Dispatchers.IO).launch {
            sharedViewModel.videosByBucket(folder_path).collect {
                CoroutineScope(Dispatchers.Main).launch {
                    if (!it.isNullOrEmpty()) {
                        adapter.submitList(it)
                    } else {
                        requireContext().toastLong("video not found.")
                    }
                }
            }
        }

    }


    private fun onItemClick(fileModel: FileModel, int: Int) {
        playingFileModel = fileModel
        playingFileName = fileModel.fileName
        findNavController().navigate(R.id.action_videosFragment_to_customVideoPlayerFragment)
    }
}