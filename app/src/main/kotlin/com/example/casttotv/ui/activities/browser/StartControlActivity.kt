package com.example.casttotv.ui.activities.browser

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.casttotv.databinding.*
import com.example.casttotv.utils.*
import com.example.casttotv.viewmodel.BrowserViewModel

class StartControlActivity : AppCompatActivity() {
    private lateinit var _binding: ActivityStartControlBinding
    private val binding get() = _binding


    private val browserViewModel: BrowserViewModel by viewModels {
        BrowserViewModel.BrowserViewModelFactory(this)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityStartControlBinding.inflate(layoutInflater)
        setContentView(binding.root)



        binding.apply {
            includeJavascriptCookies.browserVM = browserViewModel
            includeAdblock.browserVM = browserViewModel
            includeJavascript.browserVM = browserViewModel
            includeCookies.browserVM = browserViewModel

            includeAdblock.clAdblockWhitelist.setOnClickListener {
                intent(WHITE_LIST_ADBLOCK)
            }
            includeJavascript.clJavaScriptWhitelist.setOnClickListener {
                intent(WHITE_LIST_JAVASCRIPT)
            }
            includeCookies.clCookiesWhitelist.setOnClickListener {
                intent(WHITE_LIST_COOKIES)
            }
        }
    }

    fun intent(whiteListType: String) {
        startActivity(Intent(this@StartControlActivity, WhitelistActivity()::class.java).apply {
            putExtra(WHITE_LIST_TYPE, whiteListType)
        })
    }
}