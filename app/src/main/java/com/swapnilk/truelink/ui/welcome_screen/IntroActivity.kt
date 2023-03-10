package com.swapnilk.truelink

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.widget.ViewPager2
import com.swapnilk.truelink.ui.SigninActivity
import com.swapnilk.truelink.ui.welcome_screen.IntroPagerAdapter
import com.swapnilk.truelink.utils.CommonFunctions
import com.swapnilk.truelink.utils.SharedPreferences
import me.relex.circleindicator.CircleIndicator3
import java.util.*

class IntroActivity : FragmentActivity() {
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var commonFunctions: CommonFunctions
    private lateinit var viewPager2: ViewPager2
    private lateinit var indicator: CircleIndicator3

    private lateinit var introPagerAdapter: IntroPagerAdapter
    private lateinit var btnlogin: Button
    var currentPage = 0
    var timer: Timer? = null
    val DELAY_MS: Long = 500 //delay in milliseconds before task is to be executed

    val PERIOD_MS: Long = 3000 // time in milliseconds between successive task executions.


    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        commonFunctions = CommonFunctions(applicationContext)
        commonFunctions.setStatusBar(this@IntroActivity)
        sharedPrefs = SharedPreferences(applicationContext)
        if (sharedPrefs.isFirstLaunch()) {
            setContentView(R.layout.activity_intro)
            viewPager2 = findViewById(R.id.viewpager_intro)
            indicator = findViewById(R.id.indicator)
            indicator.setViewPager(viewPager2)
            indicator.createIndicators(3, 0)
            btnlogin = findViewById(R.id.button_login_main)
            btnlogin.setOnClickListener {
                sharedPrefs.setFirstLaunch(false)
                startSignin()
            }
            setViewPager()
            /*After setting the adapter use the timer */

            /*After setting the adapter use the timer */
            val handler = Handler()
            val Update = Runnable {
                if (currentPage === 4 - 1) {
                    currentPage = 0
                }
                viewPager2.setCurrentItem(currentPage++, true)
            }

            timer = Timer() // This will create a new Thread

            timer!!.schedule(object : TimerTask() {
                // task to be scheduled
                override fun run() {
                    handler.post(Update)
                }
            }, DELAY_MS, PERIOD_MS)
        } else {
            startSignin()
        }
    }

    private fun startSignin() {
        val intent = Intent(this, SigninActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun setViewPager() {
        var curr: Int = 0
        var tabcount: Int = 3

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
        } catch (e: Exception) {

        }

    }//End function

}// End Class