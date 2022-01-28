package com.example.casttotv.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import com.example.casttotv.utils.Pref.getPrefs

class ObservableWebView : WebView {
    private var onScrollChangedCallback: OnScrollChangedCallback? = null
    private val cookieManager: CookieManager = CookieManager.getInstance()!!

    constructor(context: Context?) : super(context!!)
    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs)

     constructor(context: Context?, attrs: AttributeSet, defStyle: Int) : super(
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

    private fun initValue() {
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

    fun bitmap(): Bitmap? {
        val width = EasyViewUtils.dp2px(context, 200).toInt()
        val height = EasyViewUtils.dp2px(context, 200).toInt()

        if (width > 0 && height > 0) {
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            val canvas = Canvas(bitmap)
            val left: Int = this.scrollX
            val top: Int = this.scrollY
            canvas.translate(-left.toFloat(), -top.toFloat())
            val scaleX: Float = width.toFloat() / this.width
            val scaleY: Float = height.toFloat() / this.height
            canvas.scale(scaleX, scaleY, left.toFloat(), top.toFloat())
            this.draw(canvas)
            canvas.setBitmap(null)
            return bitmap
        }
        return null
    }


    class



    MyBrowser : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            return true
        }
    }
}