package com.swapnilk.truelink

import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
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
import com.apollographql.apollo3.api.Optional
import com.apollographql.apollo3.exception.ApolloException
import com.auth0.android.jwt.JWT
import com.example.ScanLinkMutation
import com.example.TokenUpdateMutation
import com.example.type.AppType
import com.example.type.ScanLinkInput
import com.example.type.ScanTriggerType
import com.example.type.WhoisInput
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.swapnilk.truelink.databinding.ActivityMainBinding
import com.swapnilk.truelink.service.ForegroundService
import com.swapnilk.truelink.service.MyReceiver
import com.swapnilk.truelink.ui.SigninActivity
import com.swapnilk.truelink.ui.user_profile.UpdateUserProfile
import com.swapnilk.truelink.utils.CommonFunctions
import com.swapnilk.truelink.utils.SharedPreferences
import kotlinx.coroutines.*
import java.io.DataInputStream
import java.io.IOException
import java.io.OutputStreamWriter
import java.io.Writer
import java.net.Socket
import kotlin.coroutines.CoroutineContext


open class MainActivity : AppCompatActivity(), CoroutineScope {
    companion object GlobalFields {
        val INTENT_ACTION_NOTIFICATION = "it.gmariotti.notification"

        @RequiresApi(Build.VERSION_CODES.O)
        fun checkOverlayPermission(context: MainActivity) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(context)) {
                    // send user to the device settings
                    val myIntent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                    context.startActivity(myIntent)
                } else {
                    context.startService()
                }
            } else {
                if (!Settings.canDrawOverlays(context)) {
                    val intent = Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + context.packageName)
                    )
                    context.startActivityForResult(intent, 5469)
                }
            }
        } // check for permission again when user grants it from

        @RequiresApi(Build.VERSION_CODES.Q)
        fun showLauncherSelection(context: MainActivity) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val intent = Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS)
                context.startActivity(intent)
            } else {
                context.startActivityForResult(
                    Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS),
                    0
                )
            }
            // resultLauncher.launch(intent)

        }


        /*  @RequiresApi(Build.VERSION_CODES.O)
          private var resultLauncher =
              registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                  if (result.resultCode == Activity.RESULT_OK) {
                      // There are no request codes
                      val data: Intent? = result.data
                      //doSomeOperations()
                  }
              }*/

        fun requestPermissions(permission: String) {

        }

        fun allowNotificationAccess(mainActivity: MainActivity) {
            val intent = Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
            mainActivity.startActivity(intent)
        }
    }

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
    private var mReceiver: MyReceiver = MyReceiver()
    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    override fun onResume() {
        super.onResume()
        // showToolBar()
        //  job.start()
        if (mReceiver == null) mReceiver = MyReceiver()
        registerReceiver(mReceiver, IntentFilter(INTENT_ACTION_NOTIFICATION))
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ////////////////////Initialize common classes//////////////////
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
        navView.selectedItemId = R.id.nav_dashboard
        ///////////Set Badge to Alert/////////////////
        setBadgeToAlert()
        /////////////////Set Up Toolbar//////////////////
        setUpToolbar()

        val refreshToken = sharedPreferences.getRefreshToken();
        if (!refreshToken.isNullOrEmpty() && commonFunctions.checkConnection(this@MainActivity))
            refreshAccessToken(
                refreshToken
            )

        //////////////////Handle Intent on click on Link//////////////////
        val intent = intent
        val action = intent.action
        val data = intent.data
        if (data != null) {
            /*  var intent =Intent(this@MainActivity, ScanResultsActivity::class.java)
              intent.putExtra("dara", data)
              startActivity(intent)*/
            scanLink(data)
        }
    }

    ///////////////////Scan Link/////////////////////////////////////////
    private fun scanLink(data: Uri) {
        val whoStr = consultWhois(data.host.toString())
        val whoisInput = WhoisInput(
            data.host.toString(),
            data.host.toString(),
            Optional.Absent,
            Optional.Absent,
            Optional.Absent,
            Optional.Absent

        )
        val scanLinkInput = ScanLinkInput(
            data.toString(),
            ScanTriggerType.MANUAL,
            Optional.Absent,
            Optional.present(AppType.SOCIAL_MEDIA),
            Optional.Absent,
            Optional.Absent,
            Optional.Absent,
            Optional.Absent,
            Optional.Absent
        )
        val scanLink = ScanLinkMutation(
            scanLinkInput
        )

        try {
            if (job != null)
                launch {

                    var response: ApolloResponse<ScanLinkMutation.Data> =
                        apolloClient.mutation(scanLink).execute()
                    handleScanResult(response)
                }
            else
                commonFunctions.showToast(this, "Scope ended")
        } catch (e: ApolloException) {
            e.stackTrace
        }
    }

    //////////////////////////////Handle response of scan link////////////////////
    private fun handleScanResult(response: ApolloResponse<ScanLinkMutation.Data>) {
        commonFunctions.showToast(this@MainActivity, "Links Scanned successfully")
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
            finish()
            startActivity(Intent(this, MainActivity::class.java))
        } else if (response?.data?.tokenUpdate?.code == 400) {
            sharedPreferences.setLoggedIn(false)
            startActivity(Intent(this@MainActivity, SigninActivity::class.java))
        } else {
            commonFunctions.showErrorSnackBar(
                this@MainActivity,
                navView,
                response?.data?.tokenUpdate?.message!!,
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

    ////////////////Start Foreground Service//////////////////
    @RequiresApi(Build.VERSION_CODES.O)
    private fun startService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(this)) {
                startForegroundService(Intent(this, ForegroundService::class.java))
            } else {
                startService(Intent(this, ForegroundService::class.java))
            }
        }
    }

    //////////////////////////Check whois info from domain////////////////////////////
    open fun consultWhois(domain: String): String? {
        // val domquest = "$domain.$tld"
        var resultado = ""
        var theSocket: Socket
        val hostname = "whois.internic.net"
        val port = 43
        try {
            async {
                theSocket = Socket(hostname, port, true)
                val out: Writer = OutputStreamWriter(theSocket.getOutputStream())
                out.write("=$domain\r\n")
                out.flush()
                val theWhoisStream: DataInputStream = DataInputStream(theSocket.getInputStream())
                var s: String
                while (theWhoisStream.readLine().also { s = it } != null) {
                    resultado = """
                $resultado$s
                
                """.trimIndent()
                }
            }
        } catch (e: IOException) {
        }
        return resultado
    }

}