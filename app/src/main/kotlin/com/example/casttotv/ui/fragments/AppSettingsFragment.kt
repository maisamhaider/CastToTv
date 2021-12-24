package com.example.casttotv.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.casttotv.BuildConfig
import com.example.casttotv.databinding.FragmentAppSettingsBinding
import com.example.casttotv.viewmodel.MainViewModel


class AppSettingsFragment : Fragment() {
    private lateinit var _binding: FragmentAppSettingsBinding
    private val binding get() = _binding
    val version = BuildConfig.VERSION_NAME
    private val viewModel: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAppSettingsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.preLoadDarkAndLight(requireContext())
        binding.apply {
            thisFrag = this@AppSettingsFragment
            lifecycleOwner = viewLifecycleOwner
            mainVM = viewModel
        }
    }

    fun themeDialog() {
        viewModel.themeDialog(requireContext())
    }
}