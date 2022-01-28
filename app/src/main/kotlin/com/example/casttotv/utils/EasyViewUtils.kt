package com.example.casttotv.utils

import android.content.Context
import android.util.TypedValue

object EasyViewUtils {
    fun dp2px(context: Context?, dpValue: Int): Float {
        return if (context == null) {
            0f
        } else TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
            dpValue.toFloat(),
            context.resources.displayMetrics)
    }
}