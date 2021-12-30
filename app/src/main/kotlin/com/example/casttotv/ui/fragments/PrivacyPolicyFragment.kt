package com.example.casttotv.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.casttotv.R
import com.example.casttotv.databinding.FragmentPrivacyPolicyBinding


class PrivacyPolicyFragment : Fragment() {

    lateinit var _binding: FragmentPrivacyPolicyBinding
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentPrivacyPolicyBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            thisFrag = this@PrivacyPolicyFragment
        }
    }

    fun back() {
        findNavController().navigate(R.id.action_pricacyPolicyFragment_to_appSettingsFragment)
    }


}