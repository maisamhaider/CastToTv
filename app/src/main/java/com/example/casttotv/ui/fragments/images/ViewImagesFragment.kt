package com.example.casttotv.ui.fragments.images

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.casttotv.adapter.ImageHorizonAdapter2
import com.example.casttotv.adapter.ImageViewPagerAdapter
import com.example.casttotv.databinding.FragmentViewImagesBinding
import com.example.casttotv.models.FileModel
import com.example.casttotv.utils.MySingleton.toastLong
import com.example.casttotv.viewmodel.SharedViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class ViewImagesFragment : Fragment() {

    private var _binding: FragmentViewImagesBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModel by activityViewModels()
    lateinit var adapter: ImageHorizonAdapter2
    private lateinit var adapterSlider: ImageViewPagerAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentViewImagesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bundle = arguments?.getBundle("bundle")!!

        val path = bundle.getString("folderPath")!!
        adapter = ImageHorizonAdapter2(::onItemClick, requireContext(), false)
        adapterSlider = ImageViewPagerAdapter(requireContext())

        binding.recyclerView.adapter = adapter
        binding.viewpager2.adapter = adapterSlider

        CoroutineScope(Dispatchers.IO).launch {
            sharedViewModel.imagesByFolder(path).collect {
                CoroutineScope(Dispatchers.Main).launch {
                    if (!it.isNullOrEmpty()) {
                        adapter.submitList(it)
                        adapterSlider.submitList(it)

                    } else {
                        requireContext().toastLong("Image not found.")
                    }
                }
            }
        }

    }


    private fun onItemClick(fileModel: FileModel, pos: Int) {
        binding.viewpager2.setCurrentItem(pos, true)
        adapter.selected = fileModel.filePath
        binding.recyclerView.smoothScrollToPosition(pos)
        adapter.notifyDataSetChanged()
    }


}