package com.example.casttotv.ui.fragments.audios

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.casttotv.R
import com.example.casttotv.adapter.ImageVideosAdapter
import com.example.casttotv.databinding.FragmentAudiosBinding
import com.example.casttotv.models.FileModel
import com.example.casttotv.utils.MySingleton
import com.example.casttotv.utils.MySingleton.AUDIO
import com.example.casttotv.utils.MySingleton.toastLong
import com.example.casttotv.viewmodel.SharedViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class AudiosFragment : Fragment() {

    private var _binding: FragmentAudiosBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAudiosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = ImageVideosAdapter(::onItemClick, requireContext(), AUDIO)

        binding.recyclerView.adapter = adapter

        CoroutineScope(Dispatchers.IO).launch {
            sharedViewModel.audiosByBucket(MySingleton.folder_path).collect {
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
        MySingleton.playingFileModel = fileModel
        MySingleton.playingFileName = fileModel.fileName
        findNavController().navigate(R.id.action_audiosFragment_to_audioPlayerFragment)
    }


}