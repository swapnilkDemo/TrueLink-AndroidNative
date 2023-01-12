package com.sapnilk.truelink.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.sapnilk.truelink.MainActivity
import com.sapnilk.truelink.R
import com.swapnilk.truelink.IntroActivity

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        ////Manage SharePreferences, Sessions in this activity//////////////////

        Handler().postDelayed(object : Runnable {
            override fun run() {
                //start main activity is used is previously logged in////////////////////
                startActivity(Intent(this@SplashScreen, IntroActivity::class.java))
                finish()
            }

        }, 2000)
    }
}