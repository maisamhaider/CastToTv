package com.example.casttotv.ui.fragments.slider

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.example.casttotv.adapter.ImageViewPagerAdapter
import com.example.casttotv.adapter.ViewPager2AnimationsAdapter
import com.example.casttotv.databinding.FragmentImageSliderViewerBinding
import com.example.casttotv.models.PagerAnimation
import com.example.casttotv.utils.MySingleton.toastShort
import com.example.casttotv.viewmodel.SharedViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class ImageSliderViewerFragment : Fragment() {
    private lateinit var _binding: FragmentImageSliderViewerBinding

    private val sharedViewModel: SharedViewModel by activityViewModels {
        SharedViewModel.SharedViewModelFactory(requireContext())
    }
    private var viewPager: ViewPager2? = null

    private val binding get() = _binding
    private lateinit var adapterSlider: ImageViewPagerAdapter
    private lateinit var viewPager2AnimationsAdapter: ViewPager2AnimationsAdapter
    var listSize = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentImageSliderViewerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            thisFragment = this@ImageSliderViewerFragment
        }
        adapterSlider = ImageViewPagerAdapter(requireContext(), ::onPagerClicked)
        viewPager2AnimationsAdapter = ViewPager2AnimationsAdapter(::onAnimationSelected)

        binding.recyclerView.adapter = viewPager2AnimationsAdapter

        CoroutineScope(Dispatchers.IO).launch {
            sharedViewModel.pagerAnimations().collect {
                listSize = it.size
                launch(Dispatchers.Main) {
                    viewPager2AnimationsAdapter.submitList(it)
                }
            }
        }
        sharedViewModel.play.observe(viewLifecycleOwner, {
            if (it) {
                binding.buttonPlay.text = "pause"
                requireContext().toastShort("playing")
            } else {
                binding.buttonPlay.text = "play"
                requireContext().toastShort("paused")
            }
        })
        onAnimationSelected(null)
    }

    private fun play() {
        for (i in listSize until listSize) {

            binding.viewpager2.setCurrentItem(i, true)
        }
    }


    fun onPagerClicked() {
        sharedViewModel.playPause()
        requireContext().toastShort("Play option is coming soon")
    }

    private fun onAnimationSelected(animationClass: PagerAnimation?) {
        requireContext().toastShort("Swipe to check animation")
        viewPager = null
        viewPager = binding.viewpager2
        viewPager!!.adapter = adapterSlider
        viewPager!!.setPageTransformer(animationClass?.pageTransformer)
        sharedViewModel.selectedImages.observe(viewLifecycleOwner, {
            adapterSlider.submitList(it)
        })

    }

}