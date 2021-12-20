package com.example.casttotv.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.casttotv.ui.activities.browser.fragments.MoreFragment
import com.example.casttotv.ui.activities.browser.fragments.SaveFragment
import com.example.casttotv.ui.activities.browser.fragments.ShareFragment
import com.example.casttotv.ui.activities.browser.fragments.TabsFragment


class BrowserAdapter2(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {


    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> TabsFragment.newInstance()
            1 -> ShareFragment.newInstance()
            2 -> SaveFragment.newInstance()
            else -> MoreFragment.newInstance()

        }
    }

    override fun getItemCount(): Int {
        return 4
    }
}