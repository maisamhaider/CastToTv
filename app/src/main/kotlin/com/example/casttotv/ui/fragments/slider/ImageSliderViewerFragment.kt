package com.example.casttotv.ui.fragments.slider

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.example.casttotv.R
import com.example.casttotv.adapter.ImageViewPagerAdapter
import com.example.casttotv.adapter.ViewPager2AnimationsAdapter
import com.example.casttotv.databinding.FragmentImageSliderViewerBinding
import com.example.casttotv.models.PagerAnimation
import com.example.casttotv.viewmodel.SharedViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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

    private var mCountDownTimer: CountDownTimer? = null
    var currentImage = 0

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
            lifecycleOwner = viewLifecycleOwner
            viewModel = sharedViewModel
            thisFragment = this@ImageSliderViewerFragment
        }
        adapterSlider = ImageViewPagerAdapter(requireContext(), ::onPagerClicked)
        viewPager2AnimationsAdapter = ViewPager2AnimationsAdapter(::onAnimationSelected)

        binding.recyclerView.adapter = viewPager2AnimationsAdapter

        CoroutineScope(Dispatchers.IO).launch {
            sharedViewModel.pagerAnimations().collect {
                launch(Dispatchers.Main) {
                    viewPager2AnimationsAdapter.submitList(it)
                }
            }
        }
        viewPager = null
        viewPager = binding.viewpager2
        viewPager!!.adapter = adapterSlider
        viewPager!!.scrollBarFadeDuration = 2000

        sharedViewModel.selectedImages.observe(viewLifecycleOwner, {
            listSize = it.size
            adapterSlider.submitList(it)
        })

        startTimer()
        sharedViewModel.play.observe(viewLifecycleOwner, {
            if (it) {
                pauseTimer()
                restartTimer()
                binding.imageViewPlayPause.visibility = View.GONE
            } else {
                pauseTimer()
                binding.imageViewPlayPause.visibility = View.VISIBLE
                binding.imageViewPlayPause.setImageResource(R.drawable.ic_play_circle)
            }
        })
        onAnimationSelected(null)
    }

    private fun startTimer() {
        val milliSec = 86400000L
        mCountDownTimer = object : CountDownTimer(milliSec, 3000) {
            override fun onTick(millisUntilFinished: Long) {
                if (currentImage != listSize) {
                    binding.viewpager2.setCurrentItem(currentImage,true)
                    currentImage++
                } else {
                    currentImage = 0
                    sharedViewModel.playPause(false)
                }
            }

            override fun onFinish() {}
        }
    }

    private fun pauseTimer() {
        mCountDownTimer!!.cancel()
    }

    private fun restartTimer() {
        CoroutineScope(Dispatchers.IO).launch {
            delay(1000)
            mCountDownTimer!!.start()
        }
    }

    fun onPagerClicked() {
        sharedViewModel.playPause()
    }

    private fun onAnimationSelected(animationClass: PagerAnimation?) {
        sharedViewModel.playPause(false)
        viewPager = null
        viewPager = binding.viewpager2
        viewPager!!.adapter = adapterSlider
        viewPager!!.scrollBarFadeDuration = 2000
        viewPager!!.setPageTransformer(animationClass?.pageTransformer)
        if (currentImage!= 0)
        {
            binding.viewpager2.setCurrentItem(currentImage-1,false)
        }
    }

    override fun onPause() {
        super.onPause()
        sharedViewModel.playPause(false)
    }

}