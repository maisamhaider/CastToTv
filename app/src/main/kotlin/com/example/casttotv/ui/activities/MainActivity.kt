package com.example.casttotv.ui.activities

import android.Manifest.permission
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.*
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Environment
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintManager
import android.provider.Settings
import android.view.View
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.casttotv.R
import com.example.casttotv.databinding.ActivityMainBinding
import com.example.casttotv.ui.activities.browser.frags.BrowserContainerFragment
import com.example.casttotv.ui.fragments.HomeFragment
import com.example.casttotv.utils.Internet
import com.example.casttotv.utils.MySingleton.toastShort
import com.example.casttotv.viewmodel.BrowserViewModel
import com.example.casttotv.viewmodel.MainViewModel
import com.example.casttotv.viewmodel.SharedViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var controller: NavController
    private lateinit var navHostFragment: NavHostFragment
    private val viewModel: MainViewModel by viewModels()
    val browserVM: BrowserViewModel by viewModels {
        BrowserViewModel.BrowserViewModelFactory(this)
    }
    private val sharedVm: SharedViewModel by viewModels {
        SharedViewModel.SharedViewModelFactory(this)
    }
    private var connection: ConnectivityManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)

        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment
        controller = navHostFragment.navController

        binding.apply {
            layoutExitButton.textViewNotNow.setOnClickListener {
                clBottomSheet.visibility = View.GONE
            }
            layoutExitButton.textViewExit.setOnClickListener { finish() }
            refreshAd2(layoutExitAd.flAdplaceholder, ::close)
            clBottomSheet.setOnClickListener { it.visibility = View.GONE }
        }
    }

    fun restartActivity() {
        recreate()
    }

    fun close() {
        binding.clBottomSheet.visibility = View.GONE
    }

    private var networkCallbackWiFi = object : ConnectivityManager.NetworkCallback() {
        override fun onLost(network: Network) {
            CoroutineScope(Dispatchers.Main).launch { sharedVm.wifiCon(false) }
        }

        override fun onAvailable(network: Network) {
            CoroutineScope(Dispatchers.Main).launch {
                refreshAd2(binding.layoutExitAd.flAdplaceholder, ::close)
                sharedVm.wifiCon(true)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (!checkPermission()) {
            showPermissionDialog()
        }
        connection = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkRequestWiFi = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .build()
        connection!!.registerNetworkCallback(networkRequestWiFi, networkCallbackWiFi)
    }

    private fun showPermissionDialog() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                intent.data = Uri.parse(String.format("package:%s", packageName))
                startActivity(intent)
            } catch (e: Exception) {
                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                startActivity(intent)
            }
        } else ActivityCompat.requestPermissions(
            this, arrayOf(permission.READ_EXTERNAL_STORAGE), 333
        )
    }

    private fun checkPermission(): Boolean {
        return if (SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            val read = ContextCompat.checkSelfPermission(this, permission.READ_EXTERNAL_STORAGE)
            read == PackageManager.PERMISSION_GRANTED
        }
    }

    fun print() {
        if (browserVM.webViewVisible()) {
            val printManager =
                originalContext?.getSystemService(Context.PRINT_SERVICE) as PrintManager
            val printAdapter: PrintDocumentAdapter? =
                browserVM.webView.value?.run { createPrintDocumentAdapter("MyDocument") }
            val jobName = "Print Test"
            printAdapter?.let {
                CoroutineScope(Dispatchers.Main).launch {
                    printManager.print(jobName, it, PrintAttributes.Builder().build())
                }
            }
        } else {
            CoroutineScope(Dispatchers.Main).launch {
                toastShort(getString(R.string.no_site_loaded))
            }
        }
    }

    fun fromBrowserBack() =
        controller.navigate(R.id.action_browserContainerFragment_to_homeFragment)

    private val FragmentManager.currentNavigationFragment: Fragment?
        get() = primaryNavigationFragment?.childFragmentManager?.fragments?.first()

    override fun onBackPressed() {
        val currentFragment = supportFragmentManager.currentNavigationFragment
        when {
            (currentFragment is BrowserContainerFragment) -> {
                if (browserVM.canGoBack() || !browserVM.showBrowserHome.value!! ||
                    browserVM.tabFragmentIsShowing()
                ) {
                    browserVM.mainBackPress(true)
                } else {
                    browserVM.exitDialog(this)
                }
            }
            viewModel.languageDialogIsShowing() -> {
                viewModel.cancelLanguageDialog()
            }
            (currentFragment is HomeFragment) -> {
                if (binding.clBottomSheet.isVisible) {
                    binding.clBottomSheet.visibility = View.GONE
                } else {
                    binding.clBottomSheet.visibility = View.VISIBLE
                }
                if (Internet(this).isInternetAvailable()) {
                    binding.layoutExitAd.clAd.visibility = View.VISIBLE
                } else {
                    binding.layoutExitAd.clAd.visibility = View.GONE
                }
            }
            else -> super.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (connection != null) {
            connection!!.unregisterNetworkCallback(networkCallbackWiFi)
        }
    }
}