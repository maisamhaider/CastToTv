package com.example.casttotv.ui.activities.browser.frags

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.casttotv.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class MenuBottomSheetFragment : BottomSheetDialogFragment() {


    override fun onAttach(context: Context) {
        super.onAttach(context)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogStyle)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_menu_bottom_sheet, container, false)
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