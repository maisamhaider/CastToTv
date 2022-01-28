package com.example.casttotv.ui.activities.browser.frags

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.casttotv.R
import com.example.casttotv.databinding.FragmentBrowserContainerBinding
import com.example.casttotv.utils.Pref.getPrefs
import com.example.casttotv.utils.THEME_DARK
import com.example.casttotv.viewmodel.BrowserViewModel


class BrowserContainerFragment : Fragment() {
    private lateinit var _binding: FragmentBrowserContainerBinding
    private val binding get() = _binding

    private val viewModel: BrowserViewModel by activityViewModels {
        BrowserViewModel.BrowserViewModelFactory(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentBrowserContainerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            browserVM = viewModel
            viewModel.loadFragment(BrowserHomeFragment(),
                childFragmentManager,
                R.id.browser_container)

            viewBack.setOnClickListener {
                viewModel.switchToTabBack()
            }

            viewNext.setOnClickListener {
                viewModel.switchToTabForward()
            }

            viewProfile.setOnClickListener {
                viewModel.showBottomSheet(requireActivity().supportFragmentManager,
                    MenuBottomSheetFragment())
                if (viewModel.tabFragmentIsShowing()) {
                    viewModel.clickTabLayout()
                }
            }

            viewDownloads.setOnClickListener {
                viewModel.showBottomSheet(requireActivity().supportFragmentManager,
                    SaveActionFragment())
                if (viewModel.tabFragmentIsShowing()) {
                    viewModel.clickTabLayout()
                }
            }
            viewTab.setOnClickListener {
                viewModel.clickTabLayout()
            }

        }
        viewModel.liveTabs.observe(viewLifecycleOwner) {
            if (it != null) {
                it.size.let { size ->
                    if (size > 9) {
                        binding.textviewTabsAmount.text = "*"
                    } else {
                        binding.textviewTabsAmount.text = size.toString()
                    }
                }
            } else {
                binding.textviewTabsAmount.text = "0"
            }

        }
    }

    override fun onResume() {
        super.onResume()
        val themeDark = requireContext().getPrefs(THEME_DARK, false)
        if (themeDark) {
            binding.imageviewDownloads.setColorFilter(ContextCompat.getColor(requireContext(),
                R.color.cr_silver_chalice))
        } else {
            binding.imageviewDownloads.setColorFilter(ContextCompat.getColor(requireContext(),
                R.color.cr_emperor))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.mainBackPress(false)
    }
}