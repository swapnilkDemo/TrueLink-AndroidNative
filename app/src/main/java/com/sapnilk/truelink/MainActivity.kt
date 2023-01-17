package com.sapnilk.truelink

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import br.com.simplepass.loadingbutton.animatedDrawables.ProgressType
import br.com.simplepass.loadingbutton.customViews.ProgressButton
import com.sapnilk.truelink.databinding.ActivityMainBinding
import com.tenclouds.fluidbottomnavigation.FluidBottomNavigation
import com.tenclouds.fluidbottomnavigation.FluidBottomNavigationItem
import com.tenclouds.fluidbottomnavigation.listener.OnTabSelectedListener

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var fluidBottomNavigationItems: List<FluidBottomNavigationItem>
    private lateinit var navView: FluidBottomNavigation


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navView = binding.fluidBottomNavigation

      /*  val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
         val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)*/
        // navView.setupWithNavController(navController)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        setNavigation()

        //appPref.putString(appPref.INCIDENT_ID, "");
        ////////////////////////////////////////////
        // setupTabIcons(0);
        // bottomNavigationView.getMenu().findItem(R.id.navigation_Notification).setChecked(true);
        navView.selectTab(1)

        navView.onTabSelectedListener

        ///////////////////////////////////////////////////////////////////

        //////////////////////TAb Bar Style///////////////
        //  fluidBottomNavigation.setBackColor(ContextCompat.getColor(this, R.color.backColor));
        //fluidBottomNavigation.ba

        // createTabs();
        navView.onTabSelectedListener = object : OnTabSelectedListener {
            override fun onTabSelected(i: Int) {
                try {
                    if (i == 0) {
                        navController.navigate(R.id.navigation_dashboard)
                    } else if (i == 1) {
                        navController.navigate(R.id.navigation_home)
                    } else {
                        navController.navigate(R.id.navigation_notifications)
                    }

                } catch (e: Exception) {
                    e.stackTrace
                }
            }
        }
    }

    private fun setNavigation() {
        fluidBottomNavigationItems = ArrayList()
        (fluidBottomNavigationItems as ArrayList<FluidBottomNavigationItem>)
            .add(
                FluidBottomNavigationItem(
                    getString(R.string.title_dashboard),
                    ContextCompat.getDrawable(this, R.drawable.ic_dashboard_black_24dp)
                )
            )
        (fluidBottomNavigationItems as ArrayList<FluidBottomNavigationItem>)
            .add(
                FluidBottomNavigationItem(
                    getString(R.string.title_home),
                    ContextCompat.getDrawable(this, R.drawable.ic_home_black_24dp)
                )
            )
        (fluidBottomNavigationItems as ArrayList<FluidBottomNavigationItem>)
            .add(
                FluidBottomNavigationItem(
                    getString(R.string.title_notifications),
                    ContextCompat.getDrawable(this, R.drawable.ic_notifications_black_24dp)
                )
            )
        navView.items = fluidBottomNavigationItems
    }
}