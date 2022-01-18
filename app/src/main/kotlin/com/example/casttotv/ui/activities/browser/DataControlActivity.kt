package com.example.casttotv.ui.activities.browser

import android.os.Bundle
import androidx.activity.viewModels
import com.example.casttotv.databinding.ActivityDataControlBinding
import com.example.casttotv.ui.activities.BaseActivity
import com.example.casttotv.viewmodel.BrowserViewModel

class DataControlActivity : BaseActivity() {
    private lateinit var _binding: ActivityDataControlBinding
    private val binding get() = _binding

    private val browserViewModel: BrowserViewModel by viewModels {
        BrowserViewModel.BrowserViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDataControlBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {
            browserVM = browserViewModel
            dataControlLayout1.browserVM = browserViewModel
            dataControlLayout2.browserVM = browserViewModel
            dataControlActivity = this@DataControlActivity
        }
    }

    fun back() {
        finish()
    }
}