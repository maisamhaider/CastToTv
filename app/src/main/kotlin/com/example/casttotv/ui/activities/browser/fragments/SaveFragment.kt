package com.example.casttotv.ui.activities.browser.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.casttotv.databinding.FragmentSaveBinding
import com.example.casttotv.utils.MySingleton.toastShort
import com.example.casttotv.viewmodel.BrowserViewModel


class SaveFragment : Fragment() {

    private lateinit var _binding: FragmentSaveBinding
    private val binding get() = _binding

    val browserViewModel: BrowserViewModel by activityViewModels {
        BrowserViewModel.BrowserViewModelFactory(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSaveBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = SaveFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            browserVM = browserViewModel
            textviewSaveScreenshot.setOnClickListener {
                val ssPath = browserViewModel.saveScreenShot(false) //screenshot return path
                if (ssPath != "error") {
                    requireContext().toastShort("save \n$ssPath")
                } else {
                    requireContext().toastShort("error")
                }
            }


        }
    }
}