package com.example.casttotv.ui.activities.browser.fragments

import android.app.Dialog
import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

open class CBottomSheetDialogFragment : BottomSheetDialogFragment() {
    //wanna get the bottomSheetDialog
    private lateinit var dialog: BottomSheetDialog
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.behavior.isDraggable = false
        return dialog
    }

    //set the behavior here
    fun setFullScreen() {
        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    fun setHalfScreen() {
        dialog.behavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
    }

    fun setHideScreen() {
        dialog.behavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

}