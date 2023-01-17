package com.sapnilk.truelink.utils

import android.content.Context
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan

class CommonFunctions(context: Context) {
    fun spanTextWithColor(
        text: String,
        color: Int,
        start: Int,
        end: Int
    ): SpannableStringBuilder {
        val spannable = SpannableStringBuilder(text)
        spannable.setSpan(
            ForegroundColorSpan(color),
            start, // start
            end, // end
            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
        )
        return spannable
    }
}