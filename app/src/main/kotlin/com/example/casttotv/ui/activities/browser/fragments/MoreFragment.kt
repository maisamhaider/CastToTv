package com.example.casttotv.ui.activities.browser.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.casttotv.databinding.FragmentMoreBinding
import com.example.casttotv.ui.activities.browser.BrowserSettingsActivity
import com.example.casttotv.utils.MySingleton.toastShort
import com.example.casttotv.viewmodel.BrowserViewModel


class MoreFragment : Fragment() {

    private lateinit var _binding: FragmentMoreBinding
    private val binding get() = _binding

    val browserViewModel: BrowserViewModel by activityViewModels {
        BrowserViewModel.BrowserViewModelFactory(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMoreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            thisFragment = this@MoreFragment

            textviewOpenDownloads.setOnClickListener {
                browserViewModel.openDownload()
            }
            textviewOpenFileManager.setOnClickListener {
                browserViewModel.openFileManager()
            }
            textviewSaveAsFavorite.setOnClickListener {
                browserViewModel.addToFavorite()
                requireContext().toastShort("added")
            }

        }
    }

    fun settings() {
        startActivity(Intent(requireContext(), BrowserSettingsActivity::class.java))
    }


    companion object {
        @JvmStatic
        fun newInstance() = MoreFragment()
    }
}