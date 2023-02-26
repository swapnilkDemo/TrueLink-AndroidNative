package com.swapnilk.truelink.ui.dashboard

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.swapnilk.truelink.data.online.model.AppDataModel

class ViewPagerAdapter(
    requireContext: Context,
    supportFragmentManager: FragmentManager?,
    tabCount: Int,
    statistics: AppDataModel
) : FragmentPagerAdapter(supportFragmentManager!!) {
    private val count = tabCount
    private val appDataModel = statistics
    override fun getCount(): Int {
        return count
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> BarChartFragment(appDataModel.allScansMap)
            1 -> BarChartFragment(appDataModel.safeScansMap)
            2 -> BarChartFragment(appDataModel.verifiedScansMap)
            else -> BarChartFragment(appDataModel.suspiciousScansMap)
        }
    }

}
