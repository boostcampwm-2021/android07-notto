package com.gojol.notto.ui.option

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.content.getSystemService
import java.util.*
import javax.inject.Inject

class DayAlarmManagerImpl @Inject constructor(
    private val context: Context
) : DayAlarmManager {

    override fun setAlarm() {
        val alarmManager: AlarmManager = context.getSystemService() ?: return
        val intent = Intent(context, DayNotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            DayNotificationReceiver.NOTIFICATION_ID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            getSelectedTime(),
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    private fun getSelectedTime(): Long {
        val currentTime = System.currentTimeMillis()
        val calendar = Calendar.getInstance().apply {
            timeInMillis = currentTime
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 50)
        }

        var selectedTime = calendar.timeInMillis
        if (currentTime > selectedTime) selectedTime += 24 * 60 * 60 * 1000 //하루
        return selectedTime
    }

    override fun cancelAlarm() {
        val alarmManager: AlarmManager = context.getSystemService() ?: return
        val intent = Intent(context, DayNotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context, DayNotificationReceiver.NOTIFICATION_ID, intent, PendingIntent.FLAG_NO_CREATE
        )
        pendingIntent?.let {
            alarmManager.cancel(pendingIntent)
        }
    }
}
