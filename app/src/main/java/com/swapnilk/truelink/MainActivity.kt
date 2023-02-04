package com.swapnilk.truelink

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.exception.ApolloException
import com.auth0.android.jwt.JWT
import com.example.TokenUpdateMutation
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.swapnilk.truelink.databinding.ActivityMainBinding
import com.swapnilk.truelink.ui.user_profile.UpdateUserProfile
import com.swapnilk.truelink.utils.CommonFunctions
import com.swapnilk.truelink.utils.SharedPreferences
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

    override fun onResume() {
        super.onResume()
        // showToolBar()
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ////////////////////Initialize common classes////////////////
        sharedPreferences = SharedPreferences(this@MainActivity)
        commonFunctions = CommonFunctions(this@MainActivity)
        commonFunctions.setStatusBar(this@MainActivity)
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
        /////////////////Set Up Toolbar//////////////////
        setUpToolbar()

        val refreshToken = sharedPreferences.getRefreshToken();
        if (!refreshToken.isNullOrEmpty() && commonFunctions.checkConnection(this@MainActivity)) refreshAccessToken(
            refreshToken
        )
    }

    private fun showToolBar() {
        findViewById<AppBarLayout>(R.id.appBarLayout).visibility = View.VISIBLE
        navView.visibility = View.VISIBLE
    }

    private fun setUpToolbar() {
        toolbar = binding.toolbarMain.toolbar
        ivProfile = binding.toolbarMain.ivProfile
        ivSearch = binding.toolbarMain.ivSearch

        ivProfile.setOnClickListener {
            val userFragment = UpdateUserProfile()
            addFragmentToActivity(userFragment)
        }
    }

    private fun addFragmentToActivity(fragment: Fragment?) {

        if (fragment == null) return
        val fm = supportFragmentManager
        val tr = fm.beginTransaction()
        tr.add(R.id.nav_host_fragment, fragment)
        tr.commitAllowingStateLoss()
        var curFragment = fragment
    }

    ///////////////Set Badge to alert navigation/////////////////
    private fun setBadgeToAlert() {
        var badge = navView.getOrCreateBadge(R.id.nav_alert)
        badge.number = 11
        badge.maxCharacterCount = 2
    }

    /////////////////Refresh token if expired/////////////////
    private fun refreshAccessToken(refreshToken: String) {
        val jwt = JWT(sharedPreferences.getAccessToken().toString())
        if (jwt.isExpired(10)) {
            var tokenRefresh = TokenUpdateMutation(
                refreshToken
            )

            try {
                launch {
                    var response: ApolloResponse<TokenUpdateMutation.Data> =
                        apolloClient.mutation(tokenRefresh).execute()
                    afterResponse(response)
                }
            } catch (e: Exception) {
                e.stackTrace
                commonFunctions.showToast(this@MainActivity, e.message)

            } catch (e: ApolloException) {
                e.stackTrace
                commonFunctions.showToast(this@MainActivity, e.message)

            }
        }

    }

    ////////////////////handle refresh token response//////////////////
    private fun afterResponse(response: ApolloResponse<TokenUpdateMutation.Data>) {
        if (response?.data?.tokenUpdate?.success == true) {
            sharedPreferences.setAccessToken(response?.data?.tokenUpdate!!.payload!!.accessToken.toString())
            sharedPreferences.setRefreshToken(response?.data?.tokenUpdate!!.payload!!.refreshToken.toString())
            commonFunctions.showErrorSnackBar(
                this@MainActivity,
                navView,
                getString(R.string.token_refresh),
                false
            )
        }
    }

    ////////////////////////Handle Navigation and Backpress//////////////////
    override fun onBackPressed() {
        super.onBackPressed()
        if (supportFragmentManager.fragments.size == 1)
            navView.selectedItemId = R.id.nav_threat_control
    }
}