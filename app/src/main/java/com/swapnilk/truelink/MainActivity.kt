package com.swapnilk.truelink

import android.os.Bundle
import android.widget.ImageView
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.auth0.android.jwt.JWT
import com.example.TokenUpdateMutation
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.swapnilk.truelink.databinding.ActivityMainBinding
import com.swapnilk.truelink.utils.CommonFunctions
import com.swapnilk.truelink.utils.SharedPreferences
import com.tenclouds.fluidbottomnavigation.FluidBottomNavigationItem
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
    private lateinit var navView: BottomNavigationView
    private lateinit var toolbar: Toolbar
    private lateinit var ivProfile: ImageView
    private lateinit var ivSearch: ImageView

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var commonFunctions: CommonFunctions

    //  private lateinit var apolloHelper: ApolloHelper
    private lateinit var apolloClient: ApolloClient

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ////////////////////Initialize common classes////////////////
        sharedPreferences = SharedPreferences(this@MainActivity)
        commonFunctions = CommonFunctions(this@MainActivity)
        try {
            //apolloHelper = ApolloHelper(this@MainActivity)
            apolloClient =
                ApolloClient.Builder().serverUrl("https://truelink.neki.dev/graphql/").build()
        } catch (e: Exception) {
            e.stackTrace
        }
        //////////////////////////////////////////////////////////////
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //////////////////Fragment Navigation//////////////////////////////
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        /////////////Set Navigation Menus////////////////
        navView = binding.bottomNavigation
        navView.setupWithNavController(navController)
        navView.selectedItemId = R.id.nav_threat_control
        ///////////Set Badge to Alert/////////////////
        setBadgeToAlert()


        /*setNavigation()
        navView.selectTab(2)
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
        }*/
    }


    private fun setNavigation() {
        fluidBottomNavigationItems = ArrayList()
        (fluidBottomNavigationItems as ArrayList<FluidBottomNavigationItem>)
            .add(
                FluidBottomNavigationItem(
                    getString(R.string.title_dashboard),
                    ContextCompat.getDrawable(this, R.drawable.ic_nav_dashboard)
                )
            )
        (fluidBottomNavigationItems as ArrayList<FluidBottomNavigationItem>)
            .add(
                FluidBottomNavigationItem(
                    getString(R.string.title_bulk_scan),
                    ContextCompat.getDrawable(this, R.drawable.ic_nav_bulk)
                )
            )
        (fluidBottomNavigationItems as ArrayList<FluidBottomNavigationItem>)
            .add(
                FluidBottomNavigationItem(
                    getString(R.string.title_threat_control),
                    ContextCompat.getDrawable(this, R.drawable.ic_nav_threat)
                )
            )

        (fluidBottomNavigationItems as ArrayList<FluidBottomNavigationItem>)
            .add(
                FluidBottomNavigationItem(
                    getString(R.string.title_url_shortener),
                    ContextCompat.getDrawable(this, R.drawable.ic_nav_shortner)
                )
            )
        (fluidBottomNavigationItems as ArrayList<FluidBottomNavigationItem>)
            .add(
                FluidBottomNavigationItem(
                    getString(R.string.title_alert),
                    ContextCompat.getDrawable(
                        this, R.drawable.ic_nav_alert
                    )
                )
            )
        //  navView.items = fluidBottomNavigationItems

        val refreshToken = sharedPreferences.getRefreshToken();
        if (!refreshToken.isNullOrEmpty() && commonFunctions.checkConnection(this@MainActivity))
            refreshAccessToken(refreshToken)

    }

    private fun setBadgeToAlert() {
        var badge = navView.getOrCreateBadge(R.id.nav_alert)
        badge.number = 11
        badge.maxCharacterCount = 2
    }

    private fun refreshAccessToken(refreshToken: String) {
        val jwt = JWT(sharedPreferences.getAccessToken().toString())
        if (jwt.isExpired(10)) {
            var tokenRefresh = TokenUpdateMutation(
                refreshToken
            )

            launch {
                var response: ApolloResponse<TokenUpdateMutation.Data> =
                    apolloClient.mutation(tokenRefresh).execute()
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