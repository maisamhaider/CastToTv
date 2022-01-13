package com.example.casttotv.ui.activities.browser.frags

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.casttotv.R
import com.example.casttotv.databinding.FragmentSaveActionBinding
import com.example.casttotv.ui.activities.MainActivity
import com.example.casttotv.utils.MySingleton.toastShort
import com.example.casttotv.viewmodel.BrowserViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SaveActionFragment : BottomSheetDialogFragment() {

    lateinit var _binding: FragmentSaveActionBinding
    private val binding get() = _binding


    private val viewModel: BrowserViewModel by activityViewModels {
        BrowserViewModel.BrowserViewModelFactory(requireActivity())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogStyle)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSaveActionBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            browserVM = viewModel
            thisFrag = this@SaveActionFragment
            clSaveSs.setOnClickListener {
                val ssPath = viewModel.saveScreenShot(false) //screenshot return path
                if (ssPath != "error") {
                    requireContext().toastShort("${getString(R.string.save)}\n$ssPath")
                } else {
                    requireContext().toastShort(getString(R.string.make_sure_you_are_in_browser_view))
                }
            }
            clSaveAsPdf.setOnClickListener {
                requireContext().toastShort("clicked")
                if (activity is MainActivity) {
                    (activity as MainActivity).print()
                }
            }
        }
    }


}