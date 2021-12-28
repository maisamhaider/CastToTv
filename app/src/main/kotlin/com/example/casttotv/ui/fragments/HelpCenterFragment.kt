package com.example.casttotv.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.casttotv.R
import com.example.casttotv.adapter.HelpCenterAdapter
import com.example.casttotv.databinding.FragmentHelpCenterBinding
import com.example.casttotv.datasource.DataSource


class HelpCenterFragment : Fragment() {
    private lateinit var _binding: FragmentHelpCenterBinding
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHelpCenterBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = HelpCenterAdapter(requireContext())
        binding.apply {
            recyclerView.adapter = adapter
            helpCenter = this@HelpCenterFragment
        }
        adapter.submitList(DataSource().listOfQA(requireContext()))
    }

    fun back() {
        findNavController().navigate(R.id.action_helpCenterFragment_to_homeFragment)
    }
}