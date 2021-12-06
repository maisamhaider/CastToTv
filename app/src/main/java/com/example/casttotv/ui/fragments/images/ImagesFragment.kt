package com.example.casttotv.ui.fragments.images

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.casttotv.R
import com.example.casttotv.adapter.ImageVideosAdapter
import com.example.casttotv.databinding.FragmentImagesBinding
import com.example.casttotv.models.FileModel
import com.example.casttotv.utils.MySingleton.IMAGE
import com.example.casttotv.utils.MySingleton.toastLong
import com.example.casttotv.viewmodel.SharedViewModel

class ImagesFragment : Fragment() {

    private var _binding: FragmentImagesBinding? = null
    private var path = ""
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
        path = arguments?.getString("folderPath")!!

        val adapter = ImageVideosAdapter(::onItemClick, requireContext(), IMAGE)

        binding.recyclerView.adapter = adapter

        sharedViewModel.imagesByFolder(path).observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                adapter.submitList(it)
            } else {
                requireContext().toastLong("Image not found.")
            }
        }


    }


    private fun onItemClick(fileModel: FileModel, int: Int) {
        val bundle = Bundle().apply {
            putString("imagePath", fileModel.filePath)
            putString("folderPath", path)
            putInt("image_id", int)
        }

        findNavController().navigate(R.id.action_imagesFragment_to_viewImagesFragment,
            bundleOf("bundle" to bundle))
        requireContext().toastLong(path)
    }
}