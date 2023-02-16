package com.swapnilk.truelink.ui.dashboard

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class ViewPagerAdapter(
    requireContext: Context,
    supportFragmentManager: FragmentManager?,
    tabCount: Int
) : FragmentPagerAdapter(supportFragmentManager!!) {
    private val count = tabCount
    override fun getCount(): Int {
        return count
    }

    override fun getItem(position: Int): Fragment {
        return BarChartFragment()
    }

}
