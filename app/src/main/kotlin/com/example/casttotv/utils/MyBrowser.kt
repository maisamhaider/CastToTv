package com.example.casttotv.utils

import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient

class MyBrowser : WebViewClient() {
    val TAG = "MyBrowser"
    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
//        view.loadUrl(url)
        Log.e(TAG, "title---->${view.title.toString()}\n url----->${view.url.toString()}")
        return true
    }
}