package com.example.casttotv.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import androidx.lifecycle.ViewModel
import com.example.casttotv.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabItem
import com.google.android.material.tabs.TabLayout

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

    fun bottomSheet(layout: View, context: Context) {

        // on below line we are creating a new bottom sheet dialog.
        val dialog = BottomSheetDialog(context)
        // on below line we are inflating a layout file which we have created.

        val view = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_dialog, null, false)


        // on below line we are creating a variable for our button
        // which we are using to dismiss our dialog.
        //val btnClose = view.findViewById<Button>(R.id.idBtnDismiss)

        // on below line we are adding on click listener
        // for our dismissing the dialog button.

        // below line is use to set cancelable to avoid
        // closing of dialog box when clicking on the screen.
        dialog.setCancelable(true)

        // on below line we are setting
        // content view to our view.
        dialog.setContentView(view)
        val viewContainer = view.findViewById<FrameLayout>(R.id.fl_bottom_layout_container)
        val tabLayout = view.findViewById<TabLayout>(R.id.tabLayout)

//        val tab = TabLayout.Tab()
//        tab.icon =
//        tabLayout.addTab(tab)
        viewContainer.addView(layout)
        // on below line we are calling
        // a show method to display a dialog.
        dialog.show()


    }
}

class MyBrowser : WebViewClient() {
    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
        view.loadUrl(url)
        return true
    }
}