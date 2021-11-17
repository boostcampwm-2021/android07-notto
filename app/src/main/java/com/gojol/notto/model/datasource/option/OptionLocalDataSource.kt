package com.gojol.notto.model.datasource.option

import com.gojol.notto.common.DAY_PUSH_NOTIFICATION_KEY
import com.gojol.notto.model.SharedPrefManager
import javax.inject.Inject

class OptionLocalDataSource @Inject constructor(
    private val sharedPreferences: SharedPrefManager
) : OptionDataSource {

    override fun loadIsPushNotificationChecked(): Boolean {
        return sharedPreferences.getBoolean(DAY_PUSH_NOTIFICATION_KEY)
    }

    override fun saveIsPushNotificationChecked(isPushChecked: Boolean) {
        sharedPreferences.setBoolean(DAY_PUSH_NOTIFICATION_KEY, isPushChecked)
    }
}
