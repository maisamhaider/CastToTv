package com.example.casttotv.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.casttotv.R
import com.example.casttotv.adapter.MoreAppsAdapter
import com.example.casttotv.databinding.FragmentMoreAppsBinding
import com.example.casttotv.dataclasses.ModelMoreApps


class MoreAppsFragment : Fragment() {
    private lateinit var _binding: FragmentMoreAppsBinding
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentMoreAppsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = MoreAppsAdapter(requireContext())
        val list = listOf(ModelMoreApps("", "", ""),
            ModelMoreApps("", "", ""),
            ModelMoreApps("", "", ""))
        binding.apply {
            thisFrag = this@MoreAppsFragment
            recyclerView.adapter = adapter
            adapter.submitList(list)
        }
    }

    fun back() {
        findNavController().navigate(R.id.action_moreAppsFragment_to_homeFragment)
    }
}