package com.gojol.notto.ui.option

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.os.Build
import android.os.SystemClock
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.gojol.notto.R
import com.gojol.notto.ui.MainActivity
import java.util.*

private const val notificationDescription = "오늘의 마무리 알림입니다."
private const val notificationTitle = "오늘의 마무리 알림"
private const val notificationContent = "하루를 마무리하기 전에 Not Todo를 체크해주세요!"

object DayNotificationManager {

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                DayNotificationReceiver.CHANNEL_ID,
                DayNotificationReceiver.CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = notificationDescription
            }

            with(NotificationManagerCompat.from(context)) {
                createNotificationChannel(notificationChannel)
            }
        }
    }

    fun showNotification(context: Context) {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            DayNotificationReceiver.NOTIFICATION_ID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notificationBuilder = NotificationCompat.Builder(
            context,
            DayNotificationReceiver.CHANNEL_ID
        )
            .setSmallIcon(R.mipmap.ic_notto_launcher_foreground)
            .setContentTitle(notificationTitle)
            .setContentText(notificationContent)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)

        with(NotificationManagerCompat.from(context)) {
            notify(DayNotificationReceiver.NOTIFICATION_ID, notificationBuilder.build())
        }
    }

    fun setAlarm(context: Context) {
        val alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, DayNotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            DayNotificationReceiver.NOTIFICATION_ID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 50)

        val triggerTime = (SystemClock.elapsedRealtime() + 10 * 1000)
        alarmManager.set(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }

    fun cancelAlarm(context: Context) {
        val alarmManager = context.getSystemService(ALARM_SERVICE) as? AlarmManager
        val intent = Intent(context, DayNotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context, DayNotificationReceiver.NOTIFICATION_ID, intent, PendingIntent.FLAG_NO_CREATE
        )
        if (pendingIntent != null && alarmManager != null) {
            alarmManager.cancel(pendingIntent)
        }
    }
}
