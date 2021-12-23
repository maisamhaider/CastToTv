package com.example.casttotv.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.casttotv.R
import com.example.casttotv.adapter.LanguagesAdapter
import com.example.casttotv.databinding.HomeFragmentBinding
import com.example.casttotv.ui.activities.browser.WebBrowserActivity
import com.example.casttotv.utils.MySingleton
import com.example.casttotv.utils.MySingleton.toastShort
import com.example.casttotv.viewmodel.MainViewModel

class HomeFragment : Fragment() {

    private var _navController: NavController? = null
    private var _dataBinding: HomeFragmentBinding? = null

    private val binding get() = _dataBinding!!
    private val navController get() = _navController!!

    val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _dataBinding = HomeFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _navController = NavHostFragment.findNavController(this)
        binding.apply {
            homeFragment = this@HomeFragment
            includedDrawer.homeFrag = this@HomeFragment
        }


        val adapter = LanguagesAdapter(requireContext())
        binding.apply {
        }
        val recyclerView = binding.includedLanguages.recyclerview
        recyclerView.adapter = adapter
        try {
            adapter.submitList(viewModel.list())
        } catch (e: Exception) {
            e.stackTrace
        }
        viewModel.setView(binding.includedLanguages.root)

    }

    fun languagesDialog() {
        MySingleton.LANGUAGE_DIALOG_SHOWING = true
        viewModel.viewVisibility(MySingleton.LANGUAGE_DIALOG_SHOWING)
    }

    fun openCloseDrawer() {

    }

    fun closeDrawer() {
        binding.drawerLayout.closeDrawer(GravityCompat.START)
    }

    fun openDrawer() {
        binding.drawerLayout.openDrawer(GravityCompat.END)
    }


    fun goToScreenMirroring() {
        navController.navigate(R.id.action_homeFragment_to_screenMirroringFragment)
    }

    fun goToImages() {
        navController.navigate(R.id.action_homeFragment_to_imagesFoldersFragment)
        requireContext().toastShort("click")
    }

    fun goToVideos() {
        navController.navigate(R.id.action_homeFragment_to_videosFoldersFragment)
    }

    fun goToWebBrowser() {
        startActivity(Intent(requireContext(), WebBrowserActivity::class.java))
    }

    fun goToImageSlider() {
        navController.navigate(R.id.action_homeFragment_to_sliderImagesFoldersFragment)
    }

    fun goToAppSettings() {
        navController.navigate(R.id.action_homeFragment_to_appSettingsFragment)
    }

    fun goToTutorial() {
        navController.navigate(R.id.action_homeFragment_to_tutorialFragment)
    }

//    fun goToVpn() {
//        navController.navigate(R.id.action_homeFragment_to_vpnFragment)
//    }

    //    fun goToAudios() {
//        navController.navigate(R.id.action_homeFragment_to_audiosFoldersFragment)
//    }
//


}
