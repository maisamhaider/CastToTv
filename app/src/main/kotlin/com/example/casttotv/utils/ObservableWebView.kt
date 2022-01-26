package com.example.casttotv.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import android.webkit.CookieManager
import android.webkit.WebView
import com.example.casttotv.R
import com.example.casttotv.utils.Pref.getPrefs


class ObservableWebView : WebView {
    private var onScrollChangedCallback: OnScrollChangedCallback? = null
    private val cookieManager: CookieManager = CookieManager.getInstance()!!

    constructor(context: Context?) : super(context!!)
    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context!!, attrs, defStyle)

    init {
        initValue()
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        if (onScrollChangedCallback != null) {
            onScrollChangedCallback!!.onScroll(l, t, oldl, oldt)
        }
    }

    @JvmName("setOnScrollChangedCallback1")
    fun setOnScrollChangedCallback(onScrollChangedCallback: OnScrollChangedCallback) {
        this.onScrollChangedCallback = onScrollChangedCallback
    }

    fun initValue() {
        webViewClient = MyBrowser()
        settings.javaScriptEnabled = context.getPrefs(START_CONTROL_JAVASCRIPT, true)
        settings.loadsImagesAutomatically = context.getPrefs(START_CONTROL_IMAGE, true)
        this.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
        settings.setGeolocationEnabled(context.getPrefs(START_CONTROL_LOCATION, false))
        cookieManager.setAcceptThirdPartyCookies(this,
            context.getPrefs(START_CONTROL_COOKIES, false))
    }

    fun clearCookies() {
        cookieManager.removeAllCookies(null)
        cookieManager.flush()
    }

    /**
     * Impliment in the activity/fragment/view that you want to listen to the webview
     */
    interface OnScrollChangedCallback {
        fun onScroll(l: Int, t: Int, oldl: Int, oldt: Int)
    }

    fun bitmap(): Bitmap {
        val dimen144dp: Int =
            context.resources.getDimensionPixelSize(R.dimen.layout_width_144dp)
        val dimen108dp: Int =
            context.resources.getDimensionPixelSize(R.dimen.layout_height_108dp)

        val width: Float =
            context.resources.getDimensionPixelSize(R.dimen.layout_width_144dp).toFloat()
        val height: Float =
            context.resources.getDimensionPixelSize(R.dimen.layout_height_108dp).toFloat()

        setLayerType(View.LAYER_TYPE_HARDWARE, null)

        val sBitmap = Bitmap.createBitmap(dimen144dp, dimen108dp, Bitmap.Config.RGB_565)

        val sCanvas = Canvas(sBitmap)

        val left: Int = this.left
        val top: Int = this.top
        val status = sCanvas.save()
        sCanvas.translate(-left.toFloat(), -top.toFloat())

        val scale: Float = width / this.width
        val scale2: Float = height / this.height
        sCanvas.scale(scale, scale, scale2, scale2)

        this.draw(sCanvas)
        sCanvas.restoreToCount(status)
        return sBitmap
    }

}