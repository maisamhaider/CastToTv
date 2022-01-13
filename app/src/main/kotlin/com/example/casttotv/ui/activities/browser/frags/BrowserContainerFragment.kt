package com.example.casttotv.ui.activities.browser.frags

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.casttotv.R
import com.example.casttotv.databinding.FragmentBrowserContainerBinding

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

            viewProfile.setOnClickListener {
                viewModel.showBottomSheet(requireActivity().supportFragmentManager,
                    MenuBottomSheetFragment())
//                viewModel.cancelBottomSheet(requireActivity().supportFragmentManager,
//                    SaveActionFragment())
            }

            viewDownloads.setOnClickListener {
//                viewModel.cancelBottomSheet(requireActivity().supportFragmentManager,
//                    MenuBottomSheetFragment())
                viewModel.showBottomSheet(requireActivity().supportFragmentManager,
                    SaveActionFragment())
            }
            viewTab.setOnClickListener {
                viewModel.clickTabLayout()
            }

        }

    }

}