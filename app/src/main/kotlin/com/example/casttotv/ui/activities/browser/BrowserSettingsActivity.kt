package com.example.casttotv.ui.activities.browser

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.viewModels
import com.example.casttotv.R
import com.example.casttotv.databinding.ActivityBrowserSettingsBinding
import com.example.casttotv.ui.activities.BaseActivity
import com.example.casttotv.utils.CUSTOM_SEARCH_ENGINE
import com.example.casttotv.utils.FAVORITE_DEFAULT_SITE
import com.example.casttotv.viewmodel.BrowserViewModel

class BrowserSettingsActivity : BaseActivity() {
    private lateinit var _binding: ActivityBrowserSettingsBinding
    private val binding get() = _binding

    private val browserViewModel: BrowserViewModel by viewModels {
        BrowserViewModel.BrowserViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityBrowserSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            browserSettingsActivity = this@BrowserSettingsActivity

            includeBrowserSettings.clFavoriteSite.setOnClickListener {
                browserViewModel.inputDialog(getString(R.string.favorite), FAVORITE_DEFAULT_SITE)
            }
            includeBrowserSettings.clCustomSearch.setOnClickListener {
                browserViewModel.inputDialog(getString(R.string.custom_search_engine),
                    CUSTOM_SEARCH_ENGINE)
            }
            includeBrowserSettings.clDataControl.setOnClickListener {
                startActivity(Intent(this@BrowserSettingsActivity,
                    DataControlActivity::class.java))
            }
            includeBrowserSettings.clStartControl.setOnClickListener {
                startActivity(Intent(this@BrowserSettingsActivity,
                    StartControlActivity::class.java))
            }
            includeBrowserSettings.clSearchEngine.setOnClickListener {
                browserViewModel.engineDialog()
            }
            //
            includeSettingsLayout.clBehaviorUi.setOnClickListener {
                startActivity(Intent(this@BrowserSettingsActivity,
                    BehaviorAndUIActivity::class.java))
            }
            includeSettingsLayout.clAppSettings.setOnClickListener {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", packageName, null)
                }
                startActivity(intent)
            }
        }
    }

    fun back() {
        finish()
    }
}