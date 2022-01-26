package com.example.casttotv.ui.activities.browser

import android.os.Bundle
import androidx.activity.viewModels
import com.example.casttotv.databinding.ActivityBehaviorAndUiactivityBinding
import com.example.casttotv.ui.activities.BaseActivity
import com.example.casttotv.viewmodel.BrowserViewModel

class BehaviorAndUIActivity : BaseActivity() {
    private lateinit var _binding: ActivityBehaviorAndUiactivityBinding
    private val binding get() = _binding

    private val browserViewModel: BrowserViewModel by viewModels {
        BrowserViewModel.BrowserViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityBehaviorAndUiactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            thisActivity = this@BehaviorAndUIActivity
            includeBehaviorUi1.browserVM = browserViewModel
            includeBehaviorUi2.browserVM = browserViewModel
        }
    }

    fun back() {
        finish()
    }
}