package com.gojol.notto.ui.option

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class DayNotificationReceiver : BroadcastReceiver() {

    private lateinit var notificationManager: NotificationManager

    override fun onReceive(context: Context?, intent: Intent?) {
        notificationManager =
            context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        context.let {
            DayNotificationManager.createNotificationChannel(it)
            DayNotificationManager.showNotification(it)
        }
    }

    companion object {
        const val TAG = "DayPushReceiver"
        const val NOTIFICATION_ID = 0
        const val CHANNEL_ID = "day_notification"
        const val CHANNEL_NAME = "day_push"
    }
}
