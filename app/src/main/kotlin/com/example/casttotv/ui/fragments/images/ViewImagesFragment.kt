package com.example.casttotv.ui.fragments.images

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.casttotv.R
import com.example.casttotv.adapter.ImageHorizonAdapter2
import com.example.casttotv.adapter.ImageViewPagerAdapter
import com.example.casttotv.databinding.FragmentViewImagesBinding
import com.example.casttotv.dataclasses.FileModel
import com.example.casttotv.utils.MySingleton.enablingWiFiDisplay
import com.example.casttotv.utils.MySingleton.toastLong
import com.example.casttotv.utils.animation.DepthPageTransformer
import com.example.casttotv.utils.playingFileModel
import com.example.casttotv.utils.singletonFolderModel
import com.example.casttotv.viewmodel.SharedViewModel


class ViewImagesFragment : Fragment() {

    private var _binding: FragmentViewImagesBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModel by activityViewModels {
        SharedViewModel.SharedViewModelFactory(requireContext())
    }
    var pos = 0
    lateinit var adapter: ImageHorizonAdapter2
    private lateinit var adapterSlider: ImageViewPagerAdapter
    private val list: MutableList<FileModel> = ArrayList()
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

        adapter = ImageHorizonAdapter2(::onItemClick,false)
        adapterSlider = ImageViewPagerAdapter() { }

        binding.recyclerView.adapter = adapter
        binding.viewpager2.adapter = adapterSlider
        binding.viewpager2.setPageTransformer(DepthPageTransformer())
        sharedViewModel.imagesByFolder(singletonFolderModel.folderPath)
            .observe(viewLifecycleOwner) {
                if (!it.isNullOrEmpty()) {
                    adapter.submitList(it)
                    adapterSlider.submitList(it)
                    list.addAll(it)

                }
            }
        binding.apply {
            thisFrag = this@ViewImagesFragment
            textviewFileName.text = playingFileModel.fileName

        }

        binding.viewpager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                adapter.setSelect(position)
                binding.recyclerView.smoothScrollToPosition(position)
                adapter.notifyDataSetChanged()
                binding.textviewFileName.text = list[position].fileName
            }
        })

    }

    fun enablingWiFiDisplay() {
        requireContext().enablingWiFiDisplay()
    }

    fun back() {
        findNavController().navigate(R.id.action_viewImagesFragment_to_imagesFragment)
    }

    private fun onItemClick(fileModel: FileModel, pos: Int) {
        binding.viewpager2.setCurrentItem(pos, true)
        adapter.setSelect(pos)
        binding.recyclerView.smoothScrollToPosition(pos)
        adapter.notifyDataSetChanged()
        binding.textviewFileName.text = fileModel.fileName
    }


}