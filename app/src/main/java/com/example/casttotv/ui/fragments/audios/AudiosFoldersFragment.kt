package com.example.casttotv.ui.fragments.audios

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.casttotv.R
import com.example.casttotv.adapter.FolderAdapter
import com.example.casttotv.databinding.FragmentAudiosFoldersBinding
import com.example.casttotv.models.FolderModel
import com.example.casttotv.utils.MySingleton
import com.example.casttotv.utils.MySingleton.AUDIO
import com.example.casttotv.utils.MySingleton.toastLong
import com.example.casttotv.viewmodel.SharedViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class AudiosFoldersFragment : Fragment() {
    private var _binding: FragmentAudiosFoldersBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: SharedViewModel by activityViewModels {
        SharedViewModel.SharedViewModelFactory(requireContext())
    }
    private lateinit var adapter: FolderAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAudiosFoldersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = FolderAdapter(::onItemClick, requireContext(), AUDIO)
        binding.recyclerView.adapter = adapter
        CoroutineScope(Dispatchers.IO).launch {
            loadAudios()
        }
    }

    private suspend fun loadAudios() {
        sharedViewModel.audioFoldersFlow.collect {
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
        MySingleton.folder_path = folderPath.folderPath
        findNavController().navigate(R.id.action_audiosFoldersFragment_to_audiosFragment)
    }


}