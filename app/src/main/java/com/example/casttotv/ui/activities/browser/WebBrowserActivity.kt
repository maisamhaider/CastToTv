package com.example.casttotv.ui.activities.browser

import android.os.Bundle
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import android.widget.TextView.OnEditorActionListener
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.casttotv.adapter.BrowserAdapter2
import com.example.casttotv.databinding.ActivityWebBrowserBinding
import com.example.casttotv.ui.activities.browser.fragments.BrowserBottomSheetFragment
import com.example.casttotv.utils.BEHAVIOR_UI_CONFIRM_BROWSER_EXIT
import com.example.casttotv.utils.MySingleton.toastShort
import com.example.casttotv.viewmodel.BrowserViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class WebBrowserActivity : AppCompatActivity() {

    private lateinit var _binding: ActivityWebBrowserBinding
    private val binding get() = _binding

    private val viewModel: BrowserViewModel by viewModels {
        BrowserViewModel.BrowserViewModelFactory(this)
    }

    private val modalBottomSheet = BrowserBottomSheetFragment()

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
                bottomSheet()
            }
            imageviewTabs.setOnClickListener {
                showBottomSheet()
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
    private fun showBottomSheet() {
        modalBottomSheet.show(supportFragmentManager, BrowserBottomSheetFragment.TAG)
    }

    fun back() = viewModel.back()

    private fun bottomSheet() {

        // on below line we are creating a new bottom sheet dialog.
        val dialog = BottomSheetDialog(this)
        // on below line we are inflating a layout file which we have created.

        val bottomBinding =
            LayoutInflater.from(this)
                .inflate(com.example.casttotv.R.layout.bottom_sheet_dialog, null, false)


        // closing of dialog box when clicking on the screen.
        dialog.setCancelable(true)

        // on below line we are setting
        // content view to our view.
        dialog.setContentView(bottomBinding)

        val viewpager2 =
            bottomBinding.findViewById<ViewPager2>(com.example.casttotv.R.id.viewpager_2)
        val tabLayout = bottomBinding.findViewById<TabLayout>(com.example.casttotv.R.id.tabLayout)

        val adapter = BrowserAdapter2(this)
        viewpager2.adapter = adapter

        TabLayoutMediator(tabLayout, viewpager2) { tab, position ->
            when (position) {
                0 -> {
                    tab.icon = getDrawable(com.example.casttotv.R.drawable.ic_tab)
                }
                1 -> {
                    tab.icon = getDrawable(com.example.casttotv.R.drawable.ic_share)
                }
                2 -> {
                    tab.icon = getDrawable(com.example.casttotv.R.drawable.ic_save)
                }
                3 -> {
                    tab.icon = getDrawable(com.example.casttotv.R.drawable.ic_more_vert)
                }
            }
        }.attach()
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab!!.position) {
                    0 -> {
                        viewpager2.currentItem = 0
                    }
                    1 -> {
                        viewpager2.currentItem = 1
                    }
                    2 -> {
                        viewpager2.currentItem = 2
                    }
                    3 -> {
                        viewpager2.currentItem = 3
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

        })

        dialog.show()
    }

    override fun onBackPressed() {
        if (viewModel.canGoBack()) {
            back()
        } else {
            if (viewModel.getBooleanPrefs(BEHAVIOR_UI_CONFIRM_BROWSER_EXIT, false)) {
                viewModel.exitDialog(this)
            } else {
                super.onBackPressed()
            }
        }
    }

}