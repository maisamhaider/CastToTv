package com.example.casttotv.ui.activities.browser.frags

import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.example.casttotv.R
import com.example.casttotv.adapter.HomeAdapter
import com.example.casttotv.adapter.TabsAdapter
import com.example.casttotv.database.entities.HomeEntity
import com.example.casttotv.databinding.FragmentBrowserHomeBinding
import com.example.casttotv.ui.activities.MainActivity
import com.example.casttotv.ui.activities.browser.BrowserSettingsActivity
import com.example.casttotv.utils.*
import com.example.casttotv.utils.MySingleton.historyBookFavClose
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
        viewModel.initWebViewContainer(binding.webViewContainer)

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

        viewModel.showBroswerHome.observe(viewLifecycleOwner) { show ->
            if (show) {
                viewModel.setSearchText("")
                binding.clHome.visibility = View.VISIBLE
                binding.clWeb.visibility = View.GONE
                binding.webViewContainer.visibility = View.GONE
            } else {
                binding.clHome.visibility = View.GONE
                binding.clWeb.visibility = View.VISIBLE
                binding.webViewContainer.visibility = View.VISIBLE


            }
        }
        if (requireContext().getPrefs(FAVORITE_DEFAULT_SITE, "").isNotBlank()) {
            //if there is user default site load it on start of this fragment
            viewModel.searchFromHistory(requireContext().getPrefs(FAVORITE_DEFAULT_SITE, ""))
        }
        loadHomeData()

        viewModel.mainActivityBackPress.observe(viewLifecycleOwner) {
            if (it) {
                when {
                    viewModel.tabFragmentIsShowing() -> {
                        viewModel.clickTabLayout()
                    }
                    viewModel.canGoBack() -> {
                        viewModel.back()
                    }
                    !viewModel.showBroswerHome.value!! -> {
                        viewModel.showBroswerHome(true)
                    }
                }
            }
        }
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
            imageviewMic.setOnClickListener {
                displaySpeechRecognizer()
            }
        }

        binding.mInputEdittext.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                if (isSearchValid()) {
                    viewModel.search(binding.mInputEdittext.text.toString())
                }
                return@OnEditorActionListener true
            }
            false
        })

        if (historyBookFavClose != "") {
            viewModel.searchFromHistory(historyBookFavClose)
            historyBookFavClose = ""
        }


    }

    private fun loadHomeData() {
        val adapter = HomeAdapter(::onClick, requireContext())
        binding.recyclerView.adapter = adapter
        viewModel.getHome().observe(viewLifecycleOwner, {
            val list: MutableList<HomeEntity> = ArrayList()
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


    private fun displaySpeechRecognizer() {
        val sRIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            ).putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                requireContext().getPrefs(LOCALE_LANGUAGE, "en"))
        }
        intentLauncher.launch(sRIntent)
    }

    private var intentLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            if (result.data != null) {

                result.data!!.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    .let { results ->
                        results!![0]
                    }?.let { query ->
                        viewModel.setSearchText(query)
                        viewModel.search(query)
                    }

            }

        }
    }

    fun onClick(homeEntity: HomeEntity, longClick: Boolean) {
        if (longClick) {
            if (homeEntity.id != -1) {
                viewModel.deleteDialog(homeEntity.id)
            }
        } else {
            if (homeEntity.id == -1) {
                viewModel.inputHomeDialog(getString(R.string.website_link))
            } else {
                viewModel.search(homeEntity.link)
            }
        }


    }

    override fun onDestroy() {
        super.onDestroy()
    }
}