package com.swapnilk.truelink

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import com.apollographql.apollo3.api.ApolloResponse
import com.auth0.android.jwt.JWT
import com.example.TokenUpdateMutation
import com.swapnilk.truelink.data.online.ApolloHelper
import com.swapnilk.truelink.data.online.model.UserModel
import com.swapnilk.truelink.databinding.ActivityMainBinding
import com.swapnilk.truelink.utils.CommonFunctions
import com.swapnilk.truelink.utils.SharedPreferences
import com.tenclouds.fluidbottomnavigation.FluidBottomNavigation
import com.tenclouds.fluidbottomnavigation.FluidBottomNavigationItem
import com.tenclouds.fluidbottomnavigation.listener.OnTabSelectedListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), CoroutineScope {
    ////////////Start Coroutine for Background Task../////////////
    private var job: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private lateinit var binding: ActivityMainBinding
    private lateinit var fluidBottomNavigationItems: List<FluidBottomNavigationItem>
    private lateinit var navView: FluidBottomNavigation

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var commonFunctions: CommonFunctions
    private lateinit var apolloHelper: ApolloHelper

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ////////////////////Initialize common classes////////////////
        sharedPreferences = SharedPreferences(this@MainActivity)
        commonFunctions = CommonFunctions(this@MainActivity)
        apolloHelper = ApolloHelper(this@MainActivity)
        //////////////////////////////////////////////////////////////
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //////////////////Fragment Navigation//////////////////////////////
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        /////////////Set Navigation Menus////////////////
        navView = binding.fluidBottomNavigation
        setNavigation()
        navView.selectTab(1)
        navView.onTabSelectedListener
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

        val refreshToken = sharedPreferences.getRefreshToken();
        if (!refreshToken.isNullOrEmpty())
            refreshAccessToken(refreshToken)
    }


    private fun refreshAccessToken(refreshToken: String) {
        val jwt = JWT(sharedPreferences.getAccessToken().toString())
        if (jwt.isExpired(10)) {
            var tokenRefresh = TokenUpdateMutation(
                refreshToken
            )

            launch {
                var response: ApolloResponse<TokenUpdateMutation.Data> =
                    apolloHelper.apolloClient.mutation(tokenRefresh).execute()
                afterResponse(response)
            }
        }

    }

    private fun afterResponse(response: ApolloResponse<TokenUpdateMutation.Data>) {
        if (response?.data?.tokenUpdate?.success == true) {
            sharedPreferences.setAccessToken(response?.data?.tokenUpdate!!.payload!!.accessToken.toString())
            sharedPreferences.setRefreshToken(response?.data?.tokenUpdate!!.payload!!.refreshToken.toString())
            commonFunctions.showErrorSnackBar(
                this@MainActivity,
                navView,
                getString(R.string.token_refresh)
            )
        }
    }
}