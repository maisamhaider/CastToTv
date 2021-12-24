package com.example.casttotv.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.casttotv.R
import com.example.casttotv.adapter.TutorialPagerAdapter
import com.example.casttotv.databinding.*
import com.example.casttotv.viewmodel.TutorialViewModel


class TutorialFragment : Fragment() {

    lateinit var _binding: FragmentTutorialBinding
    private val binding get() = _binding
    lateinit var adapter: TutorialPagerAdapter
    lateinit var viewPager: ViewPager2
    private val viewList: MutableList<View> = ArrayList()
    val tutorialViewModel: TutorialViewModel by viewModels {
        TutorialViewModel.TutorialViewModelFactory(requireContext())
    }
    private var currentPosition = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentTutorialBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        adapter = TutorialPagerAdapter(requireContext())

        val view1: View =
            TutorialLayout1Binding.inflate(LayoutInflater.from(requireActivity())).root
        val view2: View =
            TutorialLayout2Binding.inflate(LayoutInflater.from(requireActivity())).root
        val view3: View =
            TutorialLayout3Binding.inflate(LayoutInflater.from(requireActivity())).root
        val view4: View =
            TutorialLayout4Binding.inflate(LayoutInflater.from(requireActivity())).root
        val view5: View =
            TutorialLayout5Binding.inflate(LayoutInflater.from(requireActivity())).root
        viewList.add(view1)
        viewList.add(view2)
        viewList.add(view3)
        viewList.add(view4)
        viewList.add(view5)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            tutorialVM = tutorialViewModel
            thisFrag = this@TutorialFragment
            viewPager = viewpager2
            viewPager.adapter = adapter
            adapter.submitList(viewList)
            dotsIndicator.setViewPager2(viewPager)
        }
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {


            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int,
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                currentPosition = position
                when (position) {
                    0 -> {
                        binding.textviewHeader.text = getString(R.string.screen_mirror)
                        tutorialViewModel.nextButtonProperties(getString(R.string.next))
                        binding.imageviewArrow.visibility = View.VISIBLE
                    }
                    1 -> {
                        binding.textviewHeader.text = getString(R.string.screen_setup)
                        tutorialViewModel.nextButtonProperties(getString(R.string.next))
                        binding.imageviewArrow.visibility = View.VISIBLE

                    }
                    2 -> {
                        binding.textviewHeader.text = getString(R.string.device_hotspot)
                        tutorialViewModel.nextButtonProperties(getString(R.string.next))
                        binding.imageviewArrow.visibility = View.VISIBLE

                    }
                    3 -> {
                        binding.textviewHeader.text = getString(R.string.scan_code)
                        tutorialViewModel.nextButtonProperties(getString(R.string.next))
                        binding.imageviewArrow.visibility = View.VISIBLE

                    }
                    4 -> {
                        binding.textviewHeader.text = getString(R.string.support)
                        tutorialViewModel.nextButtonProperties(getString(R.string.finish))
                        binding.imageviewArrow.visibility = View.GONE

                    }
                }
            }
        })

    }

    fun nextOrFinished() {
        if (currentPosition == 4) {
            findNavController().navigate(R.id.action_tutorialFragment_to_homeFragment)
        } else {
            tutorialViewModel.nextOrFinished(currentPosition, viewPager)
        }
    }
}