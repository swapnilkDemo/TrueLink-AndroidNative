package com.swapnilk.truelink.service

import android.app.Notification
import android.content.Intent
import android.os.Bundle
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.swapnilk.truelink.MainActivity


class NotificationService : NotificationListenerService() {
    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val mNotification: Notification? = sbn.notification
        if (mNotification != null) {
            val extras: Bundle = mNotification.extras
            val intent = Intent(MainActivity.INTENT_ACTION_NOTIFICATION)
            intent.putExtras(mNotification.extras)
            sendBroadcast(intent)
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {}
}