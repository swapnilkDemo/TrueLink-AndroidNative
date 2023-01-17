package com.swapnilk.truelink.ui.welcome_screen

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.swapnilk.truelink.IntroFragment
import com.swapnilk.truelink.IntroFragment2
import com.swapnilk.truelink.IntroFragment3

class IntroPagerAdapter(fragmentActivity: FragmentActivity,c:Int):
    FragmentStateAdapter(fragmentActivity) {
    private val count = c
    override fun getItemCount(): Int {
        return count
    }

    override fun createFragment(position: Int): Fragment {
        when(position){
            0 -> return IntroFragment.newInstance("","")
            1 -> return IntroFragment2.newInstance("","")
            2 -> return IntroFragment3.newInstance("","")
            else -> return IntroFragment.newInstance("","")
        }

    }

}