package com.example.casttotv.ui.activities.browser.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.casttotv.databinding.FragmentShareBinding
import com.example.casttotv.utils.MySingleton.toastShort
import com.example.casttotv.viewmodel.BrowserViewModel

class ShareFragment : Fragment() {

    private lateinit var _binding: FragmentShareBinding
    private val binding get() = _binding

    val browserViewModel: BrowserViewModel by activityViewModels {
        BrowserViewModel.BrowserViewModelFactory(requireContext())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentShareBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = ShareFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            textviewShareScreenshot.setOnClickListener {
                val ssPath = browserViewModel.saveScreenShot(true) //screenshot return path
                if (ssPath != "error") {
                    browserViewModel.shareSS(ssPath)
                } else {
                    requireContext().toastShort("error")
                }
            }
            textviewSharePdf.setOnClickListener {
                browserViewModel.print()
            }
            textviewShareLink.setOnClickListener {
                browserViewModel.shareLink()
            }
            textviewCopyToClipboard.setOnClickListener {
                browserViewModel.copyToClipBoard()
                requireContext().toastShort("copied")
            }
            textviewOpenLinkWith.setOnClickListener {
                browserViewModel.openWith()
            }
        }
    }
}