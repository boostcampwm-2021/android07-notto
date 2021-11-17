package com.gojol.notto.ui.option

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.getSystemService

class DayNotificationReceiver : BroadcastReceiver() {

    private var notificationManager: NotificationManager? = null

    override fun onReceive(context: Context, intent: Intent) {
        notificationManager = context.getSystemService()
        DayNotificationManager.createNotificationChannel(context)
        DayNotificationManager.showNotification(context)
    }

    companion object {
        const val TAG = "DayPushReceiver"
        const val NOTIFICATION_ID = 0
        const val CHANNEL_ID = "day_notification"
        const val CHANNEL_NAME = "day_push"
    }
}
