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
import com.example.casttotv.databinding.ActivityWelcomeBinding
import com.example.casttotv.databinding.WalkThroughLayout1Binding
import com.example.casttotv.databinding.WalkThroughLayout2Binding
import com.example.casttotv.databinding.WalkThroughLayout3Binding
import com.example.casttotv.utils.Pref.getPrefs
import com.example.casttotv.utils.Pref.putPrefs
import com.example.casttotv.utils.WALK_THROUGH
import com.example.casttotv.viewmodel.TutorialViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class WelcomeActivity : AppCompatActivity() {

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
        binding.includedLayoutSplash.lottieAnimationView.playAnimation()

        CoroutineScope(Job()).launch {
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
        if (getPrefs(WALK_THROUGH, true)) {
            walkThrough()
        } else {
            showSplash()
        }
    }

    private fun walkThrough() {
        binding.includedWalkThrough.clWalkThroughMain.visibility = View.VISIBLE
        val viewList: MutableList<View> = ArrayList()
        val adapter = WalkThroughPagerAdapter()

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
            includedWalkThrough.tutorialVM = tutorialViewModel
            includedWalkThrough.thisActivity = this@WelcomeActivity
            viewPager = includedWalkThrough.viewpager2
            includedWalkThrough.viewpager2.adapter = adapter
            adapter.submitList(viewList)
            includedWalkThrough.dotsIndicator.setViewPager2(viewPager)
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
