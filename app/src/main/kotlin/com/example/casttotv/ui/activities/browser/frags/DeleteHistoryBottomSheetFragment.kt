package com.example.casttotv.ui.activities.browser.frags

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.casttotv.R
import com.example.casttotv.databinding.FragmentDeleteHistoryBottomSheetBinding
import com.example.casttotv.viewmodel.BrowserViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class DeleteHistoryBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var _binding: FragmentDeleteHistoryBottomSheetBinding
    private val binding get() = _binding

    val browserViewModel: BrowserViewModel by activityViewModels {
        BrowserViewModel.BrowserViewModelFactory(requireContext())
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogStyle)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentDeleteHistoryBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            clLastHour.setOnClickListener { browserViewModel.setHistoryTimeConstraint(1) }
            clToday.setOnClickListener { browserViewModel.setHistoryTimeConstraint(2) }
            clLastWeek.setOnClickListener { browserViewModel.setHistoryTimeConstraint(3) }
            clLastMonth.setOnClickListener { browserViewModel.setHistoryTimeConstraint(4) }
            clClearAll.setOnClickListener { browserViewModel.setHistoryTimeConstraint(5) }
        }
    }

}