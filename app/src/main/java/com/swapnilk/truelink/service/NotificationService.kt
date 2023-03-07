package com.swapnilk.truelink.service

import android.app.Notification
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.swapnilk.truelink.MainActivity
import java.io.ByteArrayOutputStream


class NotificationService : NotificationListenerService() {
    var context: Context? = null
    private var mReceiver: MyReceiver = MyReceiver()

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        if (mReceiver == null) mReceiver = MyReceiver()
        registerReceiver(mReceiver, IntentFilter(MainActivity.INTENT_ACTION_NOTIFICATION))
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        /* val mNotification: Notification? = sbn.notification
         if (mNotification != null) {
             val extras: Bundle = mNotification.extras
             val intent = Intent(MainActivity.INTENT_ACTION_NOTIFICATION)
             intent.putExtras(mNotification.extras)
             sendBroadcast(intent)
         }*/

        val pack = sbn.packageName
        var ticker = ""
        if (sbn.notification.tickerText != null) {
            ticker = sbn.notification.tickerText.toString()
        }
        val extras = sbn.notification.extras
        val title = extras.getString("android.title")
        val text = extras.getCharSequence("android.text").toString()
        val text1 = extras.getCharSequence("android.subtext").toString()
        val id1 = extras.getInt(Notification.EXTRA_SMALL_ICON)
        val id = sbn.notification.largeIcon


        val msgrcv = Intent(MainActivity.INTENT_ACTION_NOTIFICATION)
        msgrcv.putExtra("package", pack)
        msgrcv.putExtra("ticker", ticker)
        msgrcv.putExtra("title", title)
        msgrcv.putExtra("text", text)
        msgrcv.putExtra("subtext", text1)
        if (id != null) {
            val stream = ByteArrayOutputStream()
            id.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val byteArray: ByteArray = stream.toByteArray()
            msgrcv.putExtra("icon", byteArray)
        }
        if (id1 != null) {

            msgrcv.putExtra("smallIcon", id1)
        }
        context?.let {
            sendBroadcast(msgrcv)
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {}
}