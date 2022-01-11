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
            thisActivity = this@StartControlActivity
            includeJavascriptCookies.browserVM = browserViewModel
            includeJavascript.browserVM = browserViewModel
            includeCookies.browserVM = browserViewModel
        }
    }

    fun back() {
        finish()
    }

    fun intent(whiteListType: String) {
        startActivity(Intent(this@StartControlActivity, WhitelistActivity()::class.java).apply {
            putExtra(WHITE_LIST_TYPE, whiteListType)
        })
    }
}