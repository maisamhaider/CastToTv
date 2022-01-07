package com.example.casttotv.adapter

import TabsFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.casttotv.ui.activities.browser.fragments.MoreFragment


class BrowserAdapter2(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {


    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> TabsFragment()
//            1 -> ShareFragment.newInstance()
//            2 -> SaveFragment.newInstance()
            else -> MoreFragment.newInstance()

        }
    }

    override fun getItemCount(): Int {
        return 4
    }
}