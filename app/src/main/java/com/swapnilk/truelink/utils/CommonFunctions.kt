package com.swapnilk.truelink.utils

import android.content.Context
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.swapnilk.truelink.R
import java.security.Timestamp
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class CommonFunctions(context: Context) {
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
    fun convertDate2TimeStamp(date: String): Long {
        val formatter: DateFormat = SimpleDateFormat("MM/dd/yyyy")
        val date: Date = formatter.parse(date) as Date
        return date.time
    }


}