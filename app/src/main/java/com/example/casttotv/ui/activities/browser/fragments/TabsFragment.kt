package com.example.casttotv.ui.activities.browser.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.casttotv.databinding.FragmentTabsBinding
 import com.example.casttotv.viewmodel.BrowserViewModel


class TabsFragment : Fragment() {
    private lateinit var _binding: FragmentTabsBinding
    private val binding get() = _binding

    val browserViewModel: BrowserViewModel by activityViewModels {
        BrowserViewModel.BrowserViewModelFactory(requireContext())
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentTabsBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = TabsFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            textviewOpenFavorite.setOnClickListener {
                browserViewModel.openFavorite()
            }
        }
    }
}

