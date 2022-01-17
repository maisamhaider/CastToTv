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
import java.util.*

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
            clLastHour.setOnClickListener {
                setHistoryTimeConstraint(1)
            }
            clToday.setOnClickListener {
                setHistoryTimeConstraint(2)
            }
            clLastWeek.setOnClickListener {
                setHistoryTimeConstraint(3)
            }
            clLastMonth.setOnClickListener {
                setHistoryTimeConstraint(4)
            }
            clClearAll.setOnClickListener {
                setHistoryTimeConstraint(5)
            }
        }
    }

    private fun setHistoryTimeConstraint(timeConstraint: Int) {
        val cal = Calendar.getInstance()
        val to = cal.timeInMillis
        var from = 0L
        when (timeConstraint) {
            1 -> {//hourly
                cal.add(Calendar.HOUR, -1)
                from = cal.timeInMillis
                browserViewModel.deleteHistory(from, to)
            }
            2 -> { //today
                cal.add(Calendar.DAY_OF_YEAR, -1)
                from = cal.timeInMillis
                browserViewModel.deleteHistory(from, to)
            }
            3 -> { //week
                cal.add(Calendar.WEEK_OF_MONTH, -1)
                from = cal.timeInMillis
                browserViewModel.deleteHistory(from, to)
            }
            4 -> { //month
                cal.add(Calendar.MONTH, -1)
                from = cal.timeInMillis
                browserViewModel.deleteHistory(from, to)
            }
            5 -> { //all
                browserViewModel.deleteHistory()
            }
        }
    }


}