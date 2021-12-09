package com.example.casttotv.viewmodel

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.casttotv.R
import com.example.casttotv.databinding.InputDialogLayoutBinding
import com.example.casttotv.databinding.LayoutSearchEnginesBinding
import com.example.casttotv.databinding.LayoutTitleBodyPosAndNegButtonBinding
import com.example.casttotv.datasource.DataSource
import com.example.casttotv.utils.Pref.getBoolean
import com.example.casttotv.utils.Pref.getString
import com.example.casttotv.utils.Pref.putBoolean
import com.example.casttotv.utils.Pref.putString
import com.example.casttotv.utils.SELECTED_ENGINE

class BrowserViewModel(private val cxt: Context) : ViewModel() {

    private lateinit var webView: WebView

    val engines = DataSource().engines

    fun putBooleanPrefs(key: String, value: Boolean) {
        cxt.putBoolean(key, value)
    }

    fun getBooleanPrefs(key: String, value: Boolean) = cxt.getBoolean(key, value)


    fun putEngine(value: String) {
        cxt.putString(SELECTED_ENGINE, value)
    }

    fun checkSelectedEngine(value: String): Boolean {
        val getEngine = cxt.getString(SELECTED_ENGINE, "")

        return getEngine == value
    }


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
        var engine = cxt.getString(SELECTED_ENGINE, "https://www.google.com/search?q=")

        engine = if (engine.isEmpty()) {
            "https://www.google.com/search?q="
        } else engine

        this.webView.loadUrl("$engine${searchText}")
    }

    fun isSearchValid(searchText: String): Boolean {
        return searchText.isNotBlank()
    }

    class BrowserViewModelFactory(
        private val context: Context,
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(BrowserViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return BrowserViewModel(context) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    fun engineDialog() {
        val binding = LayoutSearchEnginesBinding.inflate(LayoutInflater.from(cxt), null, false)
        val builder = AlertDialog.Builder(cxt)
        builder.setView(binding.root)
        val dialog = builder.create()

        binding.apply {
            browserVM = this@BrowserViewModel
            mButtonCancel.setOnClickListener {
                dialog.dismiss()
            }
        }

        dialog.show()
    }

    fun exitDialog(activity: Activity) {
        val binding =
            LayoutTitleBodyPosAndNegButtonBinding.inflate(LayoutInflater.from(cxt), null, false)
        val builder = AlertDialog.Builder(cxt)
        builder.setView(binding.root)
        val dialog = builder.create()

        binding.apply {
            textviewTitle.text = "Exit"
            textviewBody.text = "This is exit dialog"
            textviewPositive.text = "Yes"
            textviewNegative.text = "No"
            textviewPositive.setOnClickListener {
                dialog.dismiss()
                activity.finish()
            }
            textviewNegative.setOnClickListener {
                dialog.dismiss()
            }
        }

        dialog.show()
    }

    fun inputDialog(title: String, prefKey: String) {
        val binding = InputDialogLayoutBinding.inflate(LayoutInflater.from(cxt), null, false)
        val builder = AlertDialog.Builder(cxt)
        builder.setView(binding.root)
        val dialog = builder.create()

        binding.apply {
            textviewTitle.text = title
            textviewOk.text = cxt.getString(R.string.ok)
            textviewCancel.text = cxt.getString(R.string.cancel)
            textviewOk.setOnClickListener {
                val input = textInputEditText.text.toString()
                if (TextUtils.isEmpty(input)) {
                    cxt.putString(prefKey, "")
                } else {
                    cxt.putString(prefKey, input)
                }
                dialog.dismiss()
            }
            textviewCancel.setOnClickListener {
                dialog.dismiss()
            }
        }
        dialog.show()
    }

}

class MyBrowser : WebViewClient() {
    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
        view.loadUrl(url)
        return true
    }
}