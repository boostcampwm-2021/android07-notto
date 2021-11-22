package com.gojol.notto.ui.option

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.getSystemService
import com.gojol.notto.R
import com.gojol.notto.ui.MainActivity

class DayNotificationReceiver : BroadcastReceiver() {

    private var notificationManager: NotificationManager? = null

    override fun onReceive(context: Context, intent: Intent) {
        notificationManager = context.getSystemService()
        createNotificationChannel(context)
        showNotification(context)
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = context.getString(R.string.option_notification_description)
            }

            with(NotificationManagerCompat.from(context)) {
                createNotificationChannel(notificationChannel)
            }
        }
    }

    private fun showNotification(context: Context) {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_ID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notificationBuilder = NotificationCompat.Builder(
            context,
            CHANNEL_ID
        )
            .setSmallIcon(R.mipmap.ic_notto_launcher_foreground)
            .setContentTitle(context.getString(R.string.option_notification_title))
            .setContentText(context.getString(R.string.option_notification_content))
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)

        with(NotificationManagerCompat.from(context)) {
            notify(NOTIFICATION_ID, notificationBuilder.build())
        }
    }

    companion object {
        const val TAG = "DayPushReceiver"
        const val NOTIFICATION_ID = 0
        const val CHANNEL_ID = "day_notification"
        const val CHANNEL_NAME = "day_push"
    }
}
