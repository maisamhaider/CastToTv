package com.example.casttotv.viewmodel

import android.annotation.SuppressLint
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.lifecycle.ViewModel

class BrowserViewModel : ViewModel() {
    private lateinit var webView: WebView

    @SuppressLint("SetJavaScriptEnabled")
    fun initWebView(webView: WebView) {
        this.webView = webView
        this.webView.webViewClient = MyBrowser()
        this.webView.settings.javaScriptEnabled = true
        this.webView.settings.loadsImagesAutomatically = true
        this.webView.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
    }

    fun back() {
        if (webView.isFocused && webView.canGoBack()) {
            webView.goBack()
        }
    }

    fun canGoBack(): Boolean {
        return (webView.isFocused && webView.canGoBack())
    }

    fun search(searchText: String) {
        this.webView.loadUrl("https://www.google.com/search?q=${searchText}")
    }

    fun isSearchValid(searchText: String): Boolean {
        return searchText.isNotBlank()
    }

}

class MyBrowser : WebViewClient() {
    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
        view.loadUrl(url)
        return true
    }
}