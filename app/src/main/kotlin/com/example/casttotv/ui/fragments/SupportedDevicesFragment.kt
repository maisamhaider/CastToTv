package com.example.casttotv.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.casttotv.R
import com.example.casttotv.adapter.SupportedDevicesAdapter
import com.example.casttotv.databinding.FragmentSupportedDevicesBinding

class SupportedDevicesFragment : Fragment() {
    private lateinit var _binding: FragmentSupportedDevicesBinding
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSupportedDevicesBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = SupportedDevicesAdapter()
        val list = listOf("1", "1", "1", "1", "1", "1")
        binding.apply {
            thisFrag = this@SupportedDevicesFragment
            recyclerView.adapter = adapter
            adapter.submitList(list)
        }
    }

    fun back() {
        findNavController().navigate(R.id.action_supportedDevicesFragment_to_appSettingsFragment)
    }
}