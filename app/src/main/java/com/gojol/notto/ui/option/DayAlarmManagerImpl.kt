package com.gojol.notto.ui.option

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.core.content.getSystemService
import java.util.*
import javax.inject.Inject

class DayAlarmManagerImpl @Inject constructor(
    private val context: Context
) : DayAlarmManager {

    override fun setAlarm() {
        val alarmManager: AlarmManager = context.getSystemService() ?: return
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            getSelectedTime(),
            AlarmManager.INTERVAL_DAY,
            DayNotificationReceiver.getPendingIntentBroadcast(
                context,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                } else {
                    PendingIntent.FLAG_UPDATE_CURRENT
                }
            )
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
        alarmManager.cancel(
            DayNotificationReceiver.getPendingIntentBroadcast(
                context,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
                } else {
                    PendingIntent.FLAG_NO_CREATE
                }
            )
        )
    }
}
