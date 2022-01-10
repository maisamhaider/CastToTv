package com.example.casttotv.ui.activities.browser.frags

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.webkit.WebView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.example.casttotv.R
import com.example.casttotv.databinding.FragmentBrowserHomeBinding
import com.example.casttotv.ui.activities.browser.BrowserSettingsActivity
import com.example.casttotv.utils.MySingleton.toastShort
import com.example.casttotv.utils.Pref.getPrefs
import com.example.casttotv.utils.SELECTED_ENGINE
import com.example.casttotv.utils.THEME_DARK
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
        viewModel.newTabWebView(WebView(requireContext()))


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
//            imageviewRefresh.setOnClickListener {
//                if (isSearchValid()) {
//                    viewModel.searchReload(mInputEdittext.text.toString())
//                }
//            }
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

            val dark = requireContext().getPrefs(THEME_DARK, false)

            if (dark) {
                imageviewSunMoon.setImageResource(R.drawable.ic_moon)
            } else {
                imageviewSunMoon.setImageResource(R.drawable.ic_sun)
            }
            imageviewSunMoon.setOnClickListener {
                homeVM.themeDialog(requireContext())
            }
        }

        binding.mInputEdittext.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                if (isSearchValid()) {
                    binding.clHome.visibility = View.GONE
                    binding.webViewContainer.visibility = View.VISIBLE
                    viewModel.search(binding.mInputEdittext.text.toString())
                } else requireContext().toastShort("empty search")
                return@OnEditorActionListener true
            }
            false
        })
    }
}