package com.example.casttotv.ui.activities.browser

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.casttotv.databinding.ActivityBehaviorAndUiactivityBinding
import com.example.casttotv.viewmodel.BrowserViewModel

class BehaviorAndUIActivity : AppCompatActivity() {
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
            includeBehaviorUi1.browserVM = browserViewModel
            includeBehaviorUi2.browserVM = browserViewModel
            includeBehaviorUi3.browserVM = browserViewModel
            includeBehaviorUi4.browserVM = browserViewModel
        }
    }
}