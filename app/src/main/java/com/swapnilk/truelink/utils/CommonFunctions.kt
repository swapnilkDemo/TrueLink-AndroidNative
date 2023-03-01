package com.swapnilk.truelink.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.palette.graphics.Palette
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.ozcanalasalvar.library.utils.DateUtils
import com.ozcanalasalvar.library.view.popup.DatePickerPopup
import com.swapnilk.truelink.R
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class CommonFunctions(context: Context) {

    ////////////////////////Get Server URL////////////////
    fun getServerUrl(): String {
        /////////////////////////Change Server URl Here////////////
        return "https://truelink.neki.dev/graphql/"
    }

    /////////////////////////Set Dark theme//////////////////
    @RequiresApi(Build.VERSION_CODES.P)
    fun setStatusBar(context: Activity) {
        try {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            Thread.sleep(300)

            //  context.setTheme(R.style.Theme_TrueLink)
            if (Build.VERSION.SDK_INT >= 21) {
                val window = context.window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                window.statusBarColor = context.getColor(R.color.light_background)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) window.navigationBarDividerColor =
                    context.getColor(R.color.light_background)
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
    fun showErrorSnackBar(context: Context, view: View, str: String, isError: Boolean) {
        try {
            val snackBarView =
                Snackbar.make(view, str, Snackbar.LENGTH_LONG).setTextColor(Color.WHITE)
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
            if (!isError) view.background.setTint(context.getColor(R.color.teal_200))
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
            val formatter: DateFormat = SimpleDateFormat("MMM, dd  yyyy")
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
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) return true
                else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) return true
                else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) return true
            }
        }
        return false
    }

    //////////////////Build Date Picker Dialog///////////////////////////////////////////////
    fun createDatePickerDialog(context: Context): DatePickerPopup {
        return DatePickerPopup.Builder().from(context).offset(3)
            //.darkModeEnabled(true)
            .pickerMode(com.ozcanalasalvar.library.view.datePicker.DatePicker.MONTH_ON_FIRST)
            .textSize(19).endDate(DateUtils.getCurrentTime())
            .currentDate(DateUtils.getTimeMiles(1997, 7, 7))
            .startDate(DateUtils.getTimeMiles(1900, 1, 1)).build()
    }

    /////////////////////Show Toast //////////////////////////
    fun showToast(context: Context, message: String?) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()

    }


    /////////////////Check if truelink is default browser///////////////
    fun isDefaultBrowser(context: Context): Boolean {
        val browserIntent = Intent("android.intent.action.VIEW", Uri.parse("http://"))
        val resolveInfo: ResolveInfo? =
            context.packageManager
                .resolveActivity(browserIntent, PackageManager.MATCH_DEFAULT_ONLY)
        // This is the default browser's packageName
        val packageName = resolveInfo?.activityInfo?.packageName
        return !TextUtils.isEmpty(packageName) && packageName == "com.swapnilk.truelink"
    }

    @Throws(IOException::class)
    fun drawableFromUrl(url: String?): Drawable? {
        val x: Bitmap
        val connection: HttpURLConnection = URL(url).openConnection() as HttpURLConnection
        connection.connect()
        val input: InputStream = connection.inputStream
        x = BitmapFactory.decodeStream(input)
        return BitmapDrawable(Resources.getSystem(), x)
    }

    /////////////////Get App Name From Package Name/////////////////
    fun getAppNameFromPackageName(packageName: String?, context: Context): CharSequence {
        var applicationInfo: ApplicationInfo? = null
        try {
            applicationInfo = context.packageManager.getApplicationInfo(packageName!!, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            Log.d("TAG", "The package with the given name cannot be found on the system.")
        }
        return (if (applicationInfo != null) context.packageManager.getApplicationLabel(
            applicationInfo
        ) else "Unknown")
    }

    ///////////////////Get App Icon From Package Name////////////////////////
    fun getAppIconFromPackageName(packageName: String, context: Context): Drawable? {
        var icon: Drawable? = null
        try {
            icon =
                context.packageManager.getApplicationIcon(packageName)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return icon;
    }

    //////////////////////Get Dominant color from image///////////////
    open fun getDominantColor(drawable: Drawable?, context: Context): Int {
        val bitmap = getBitmapFromVector(drawable)
        /* val newBitmap = Bitmap.createScaledBitmap(bitmap!!, 1, 1, true)
         val color = newBitmap.getPixel(0, 0)
         newBitmap.recycle()
         var strColor = color.toString().replaceFirstChar { "#" }*/
        var dominantColor = context.getColor(R.color.selected_color)
        Palette.Builder(bitmap!!).generate {

            dominantColor =
                it?.getDominantColor(ContextCompat.getColor(context!!, R.color.btnColor))!!

        }
        return dominantColor
    }

    //////////////////Get Bitmap Form Vector Drawable////////////////
    private fun getBitmapFromVector(drawable: Drawable?): Bitmap? {
        return try {
            val bitmap: Bitmap = Bitmap.createBitmap(
                drawable!!.intrinsicWidth,
                drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight())
            drawable.draw(canvas)
            bitmap
        } catch (ex: Exception) {
            ex.stackTrace
            null
        }
    }
}