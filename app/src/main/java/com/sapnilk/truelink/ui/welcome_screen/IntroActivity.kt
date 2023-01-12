package com.swapnilk.truelink

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.sapnilk.truelink.MainActivity
import com.sapnilk.truelink.R
import com.sapnilk.truelink.ui.welcome_screen.IntroPagerAdapter
import com.sapnilk.truelink.utils.SharedPreferences

import me.relex.circleindicator.CircleIndicator3

class IntroActivity : FragmentActivity() {
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var viewPager2: ViewPager2
    private lateinit var indicator: CircleIndicator3

    private lateinit var introPagerAdapter: IntroPagerAdapter
    private lateinit var btnlogin:Button


    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        Thread.sleep(300)
        setTheme(R.style.Theme_TrueLink)
        super.onCreate(savedInstanceState)
        sharedPrefs = SharedPreferences(applicationContext)
        if (sharedPrefs.isFirstLaunch()){
            setContentView(R.layout.activity_intro)
            viewPager2 = findViewById(R.id.viewpager_intro)
            indicator = findViewById(R.id.indicator)
            indicator.setViewPager(viewPager2)
            indicator.createIndicators(3,0);
            btnlogin = findViewById(R.id.button_login_main)
            btnlogin.setOnClickListener {
                startSignin()
            }
            setViewPager()
        }else{
            startSignin()
        }
    }

    private fun startSignin() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun setViewPager(){
        var curr:Int=0
        var tabcount:Int = 3

        try {
            introPagerAdapter = IntroPagerAdapter(this, tabcount)
            introPagerAdapter.registerAdapterDataObserver(indicator.adapterDataObserver)
            viewPager2.apply {
                adapter = introPagerAdapter
                currentItem = 0
                registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        indicator.animatePageSelected(position)
                        super.onPageSelected(position)
                    }

                })
            }
        }catch (e:Exception){

        }

    }

}