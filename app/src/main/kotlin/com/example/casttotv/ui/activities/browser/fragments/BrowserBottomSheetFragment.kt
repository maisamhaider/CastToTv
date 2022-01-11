package com.example.casttotv.ui.activities.browser.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.asLiveData
import com.example.casttotv.adapter.BookmarkAdapter
import com.example.casttotv.adapter.HistoryAdapter
import com.example.casttotv.adapter.TabsAdapter
import com.example.casttotv.database.entities.BookmarkEntity
import com.example.casttotv.database.entities.HistoryEntity
import com.example.casttotv.database.entities.HomeEntity
import com.example.casttotv.databinding.FragmentBrowserBottomSheetBinding
import com.example.casttotv.utils.MySingleton.toastShort
import com.example.casttotv.viewmodel.BrowserViewModel
import com.google.android.material.tabs.TabLayout


class BrowserBottomSheetFragment : CBottomSheetDialogFragment() {

    private lateinit var _binding: FragmentBrowserBottomSheetBinding
    private val binding get() = _binding

    val browserViewModel: BrowserViewModel by activityViewModels {
        BrowserViewModel.BrowserViewModelFactory(requireContext())
    }

    private var previousTab = 0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentBrowserBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tabLayoutClicks(binding.tabsLayoutUpper)
        tabLayoutClicks(binding.tabsLayoutLower)
        loadTab()
        binding.apply {
            textviewNewTab2.setOnClickListener {
                this@BrowserBottomSheetFragment.dismiss()
                browserViewModel.newTabWebView(WebView(requireContext()))
                loadTab()
            }
            textviewNewTab.setOnClickListener {
                this@BrowserBottomSheetFragment.dismiss()
                browserViewModel.newTabWebView(WebView(requireContext()))
                loadTab()
            }
            textviewMore.setOnClickListener {
                requireContext().toastShort("more")
                browserViewModel.deleteDialog(previousTab)
            }
        }
    }

    private fun tabLayoutClicks(tabLayout: TabLayout) {
        tabLayout.addOnTabSelectedListener(
            object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    when (tab!!.position) {
                        0 -> {
//                            this@BrowserBottomSheetFragment.dismiss()
//                            browserViewModel.newTabWebView(WebView(requireContext()))
                            loadTab2()
                            previousTab = 0
//                            loadTab()
                        }
//                        1 -> {
//                            requireContext().toastShort("home")
//                            showLowerTabs(1)
//                            loadHomeData()
//                            previousTab = 1
//                        }
                        1 -> {
                            requireContext().toastShort("bookmark")
                            showLowerTabs(1)
                            previousTab = 1
                        }
                        2 -> {
                            requireContext().toastShort("history")
                            showLowerTabs(2)
                            loadHistory()
                            previousTab = 2
                        }

                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                }

            })
    }


    fun showLowerTabs(int: Int) {
        binding.clSmallViews.visibility = View.GONE
        binding.clFullViews.visibility = View.VISIBLE
        binding.tabsLayoutLower.setScrollPosition(int, 0.0f, true)
        setFullScreen()
    }

    fun showUpperTabs() {
        binding.clSmallViews.visibility = View.VISIBLE
        binding.clFullViews.visibility = View.GONE
    }

    fun loadTab() {
        val adapter = TabsAdapter(::onTabClicked, browserViewModel, requireContext())
        binding.recyclerViewTabs.adapter = adapter

        browserViewModel.liveTabs.observe(viewLifecycleOwner, {
            it?.let {
                adapter.submitList(it)
                adapter.notifyDataSetChanged()
            }
        })

    }

    fun loadTab2() {
        val adapter = TabsAdapter(::onTabClicked, browserViewModel, requireContext())
        binding.recyclerView.adapter = adapter
        browserViewModel.liveTabs.observe(viewLifecycleOwner, {
            it?.let {
                adapter.submitList(it)
                adapter.notifyDataSetChanged()
            }
        })

    }



//    fun loadHomeData() {
//        val adapter = HomeAdapter(::onHomeClicked, requireContext())
//        binding.recyclerView.adapter = adapter
//        browserViewModel.getHome().asLiveData().observe(viewLifecycleOwner) {
//            it?.let { homes ->
//                adapter.submitList(homes)
//            }
//        }
//    }

    fun loadHistory() {
        val adapter = HistoryAdapter(::onHistoryClicked, requireContext())
        binding.recyclerView.adapter = adapter
        browserViewModel.getHistory().asLiveData().observe(viewLifecycleOwner) {
            it?.let { history ->
                adapter.submitList(history)
            }
        }
    }


    private fun onTabClicked(tab: Int) {
        browserViewModel.switchToTab(tab)
        this.dismiss()
    }



    private fun onHomeClicked(homeEntity: HomeEntity) {
        requireContext().toastShort(homeEntity.link)
    }

    private fun onHistoryClicked(historyEntity: HistoryEntity, longClick: Boolean) {

        if (longClick) {
//            browserViewModel.deleteHistoryDialog(historyEntity)
            browserViewModel.editBottomSheet(historyEntity, true)

        } else {
            requireContext().toastShort(historyEntity.link)
            browserViewModel.searchFromHistory(historyEntity.link)
            this.dismiss()
        }
    }

    companion object {
        const val TAG = "ModalBottomSheet"
    }
}