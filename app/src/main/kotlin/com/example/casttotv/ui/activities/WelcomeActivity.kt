package com.example.casttotv.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.viewpager2.widget.ViewPager2
import com.example.casttotv.adapter.WalkThroughPagerAdapter
import com.example.casttotv.databinding.*
import com.example.casttotv.utils.*
import com.example.casttotv.utils.Pref.getPrefs
import com.example.casttotv.utils.Pref.putPrefs
import com.example.casttotv.viewmodel.TutorialViewModel
import kotlinx.coroutines.*


class WelcomeActivity : AppCompatActivity() {

    //    private val viewModel: WelcomeViewModel by viewModels {
//        WelcomeViewModel.WelcomeFactory(this)
//    }
    private lateinit var binding: ActivityWelcomeBinding
    private val tutorialViewModel: TutorialViewModel by viewModels {
        TutorialViewModel.TutorialViewModelFactory(this)
    }
    private var currentPosition = 0
    private lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            DataBindingUtil.setContentView(this, com.example.casttotv.R.layout.activity_welcome)
    }

    /**
     * This fun function Visible splash layout,
     * wait for for 5 seconds
     * and then go to the MainActivity
     * finally finish (user will be exit on back press from mainActivity)
     * */
    private fun showSplash() {
        binding.includedWalkThrough.clWalkThroughMain.visibility = View.GONE
        binding.includedLayoutSplash.clSplashLayout.visibility = View.VISIBLE
        val slideUp: Animation =
            AnimationUtils.loadAnimation(applicationContext, com.example.casttotv.R.anim.slide_up)
        val slideUp2: Animation =
            AnimationUtils.loadAnimation(applicationContext,
                com.example.casttotv.R.anim.slide_up_2)

        binding.includedLayoutSplash.textViewAppName.animation = slideUp
        binding.includedLayoutSplash.textviewSlogan.animation = slideUp2

        val scope = CoroutineScope(Job()).launch {
            delay(6000)
            startActivity(Intent(this@WelcomeActivity, MainActivity::class.java))
            finish()
        }

    }

    override fun onResume() {
        super.onResume()
        window.statusBarColor =
            ContextCompat.getColor(this, com.example.casttotv.R.color.cr_black_pearl_2)
        window.navigationBarColor =
            ContextCompat.getColor(this, com.example.casttotv.R.color.cr_black_pearl_2)

//        val showSplash = getPrefs(SHOW_SPLASH_LAYOUT, false)
//        val showPermissionLayout = getPrefs(SHOW_PERMISSION_LAYOUT, true)
        val walkThrough = getPrefs(WALK_THROUGH, true)
        if (walkThrough) {
            walkThrough()
        }
        /*  else if (showPermissionLayout) {
              binding.includedLayoutPermission.clShowPerm.visibility = View.VISIBLE
              if (!viewModel.checkLocationPermission(this)) {
                  viewModel.requestLocationPer(this)
              } else if (!viewModel.isLocationEnabled()) {
                  viewModel.showLocationDialog(this)
                  binding.includedLayoutPermission.mtButtonEnableLocation.visibility =
                      View.VISIBLE

                  binding.includedLayoutPermission.mtButtonEnableLocation.setOnClickListener {
                      */
        /** Call isLocationEnabled from view model this will check is location is enbaled or
         * not
         * and it updates location status
         * *//*
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
                        this@WelcomeActivity.putPrefs(COUNTRY, COUNTRY)
                        this@WelcomeActivity.putPrefs(SHOW_PERMISSION_LAYOUT, false)
                        showSplash()
                    }

                })
                viewModel.country.observe(this, {
                    if (it != null) {
                        this.putPrefs(COUNTRY, it.countryName)
                        this.putPrefs(SHOW_PERMISSION_LAYOUT, false)
                        showSplash()
                    } else {
                        toastLong("Null")

                    }
                })
            }

        }*/ else {
            /*   val savedCountry = getPrefs(COUNTRY, COUNTRY)

               if (savedCountry != NO_COUNTRY) {
                   */
            /**check on Splash that if location permission is granted and location is enabled
             * then check the saved country name and new current location based country name if
             * saved country doesn't match new country name then update value in preferences
             * *//*
                if (viewModel.checkLocationPermission(this) && viewModel.isLocationEnabled()) {
                    */
            /** get current location*//*
                    viewModel.currentLocation(this, object : LocationItem {
                        override fun location(location: Location) {
                            */
            /** get current country name and country code
             * update Country in view model
             * *//*
                            viewModel.getCurrentCountryName(this@WelcomeActivity, location)
                        }

                        override fun message(message: String) {
                            toastLong(message)
                            showSplash()
                        }

                    })
                    */
            /** Observer for country detail
             * *//*
                    viewModel.country.observe(this, {
                        if (it != null) {
                            if (savedCountry != it.countryName) {
                                this.putPrefs(COUNTRY, it.countryName)
                            }
                            toastLong(it.countryName + " " + it.countryCode)
                        } else {
                            toastLong("country Null")
                        }
                        showSplash()

                    })
                } else {
                    */
            /** if location permission is not granted and location is not enabled
             * then simply show splash and intent to main activity
             * keep work with saved country name
             * *//*
                    toastLong(savedCountry)
                    showSplash()
                }
            }
*/
            showSplash()

        }
    }

    private fun walkThrough() {
        binding.includedWalkThrough.clWalkThroughMain.visibility = View.VISIBLE
        val viewList: MutableList<View> = ArrayList()

        val adapter = WalkThroughPagerAdapter(this)

        val view1: View =
            WalkThroughLayout1Binding.inflate(LayoutInflater.from(this)).root
        val view2: View =
            WalkThroughLayout2Binding.inflate(LayoutInflater.from(this)).root
        val view3: View =
            WalkThroughLayout3Binding.inflate(LayoutInflater.from(this)).root

        viewList.add(view1)
        viewList.add(view2)
        viewList.add(view3)

        binding.apply {
            lifecycleOwner = this@WelcomeActivity
            binding.includedWalkThrough.tutorialVM = tutorialViewModel
            binding.includedWalkThrough.thisActivity = this@WelcomeActivity
            viewPager = binding.includedWalkThrough.viewpager2
            binding.includedWalkThrough.viewpager2.adapter = adapter
            adapter.submitList(viewList)
            binding.includedWalkThrough.dotsIndicator.setViewPager2(viewPager)
        }
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int,
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                currentPosition = position
                when (position) {
                    0 -> {
                        tutorialViewModel.nextButtonProperties(getString(com.example.casttotv.R.string.next))
                        binding.includedWalkThrough.imageviewArrow.visibility = View.VISIBLE
                    }
                    1 -> {
                        tutorialViewModel.nextButtonProperties(getString(com.example.casttotv.R.string.next))
                        binding.includedWalkThrough.imageviewArrow.visibility = View.VISIBLE

                    }
                    2 -> {
                        tutorialViewModel.nextButtonProperties(getString(com.example.casttotv.R.string.finish))
                        binding.includedWalkThrough.imageviewArrow.visibility = View.GONE

                    }

                }
            }
        })
    }

    fun nextOrFinished() {
        if (currentPosition == 2) {
            putPrefs(WALK_THROUGH, false)
            showSplash()
        } else {
            tutorialViewModel.nextOrFinished(currentPosition, viewPager)
        }
    }


}
