package com.example.casttotv.ui.activities.browser.frags

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.asLiveData
import com.bumptech.glide.Glide
import com.example.casttotv.R
import com.example.casttotv.adapter.HomeAdapter
import com.example.casttotv.adapter.TabsAdapter
import com.example.casttotv.database.entities.HomeEntity
import com.example.casttotv.databinding.FragmentBrowserHomeBinding
import com.example.casttotv.ui.activities.MainActivity
import com.example.casttotv.ui.activities.browser.BrowserSettingsActivity
import com.example.casttotv.utils.*
import com.example.casttotv.utils.MySingleton.toastShort
import com.example.casttotv.utils.Pref.getPrefs
import com.example.casttotv.viewmodel.BrowserViewModel
import com.example.casttotv.viewmodel.MainViewModel


class BrowserHomeFragment : Fragment() {
    private lateinit var _binding: FragmentBrowserHomeBinding
    private val binding get() = _binding

    private val viewModel: BrowserViewModel by activityViewModels {
        BrowserViewModel.BrowserViewModelFactory(requireContext())
    }

    private val homeVM: MainViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentBrowserHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.initWebViewContainer(binding.webViewContainer, binding.clWeb)
        viewModel.newTabWebView(ObservableWebView(requireContext()))
    }

    fun settings() {
        startActivity(Intent(requireContext(), BrowserSettingsActivity::class.java))
    }

    private fun isSearchValid() = viewModel.isSearchValid(binding.mInputEdittext.text.toString())


    fun back() = viewModel.back()
    override fun onResume() {
        super.onResume()

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            browserVM = viewModel
            thisFrag = this@BrowserHomeFragment
            refreshView.setOnClickListener {
                if (isSearchValid()) {
                    viewModel.searchReload(mInputEdittext.text.toString())
                }
            }
//            imageviewMore.setOnClickListener {
//                viewModel.bottomSheetMenu(requireActivity())
//            }
//            view.setOnClickListener {
//                viewModel.showBottomSheetTabPreview(requireActivity().supportFragmentManager)
//            }
            val engine = viewModel.engines[viewModel.getPrefs(SELECTED_ENGINE, "").lowercase()]
            Glide.with(requireContext()).load((engine?.engineLogo))
                .placeholder(R.drawable.ic_browser)
                .into(imageviewEngineLogo)
            Glide.with(requireContext()).load((engine?.engineLogo))
                .placeholder(R.drawable.ic_browser)
                .into(imageviewSearchEngineLogo)
            Glide.with(requireContext()).load((engine?.engineLogo))
                .placeholder(R.drawable.ic_browser)
                .into(imageviewSearchEngineLogo2)

            val dark = requireContext().getPrefs(THEME_DARK, false)

            if (dark) {
                imageviewSunMoon.setImageResource(R.drawable.ic_moon)
            } else {
                imageviewSunMoon.setImageResource(R.drawable.ic_sun)
            }
            imageviewSunMoon.setOnClickListener {
                homeVM.themeDialog(requireContext())
            }
            includeTabs.viewNewTab.setOnClickListener {
                viewModel.newTabWebView(ObservableWebView(requireContext()))
                viewModel.clickTabLayout()
            }
        }

        binding.mInputEdittext.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                if (isSearchValid()) {
                    binding.clHome.visibility = View.GONE
                    binding.clWeb.visibility = View.VISIBLE
                    binding.webViewContainer.visibility = View.VISIBLE
                    viewModel.search(binding.mInputEdittext.text.toString())
                }
                return@OnEditorActionListener true
            }
            false
        })

        if (requireContext().getPrefs(FAVORITE_DEFAULT_SITE, "").isNotBlank()) {
            //if there is user default site load it on start of this fragment
            binding.clHome.visibility = View.GONE
            binding.clWeb.visibility = View.VISIBLE
            binding.webViewContainer.visibility = View.VISIBLE
            viewModel.searchFromHistory(requireContext().getPrefs(FAVORITE_DEFAULT_SITE, ""))
        } else {
            //if there is no user default site load show start screen of the browser
            binding.clHome.visibility = View.VISIBLE
            binding.clWeb.visibility = View.GONE
            binding.webViewContainer.visibility = View.GONE
            loadHomeData()
        }
        viewModel.showTabFragment.observe(viewLifecycleOwner, {
            if (it) {
                binding.includeTabs.clTabs.visibility = View.VISIBLE
                loadTab()
            } else {
                binding.includeTabs.clTabs.visibility = View.GONE
            }
        })
        viewModel.webScrollObserver()?.let {
            if (requireContext().getPrefs(BEHAVIOR_UI_HIDE_TOOLBAR, false)) {
                it.setOnScrollChangedCallback(object :
                    ObservableWebView.OnScrollChangedCallback {
                    override fun onScroll(l: Int, t: Int, oldl: Int, oldt: Int) {
                        if (t == 0) {
                            //Do stuff
                            binding.clSearchInput2Header.visibility = View.VISIBLE
                            //Do stuff
                        } else if (t > 40) {
                            binding.clSearchInput2Header.visibility = View.GONE
                        }
                    }
                })
            }


        }
    }

    private fun loadHomeData() {
        val adapter = HomeAdapter(::onClick, requireContext())
        binding.recyclerView.adapter = adapter
        val list: MutableList<HomeEntity> = ArrayList()
        viewModel.getHome().asLiveData().observe(viewLifecycleOwner, {
            list.addAll(it)
            list.add(HomeEntity(-1, getString(R.string.add_shortcut), "", ""))
            adapter.submitList(list)
        })

        if (activity is MainActivity) {
            ((activity) as MainActivity).refreshAdBrowser(binding.addContainer)
        }
    }


    private fun loadTab() {
        val adapter = TabsAdapter(::onTabClicked, viewModel, requireContext())
        binding.includeTabs.recyclerView.adapter = adapter

        viewModel.liveTabs.observe(viewLifecycleOwner, {
            it?.let {
                adapter.submitList(it)
                adapter.notifyDataSetChanged()
            }
        })

    }


    private fun onTabClicked(tab: Int) {
        viewModel.switchToTab(tab)
        viewModel.clickTabLayout()
    }

//    binding.apply {
//        viewNewTab.setOnClickListener {
////                this@BrowserBottomSheetFragment.dismiss()
//            viewModel.newTabWebView(WebView(requireContext()))
//            loadTab()
//        }
//    }

    fun onClick(homeEntity: HomeEntity) {
        if (homeEntity.id == -1) {
            requireContext().toastShort("click")
        } else {
            binding.clHome.visibility = View.GONE
            binding.clWeb.visibility = View.VISIBLE
            binding.webViewContainer.visibility = View.VISIBLE
            viewModel.search(homeEntity.link)
        }

    }
}