package com.example.casttotv.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.casttotv.adapter.LanguagesAdapter
import com.example.casttotv.databinding.FragmentLanguagesBinding
import com.example.casttotv.models.Lang
import com.example.casttotv.utils.MySingleton.listOfLanguages

class LanguagesFragment : Fragment() {

    private lateinit var _binding: FragmentLanguagesBinding
    private val binding get() = _binding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment

        _binding = FragmentLanguagesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = LanguagesAdapter(requireContext())

        binding.apply {
            recyclerview.adapter = adapter
            thisFragment = this@LanguagesFragment
        }

    }

    fun list() : MutableList<Lang> {
        return  listOfLanguages
    }

}

