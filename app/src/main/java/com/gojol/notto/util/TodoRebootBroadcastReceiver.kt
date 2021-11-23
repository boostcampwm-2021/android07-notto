package com.gojol.notto.util

import android.content.Context
import android.content.Intent
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.gojol.notto.common.DAY_PUSH_NOTIFICATION_KEY
import com.gojol.notto.model.SharedPrefManager
import com.gojol.notto.ui.option.DayNotificationManager

class TodoRebootBroadcastReceiver : HiltBroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        if (intent.action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            // DailyNotify
            val sharedPrefManager = SharedPrefManager(context)
            val isPushChecked = sharedPrefManager.getBoolean(DAY_PUSH_NOTIFICATION_KEY)
            if (isPushChecked) DayNotificationManager.setAlarm(context)

            // 디바이스가 재부팅될 때는 즉시 알람을 다시 생성해야된다. 따라서 24시간마다 바뀌는 알람과는 관계없이 진행해야할 것
            val workRequest =
                OneTimeWorkRequestBuilder<RebootAddAlarmWorker>()
                    .build()

            val workManager = WorkManager.getInstance(context)
            workManager.enqueue(workRequest)
        }
    }
}