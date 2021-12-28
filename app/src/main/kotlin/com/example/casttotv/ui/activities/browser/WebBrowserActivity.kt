package com.example.casttotv.ui.activities.browser

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.webkit.*
import android.widget.TextView.OnEditorActionListener
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.casttotv.adapter.*
import com.example.casttotv.databinding.ActivityWebBrowserBinding
import com.example.casttotv.utils.MySingleton.toastShort
import com.example.casttotv.viewmodel.BrowserViewModel
import java.util.*


class WebBrowserActivity : AppCompatActivity() {

    private lateinit var _binding: ActivityWebBrowserBinding
    private val binding get() = _binding

    private val viewModel: BrowserViewModel by viewModels {
        BrowserViewModel.BrowserViewModelFactory(this)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityWebBrowserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel.initWebViewContainer(binding.webViewContainer)
        viewModel.newTabWebView(WebView(this))

        binding.apply {
            lifecycleOwner = this@WebBrowserActivity
            browserVM = viewModel
            imageviewRefresh.setOnClickListener {
                if (isSearchValid()) {
                    viewModel.searchReload(mInputEdittext.text.toString())
                }
            }
            imageviewMore.setOnClickListener {
                viewModel.bottomSheetMenu(this@WebBrowserActivity)
            }
            imageviewTabs.setOnClickListener {
                viewModel.showBottomSheetTabPreview(supportFragmentManager)
            }

        }


        binding.mInputEdittext.setOnEditorActionListener(OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                if (isSearchValid()) {
                    viewModel.search(binding.mInputEdittext.text.toString())
                } else toastShort("empty search")
                return@OnEditorActionListener true
            }
            false
        })
    }

    private fun isSearchValid() = viewModel.isSearchValid(binding.mInputEdittext.text.toString())


    fun back() = viewModel.back()


    @SuppressLint("DefaultLocale")

    override fun onBackPressed() {
        if (viewModel.canGoBack()) {
            back()
        } else {
            viewModel.exitDialog(this)
        }
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
    }

}