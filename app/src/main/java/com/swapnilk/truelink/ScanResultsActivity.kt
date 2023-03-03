package com.truelink

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import com.apollographql.apollo3.api.ApolloResponse
import com.example.ScanLinkMutation
import com.swapnilk.truelink.service.ForegroundService
import com.swapnilk.truelink.service.PopupService
import com.swapnilk.truelink.utils.CommonFunctions

class ScanResultsActivity : Activity() {
    lateinit var commonFunctions: CommonFunctions

    companion object {
        lateinit var response: ApolloResponse<ScanLinkMutation.Data>
    }

    // method for starting the service
    //    make activity invisible
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        commonFunctions = CommonFunctions(this@ScanResultsActivity)
        val intent = intent
        val action = intent.action
        val data: Uri? = intent.data
        Log.d("ScanResultsActivity", "onCreate: $action $data")
        val window = Window(this)
        val serviceIntent = Intent(applicationContext, PopupService::class.java)
        val bundle = Bundle()
//        show scans layout
//        setContentView(R.layout.new_scan_results)
        //        bundle.putString("action", "reaction");
//        if data contains any one of 'https://truelink.ai', 'https://truelink.app', 'https://truelink.nekione.com' replace with 'truelink://'
        if (data.toString().contains("https://truelink.ai") || data.toString()
                .contains("https://truelink.app") || data.toString()
                .contains("https://truelink.nekione.com")
        ) {
//            open link with com.truelink app
            val openIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse(
                    data.toString().replace("https://truelink.ai", "truelink://")
                        .replace("https://truelink.app", "truelink://")
                        .replace("https://truelink.nekione.com", "truelink://")
                )
            )
            startActivity(openIntent)
            finish()
            return
        }
        bundle.putString("action", "reaction")
        bundle.putString("data", data.toString())
        serviceIntent.putExtras(bundle)
        applicationContext.startService(serviceIntent)
        val scan = response.data?.scanLink?.payload?.scan
        window.open(
            scan?.link?.full_url,
            scan?.link?.whois?.domainName,
            scan?.triggers,
            scan?.flag ?: "SPAM",
            scan?.link?.totalReports,
            0,
            0,
            0,
            1,
            scan?.severity,
            scan?.link?.ipinfo?.ip,
            scan?.link?.category,
            commonFunctions.convertTimeStamp2Date(scan?.createdAt.toString()),
            "null",
            scan?.link?.ipinfo?.country,
            "Null"
        );
        finish()
    }

    private fun startService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // check if the user has already granted
            // the Draw over other apps permission
            if (Settings.canDrawOverlays(this)) {
                // start the service based on the android version
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(Intent(this, ForegroundService::class.java))
                } else {
                    startService(Intent(this, ForegroundService::class.java))
                }
            }
        } else {
            startService(Intent(this, ForegroundService::class.java))
        }
    }

    // method to ask user to grant the Overlay permission
    fun checkOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                // send user to the device settings
                val myIntent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                startActivity(myIntent)
            }
        }
    } // check for permission again when user grants it from
    // the device settings, and start the service


    override fun onResume() {
        super.onResume()
        startService();
    }
}