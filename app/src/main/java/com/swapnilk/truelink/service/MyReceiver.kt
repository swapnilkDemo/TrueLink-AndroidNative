package com.swapnilk.truelink.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.NotificationCompat
import com.swapnilk.truelink.R
import okhttp3.internal.notify


open class MyReceiver : BroadcastReceiver() {
    private var title: TextView? = null
    private var text: TextView? = null
    private var subtext: TextView? = null
    private var largeIcon: ImageView? = null

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent != null) {
            val extras = intent.extras
            val notificationTitle = extras?.getString("title")
            val notificationLargeIcon =
                extras?.get("icon") as Bitmap?
            val notificationSmallIcon =
                extras?.get("smallIcon") as Int?
            val notificationText = extras?.getString("text")
            val notificationSubText = extras?.getString("subtext")
            title?.text = notificationTitle.toString()
            text?.text = notificationText.toString()
            subtext?.text = notificationSubText.toString()
            if (notificationLargeIcon != null) {
                largeIcon?.setImageBitmap(notificationLargeIcon)
            }

            Log.i("Package", extras?.getString("package").toString())
            Log.i("Ticker", extras?.getString("ticker").toString())
            if (notificationTitle != null) {
                Log.i("Title", notificationTitle)
            }
            if (notificationText != null) {
                Log.i("Text", notificationText)
            }
            val notificationManager =
                context?.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            val mBuilder: NotificationCompat.Builder? =
                context?.let {
                    context?.getString(R.string.default_notification_channel_id)
                        ?.let { it1 -> NotificationCompat.Builder(it, it1) }
                }

            mBuilder?.setContentTitle(notificationTitle.toString())
                ?.setContentText(notificationText.toString())
                ?.setSubText(notificationSubText.toString())
                ?.setLargeIcon(notificationLargeIcon)
                ?.setSmallIcon(R.mipmap.ic_launcher)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val importance = NotificationManager.IMPORTANCE_HIGH
                val notificationChannel = NotificationChannel(
                    context?.getString(R.string.default_notification_channel_id),
                    "NOTIFICATION_CHANNEL_NAME",
                    importance
                )
                context?.getString(R.string.default_notification_channel_id)
                    ?.let { mBuilder!!.setChannelId(it) }
                notificationManager.createNotificationChannel(notificationChannel)
            }
            notificationManager.notify(0, mBuilder?.build())
        }

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun createNotification(
        context: Context?,
        notificationTitle: String?,
        notificationText: String?,
        notificationSubText: String?,
        notificationLargeIcon: Bitmap?,
        notificationSmallIcon: Int?
    ) {
        val notificationManager =
            context?.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        var mBuilder: NotificationCompat.Builder? =
            context?.let {
                context?.getString(R.string.default_notification_channel_id)
                    ?.let { it1 -> NotificationCompat.Builder(it, it1) }
            }

        mBuilder?.setContentTitle(notificationTitle.toString())
            ?.setContentText(notificationText.toString())
            ?.setSubText(notificationSubText.toString())
            ?.setLargeIcon(notificationLargeIcon)
            ?.setSmallIcon(notificationSmallIcon!!)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel(
                context?.getString(R.string.default_notification_channel_id),
                "NOTIFICATION_CHANNEL_NAME",
                importance
            )
            context?.getString(R.string.default_notification_channel_id)
                ?.let { mBuilder!!.setChannelId(it) }
            notificationManager.createNotificationChannel(notificationChannel)
        }
        notificationManager.notify()


    }


}