package com.example.casttotv.ui.activities.browser.frags

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.casttotv.R
import com.example.casttotv.databinding.FragmentMenuBottomSheetBinding
import com.example.casttotv.ui.activities.browser.BookmarkActivity
import com.example.casttotv.ui.activities.browser.FavoritesActivity
import com.example.casttotv.ui.activities.browser.HistoryActivity
import com.example.casttotv.viewmodel.BrowserViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class MenuBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var _binding: FragmentMenuBottomSheetBinding
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
        _binding = FragmentMenuBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            clBookmarks.setOnClickListener {
                startActivity(Intent(requireContext(), BookmarkActivity::class.java))
            }
            clFavorites.setOnClickListener {
                startActivity(Intent(requireContext(), FavoritesActivity::class.java))
            }
            clHistory.setOnClickListener {
                startActivity(Intent(requireContext(), HistoryActivity::class.java))
            }
        }
    }

//    textviewShareScreenshot.setOnClickListener {
//        val ssPath = browserViewModel.saveScreenShot(true) //screenshot return path
//        if (ssPath != "error") {
//            browserViewModel.shareSS(ssPath)
//        } else {
//            requireContext().toastShort("error")
//        }
//    }
//    textviewSharePdf.setOnClickListener {
//        browserViewModel.print()
//    }
//    textviewShareLink.setOnClickListener {
//        browserViewModel.shareLink()
//    }
//    textviewCopyToClipboard.setOnClickListener {
//        browserViewModel.copyToClipBoard()
//        requireContext().toastShort("copied")
//    }
//    textviewOpenLinkWith.setOnClickListener {
//        browserViewModel.openWith()
//    }

}