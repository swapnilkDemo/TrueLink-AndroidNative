package com.swapnilk.truelink.utils

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.swapnilk.truelink.R
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class CommonFunctions(context: Context) {
    /////////////////////////Set Dark theme//////////////////
    @RequiresApi(Build.VERSION_CODES.P)
    fun setStatusBar(context: Activity) {
        try {
            if (Build.VERSION.SDK_INT >= 21) {
                val window = context.window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                window.statusBarColor = context.getColor(R.color.light_background)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
                    window.navigationBarDividerColor = context.getColor(R.color.light_background)
                window.navigationBarColor = context.getColor(R.color.light_background)
            }

        } catch (e: Exception) {

        }
    }

    ////////////////////////Add color text between string...////////////////////
    fun spanTextWithColor(
        text: String, color: Int, start: Int, end: Int
    ): SpannableStringBuilder {
        lateinit var spannable: SpannableStringBuilder
        try {
            spannable = SpannableStringBuilder(text)
            spannable.setSpan(
                ForegroundColorSpan(color), start, // start
                end, // end
                Spannable.SPAN_EXCLUSIVE_INCLUSIVE
            )

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return spannable
    }

    /////////////////////////Show Common Snackbar////////////////////////////
    fun showErrorSnackBar(context: Context, view: View, str: String) {
        try {
            val snackBarView = Snackbar.make(view, str, Snackbar.LENGTH_LONG)
                .setTextColor(Color.WHITE)
                .setActionTextColor(Color.WHITE)
                .setAction(context.getString(R.string.ok), View.OnClickListener {

                })
            val view = snackBarView.view

            /* val params = view.layoutParams as FrameLayout.LayoutParams
             params.gravity = Gravity.TOP
 //            params.topMargin = R.dimen.margin50
             view.layoutParams = params*/
            view.background =
                ContextCompat.getDrawable(context, R.drawable.rect_error) // for custom background
            snackBarView.animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE

            snackBarView.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    ////////////////////////Change String MM/dd/yyyy to timestamp/////////////////
    fun convertDate2TimeStamp(dateStr: String): Long? {
        var timestamp: Long? = null
        try {
            val formatter: DateFormat = SimpleDateFormat("mm/dd/yyyy")
            val date: Date = formatter.parse(dateStr) as Date
            timestamp = date.time

        } catch (e: Exception) {
            e.stackTrace
        }
        return timestamp
    }

    /////////////////Change Timestamp string to date///////////////////
    fun convertTimeStamp2Date(timestamp: String): String {
        var strDate = ""
        try {
            val original: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            val date = original.parse(timestamp)
            val formatter: DateFormat = SimpleDateFormat("MMM, dd  yyyy")
            strDate = formatter.format(date).toString();
        } catch (e: Exception) {
            e.stackTrace
        }
        return strDate
    }

    ////////////////////Check Internet Connection//////////////////////////
    fun checkConnection(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (cm != null) {
            val capabilities = cm.getNetworkCapabilities(cm.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
                    return true
                else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI))
                    return true
                else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET))
                    return true
            }
        }
        return false
    }
}