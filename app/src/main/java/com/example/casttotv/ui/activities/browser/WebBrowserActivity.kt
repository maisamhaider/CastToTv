package com.example.casttotv.ui.activities.browser

import android.os.Bundle
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import android.widget.TextView.OnEditorActionListener
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.casttotv.R
import com.example.casttotv.databinding.ActivityWebBrowserBinding
import com.example.casttotv.utils.MySingleton.toastShort
import com.example.casttotv.viewmodel.BrowserViewModel


class WebBrowserActivity : AppCompatActivity() {

    private lateinit var _binding: ActivityWebBrowserBinding
    private val binding get() = _binding

    private val viewModel: BrowserViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityWebBrowserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel.initWebView(binding.webView)
        binding.apply {
            imageviewRefresh.setOnClickListener {
                if (isSearchValid()) {
                    viewModel.search(mInputEdittext.text.toString())
                }
            }

            imageviewMore.setOnClickListener {
                viewModel.bottomSheet(LayoutInflater.from(this@WebBrowserActivity)
                    .inflate(R.layout.bottom_sheet_tabs, null, false),
                    this@WebBrowserActivity)
            }

        }
        binding.mInputEdittext.setOnEditorActionListener(OnEditorActionListener { _, actionId, event ->
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

    override fun onBackPressed() {
        if (viewModel.canGoBack()) {
            back()
        } else {
            super.onBackPressed()
        }
    }

}