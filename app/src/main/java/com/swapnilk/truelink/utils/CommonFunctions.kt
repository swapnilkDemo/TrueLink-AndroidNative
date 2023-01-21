package com.swapnilk.truelink.utils

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Handler
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import br.com.simplepass.loadingbutton.animatedDrawables.ProgressType
import br.com.simplepass.loadingbutton.customViews.ProgressButton
import com.google.android.datatransport.runtime.backends.BackendResponse.ok
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.swapnilk.truelink.R


class CommonFunctions(context: Context) {
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

    fun showErrorSnackBar(context: Context, view: View, str: String) {
        try {
            val snackBarView = Snackbar.make(view, str, Snackbar.LENGTH_LONG)
                .setTextColor(Color.WHITE)
                .setActionTextColor(Color.WHITE)
                .setAction(context.getString(R.string.ok), View.OnClickListener {

                })
            val view = snackBarView.view

            val params = view.layoutParams as FrameLayout.LayoutParams
            params.gravity = Gravity.TOP
//            params.topMargin = R.dimen.margin50
            view.layoutParams = params
            view.background =
                ContextCompat.getDrawable(context, R.drawable.rect_error) // for custom background
            snackBarView.animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE

            snackBarView.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}