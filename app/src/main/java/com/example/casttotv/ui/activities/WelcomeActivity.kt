package com.example.casttotv.ui.activities

import android.content.Intent
import android.location.Location
import android.location.LocationListener
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.example.casttotv.LocationItem
import com.example.casttotv.R
import com.example.casttotv.databinding.ActivityWelcomeBinding
import com.example.casttotv.utils.MySingleton.toastLong
import com.example.casttotv.utils.Pref.COUNTRY
import com.example.casttotv.utils.Pref.NO_COUNTRY
import com.example.casttotv.utils.Pref.SHOW_PERMISSION_LAYOUT
import com.example.casttotv.utils.Pref.SHOW_SPLASH_LAYOUT
import com.example.casttotv.utils.Pref.getBoolean
import com.example.casttotv.utils.Pref.getString
import com.example.casttotv.utils.Pref.putBoolean
import com.example.casttotv.utils.Pref.putString
import com.example.casttotv.viewmodel.WelcomeViewModel
import kotlinx.coroutines.*

class WelcomeActivity : AppCompatActivity() {

    private val viewModel: WelcomeViewModel by viewModels {
        WelcomeViewModel.WelcomeFactory(this)
    }
    lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_welcome)
    }

    /**
     * This fun function Visible splash layout,
     * wait for for 5 seconds
     * and then go to the MainActivity
     * finally finish (user will be exit on back press from mainActivity)
     * */
    private fun showSplash() {
        binding.includedLayoutSplash.clSplashLayout.visibility = View.VISIBLE
        CoroutineScope(Job()).launch {
            delay(5000)
            launch(Dispatchers.Main)
            {
            }
            startActivity(Intent(this@WelcomeActivity, MainActivity::class.java))
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        val showSplash = getBoolean(SHOW_SPLASH_LAYOUT, false)
        val showPermissionLayout = getBoolean(SHOW_PERMISSION_LAYOUT, true)

        if (showPermissionLayout) {
            binding.includedLayoutPermission.clShowPerm.visibility = View.VISIBLE
            if (!viewModel.checkLocationPermission(this)) {
                viewModel.requestLocationPer(this)
            } else if (!viewModel.isLocationEnabled()) {
                viewModel.showLocationDialog(this)
                binding.includedLayoutPermission.mtButtonEnableLocation.visibility = View.VISIBLE

                binding.includedLayoutPermission.mtButtonEnableLocation.setOnClickListener {
                    /** Call isLocationEnabled from view model this will check is location is enbaled or
                     * not
                     * and it updates location status
                     * */
                    if (!viewModel.isLocationEnabled()) {
                        viewModel.showLocationDialog(this)
                    } else {
                        toastLong(resources.getString(R.string.location_is_already_enabled))
                    }
                }
            } else {
                binding.includedLayoutPermission.mtButtonEnableLocation.visibility = View.GONE
                viewModel.currentLocation(this, object : LocationItem {
                    override fun location(location: Location) {
                        viewModel.getCurrentCountryName(this@WelcomeActivity, location)
                    }

                    override fun message(message: String) {
                        toastLong(message)
                    }

                })
                viewModel.country.observe(this, {
                    if (it != null) {
                        putString(COUNTRY, it.countryName)
                        putBoolean(SHOW_PERMISSION_LAYOUT, false)
                        showSplash()
                    } else {
                        toastLong("Null")
                    }
                })
            }

        } else {
            val savedCountry = getString(COUNTRY, COUNTRY)

            if (savedCountry != NO_COUNTRY) {
                /**check on Splash that if location permission is granted and location is enabled
                 * then check the saved country name and new current location based country name if
                 * saved country doesn't match new country name then update value in preferences
                 * */
                if (viewModel.checkLocationPermission(this) && viewModel.isLocationEnabled()) {
                    /** get current location*/
                    viewModel.currentLocation(this, object : LocationItem {
                        override fun location(location: Location) {
                            /** get current country name and country code
                             * update Country in view model
                             * */
                            viewModel.getCurrentCountryName(this@WelcomeActivity, location)
                        }
                        override fun message(message: String) {
                            toastLong(message)
                        }

                    })
                    /** Observer for country detail
                     * */
                    viewModel.country.observe(this, {
                        if (it != null) {
                            if (savedCountry != it.countryName) {
                                putString(COUNTRY, it.countryName)
                            }
                            toastLong(it.countryName + " " + it.countryCode)
                            showSplash()
                        } else {
                            toastLong("country Null")
                        }
                    })
                } else {
                    /** if location permission is not granted and location is not enabled
                     * then simply show splash and intent to main activity
                     * keep work with saved country name
                     * */
                    toastLong(savedCountry)
                    showSplash()
                }
            }


        }
    }


}
