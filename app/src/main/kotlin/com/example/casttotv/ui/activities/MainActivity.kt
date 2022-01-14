package com.example.casttotv.ui.activities

import android.Manifest.permission
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Environment
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintManager
import android.provider.Settings
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.casttotv.R
import com.example.casttotv.databinding.ActivityMainBinding
import com.example.casttotv.ui.activities.browser.frags.BrowserContainerFragment
import com.example.casttotv.utils.MySingleton
import com.example.casttotv.utils.MySingleton.setAppLocale
import com.example.casttotv.utils.MySingleton.toastShort
import com.example.casttotv.viewmodel.BrowserViewModel
import com.example.casttotv.viewmodel.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : BaseActivity() {
    private var originalContext: Context? = null


    private lateinit var binding: ActivityMainBinding
    lateinit var controller: NavController
    lateinit var navHostFragment: NavHostFragment
    private val viewModel: MainViewModel by viewModels()
    private val browserVM: BrowserViewModel by viewModels {
        BrowserViewModel.BrowserViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)

        navHostFragment =
            supportFragmentManager.findFragmentById(com.example.casttotv.R.id.nav_host) as NavHostFragment
        controller = navHostFragment.navController


    }


    override fun onResume() {
        super.onResume()
        if (!checkPermission()) {
            showPermissionDialog()
        }

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
            val read = ContextCompat.checkSelfPermission(
                this, permission.READ_EXTERNAL_STORAGE
            )
            read == PackageManager.PERMISSION_GRANTED
        }

    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ContextWrapper(newBase.setAppLocale(MySingleton.localeLanguage)))
        this.originalContext = newBase;

    }

    fun print() {
        if (browserVM.webViewVisisble()) {
            val printManager =
                originalContext?.getSystemService(Context.PRINT_SERVICE) as PrintManager
            val printAdapter: PrintDocumentAdapter? =
                browserVM.webView.value?.run { createPrintDocumentAdapter("MyDocument") }
            val jobName = " Print Test"
            printAdapter?.let {
                CoroutineScope(Dispatchers.Main).launch {
                    printManager.print(jobName, it, PrintAttributes.Builder().build())
                }
            }
        } else {
            CoroutineScope(Dispatchers.Main).launch {
                toastShort(getString(R.string.make_sure_you_are_in_browser_view))
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
                if (browserVM.canGoBack() || !browserVM.showBroswerHome.value!! ||
                    browserVM.tabFragmentIsShowing()) {
                    browserVM.mainBackPress(true)
                } else {
                    browserVM.exitDialog(this)
                }

            }
            viewModel.languageDialogIsShowing() -> {
                viewModel.cancelLanguageDialog()
            }
            else -> {
                super.onBackPressed()
            }
        }

    }
}