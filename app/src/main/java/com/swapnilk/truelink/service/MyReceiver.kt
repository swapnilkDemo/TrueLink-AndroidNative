package com.swapnilk.truelink.service

import android.app.Notification
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Parcelable
import android.widget.ImageView
import android.widget.TextView


open class MyReceiver : BroadcastReceiver() {
    private var title: TextView? = null
    private var text: TextView? = null
    private var subtext: TextView? = null
    private var largeIcon: ImageView? = null
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent != null) {
            val extras = intent.extras
            val notificationTitle = extras!!.getString(Notification.EXTRA_TITLE)
            val notificationLargeIcon =
                extras!!.getParcelable<Parcelable>(Notification.EXTRA_LARGE_ICON) as Bitmap?
            val notificationText = extras!!.getCharSequence(Notification.EXTRA_TEXT)
            val notificationSubText = extras!!.getCharSequence(Notification.EXTRA_SUB_TEXT)
           /* title.setText(notificationTitle)
            text.setText(notificationText)
            subtext.setText(notificationSubText)
            if (notificationLargeIcon != null) {
                largeIcon.setImageBitmap(notificationLargeIcon)
            }*/
        }

    }
}