package com.swapnilk.truelink.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.swapnilk.truelink.IntroActivity
import com.swapnilk.truelink.R
import com.swapnilk.truelink.utils.CommonFunctions

class SplashScreen : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val commonFunctions = CommonFunctions(this)
        commonFunctions.setStatusBar(this@SplashScreen)
        setContentView(R.layout.activity_splash_screen)
        ////Manage SharePreferences, Sessions in this activity//////////////////

        var iv_logo: ImageView = findViewById(R.id.iv_logo)
        val animationBounce = AnimationUtils.loadAnimation(this, R.anim.rotate)
        iv_logo.startAnimation(animationBounce)
        ////////////Load Next Activity///////////////
        Handler().postDelayed(object : Runnable {
            override fun run() {
                //start main activity is used is previously logged in////////////////////
                startActivity(Intent(this@SplashScreen, IntroActivity::class.java))
                finish()
            }
        }, 2000)//////////Delay in milisecond//////////
    }

}