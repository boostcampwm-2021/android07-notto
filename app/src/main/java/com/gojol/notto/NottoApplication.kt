package com.gojol.notto

import android.app.Application
import com.facebook.stetho.Stetho
import com.gojol.notto.model.SharedPrefManager
import com.gojol.notto.ui.option.DayNotificationManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class NottoApplication : Application() {

    private val sharedPref by lazy {
        SharedPrefManager(this)
    }

    override fun onCreate() {
        super.onCreate()

        Stetho.initializeWithDefaults(this)
        initDayNotification()
    }

    private fun initDayNotification() {
        val isPushChecked = sharedPref.getBoolean(pushNotificationKey)
        if (isPushChecked) DayNotificationManager.setAlarm(this)
        else DayNotificationManager.cancelAlarm(this)
    }

    companion object {
        const val pushNotificationKey = "pushDay"
    }
}
