package com.example.casttotv.ui.fragments

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.casttotv.BuildConfig
import com.example.casttotv.R
import com.example.casttotv.databinding.HomeFragmentBinding
import com.example.casttotv.interfaces.MyCallBack
import com.example.casttotv.ui.activities.MainActivity
import com.example.casttotv.utils.MySingleton.shareApp
import com.example.casttotv.utils.Pref.getPrefs
import com.example.casttotv.utils.THEME_DARK
import com.example.casttotv.viewmodel.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class HomeFragment : Fragment(), MyCallBack {

    private var _navController: NavController? = null
    private var _dataBinding: HomeFragmentBinding? = null

    private val binding get() = _dataBinding!!
    private val navController get() = _navController!!

    val viewModel: MainViewModel by activityViewModels()

    val version = BuildConfig.VERSION_NAME
    private var connection: ConnectivityManager? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _dataBinding = HomeFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _navController = NavHostFragment.findNavController(this)
        binding.apply {
            homeFragment = this@HomeFragment
            includedDrawer.homeFrag = this@HomeFragment
            includedDrawer.clShare.setOnClickListener {
                requireContext().shareApp()
            }
        }

        val mainActivity = activity as MainActivity
        mainActivity.refreshAd(binding.adContainerBig)
        mainActivity.refreshAdSmallNative(binding.adContainerSmall)

    }

    fun languagesDialog() {
        viewModel.languageDialog(requireContext())
    }


    fun rateUs() {
        viewModel.rateUs(requireContext(), this)
    }

    fun openDrawer() {
        binding.drawerLayout.open()
    }

    private fun setIcons() {
        val dark = requireContext().getPrefs(THEME_DARK, false)
        if (dark) {
            binding.includedDrawer.imageviewSettings.setImageResource(R.drawable.ic_app_settings_dark)
            binding.includedDrawer.imageviewMoreApp.setImageResource(R.drawable.ic_more_app_dark)
            binding.imageviewScreenMirring.setImageResource(R.drawable.ic_screen_mirroring_dark)
            binding.imageviewImages.setImageResource(R.drawable.ic_image_dark)

        } else {
            binding.includedDrawer.imageviewSettings.setImageResource(R.drawable.ic_app_settings_light)
            binding.includedDrawer.imageviewMoreApp.setImageResource(R.drawable.ic_more_app_light)
            binding.imageviewScreenMirring.setImageResource(R.drawable.ic_screen_mirroring_light)
            binding.imageviewImages.setImageResource(R.drawable.ic_image_light)
        }
    }


    fun goToScreenMirroring() {
        navController.navigate(R.id.action_homeFragment_to_screenMirroringFragment)
    }

    fun goToImages() {
        navController.navigate(R.id.action_homeFragment_to_imagesFoldersFragment)
    }

    fun goToVideos() {
        navController.navigate(R.id.action_homeFragment_to_videosFoldersFragment)
    }

    fun goToWebBrowser() {
        navController.navigate(R.id.action_homeFragment_to_browserContainerFragment)
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

    fun goToFeedBack() {
        navController.navigate(R.id.action_homeFragment_to_feedbackFragment)
    }

    fun goToHelpCenter() {
        navController.navigate(R.id.action_homeFragment_to_helpCenterFragment)
    }

    fun goToMoreApps() {
        navController.navigate(R.id.action_homeFragment_to_moreAppsFragment)
    }

    override fun callback() {
        goToFeedBack()
    }

    private var networkCallbackWiFi = object : ConnectivityManager.NetworkCallback() {
        override fun onLost(network: Network) {
            CoroutineScope(Dispatchers.Main).launch {
                binding.textviewWifiState.text = getString(R.string.wifi_disconnected)
                binding.imageviewWifiState.setImageDrawable(ContextCompat.getDrawable(requireContext(),
                    R.drawable.ic_wifi_disconnected))
            }

        }

        override fun onAvailable(network: Network) {
            CoroutineScope(Dispatchers.Main).launch {
                binding.textviewWifiState.text = getString(R.string.wifi_connected)
                binding.imageviewWifiState.setImageDrawable(ContextCompat.getDrawable(requireContext(),
                    R.drawable.ic_wifi_connected))
            }
        }
    }

    override fun onResume() {
        super.onResume()
        connection =
            requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkRequestWiFi = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .build()

        connection!!.registerNetworkCallback(networkRequestWiFi, networkCallbackWiFi)

        setIcons()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (connection != null) {
            connection!!.unregisterNetworkCallback(networkCallbackWiFi)
        }

    }


}
