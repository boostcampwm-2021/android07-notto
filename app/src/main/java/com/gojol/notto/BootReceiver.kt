package com.gojol.notto

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.gojol.notto.common.DAY_PUSH_NOTIFICATION_KEY
import com.gojol.notto.model.SharedPrefManager
import com.gojol.notto.ui.option.DayNotificationManager.setAlarm

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            val sharedPrefManager = context?.let { SharedPrefManager(it) }
            val isPushChecked = sharedPrefManager?.getBoolean(DAY_PUSH_NOTIFICATION_KEY)
            if (isPushChecked == true) setAlarm(context)
        }
    }
}
