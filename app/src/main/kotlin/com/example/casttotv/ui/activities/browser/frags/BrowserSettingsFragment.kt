package com.example.casttotv.ui.activities.browser.frags

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.casttotv.R
import com.example.casttotv.databinding.FragmentBrowserSettingsBinding
import com.example.casttotv.ui.activities.browser.BehaviorAndUIActivity
import com.example.casttotv.ui.activities.browser.DataControlActivity
import com.example.casttotv.ui.activities.browser.StartControlActivity
import com.example.casttotv.utils.CUSTOM_SEARCH_ENGINE
import com.example.casttotv.utils.CUSTOM_USER_AGENT
import com.example.casttotv.utils.FAVORITE_DEFAULT_SITE
import com.example.casttotv.viewmodel.BrowserViewModel

class BrowserSettingsFragment : Fragment() {
    lateinit var _binding: FragmentBrowserSettingsBinding
    private val binding get() = _binding
    private val browserViewModel: BrowserViewModel by viewModels {
        BrowserViewModel.BrowserViewModelFactory(requireContext())

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentBrowserSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {

            includeBrowserSettings.clFavoriteSite.setOnClickListener {
                browserViewModel.inputDialog(getString(R.string.favorite), FAVORITE_DEFAULT_SITE)
            }
            includeBrowserSettings.clCustomSearch.setOnClickListener {
                browserViewModel.inputDialog(getString(R.string.custom_search_engine),
                    CUSTOM_SEARCH_ENGINE)
            }
            includeBrowserSettings.clCustomUserAgent.setOnClickListener {
                browserViewModel.inputDialog(getString(R.string.custom_user_agent),
                    CUSTOM_USER_AGENT)
            }

            includeBrowserSettings.clDataControl.setOnClickListener {
                startActivity(Intent(requireContext(), DataControlActivity::class.java))
            }
            includeBrowserSettings.clStartControl.setOnClickListener {
                startActivity(Intent(requireContext(), StartControlActivity::class.java))
            }
            includeBrowserSettings.clSearchEngine.setOnClickListener {
                browserViewModel.engineDialog()
            }
            //
            includeSettingsLayout.clBehaviorUi.setOnClickListener {
                startActivity(Intent(requireContext(), BehaviorAndUIActivity::class.java))
            }
//            includeSettingsLayout.clBookmarkFilter.setOnClickListener {
//                startActivity(Intent(this@BrowserSettingsActivity,
//                    BookmarkFilterActivity::class.java))
//            }
//            includeSettingsLayout.clGestures.setOnClickListener {
//                startActivity(Intent(this@BrowserSettingsActivity,
//                    GesturesActivity::class.java))
//            }
//            includeSettingsLayout.clBackup.setOnClickListener {
//                startActivity(Intent(this@BrowserSettingsActivity,
//                    BackupActivity::class.java))
//            }
            includeSettingsLayout.clAppSettings.setOnClickListener {

                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", requireContext().packageName, null)
                }
                startActivity(intent)
            }
        }

    }

}