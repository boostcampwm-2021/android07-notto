package com.gojol.notto.model.datasource.option

import com.gojol.notto.common.DAY_PUSH_NOTIFICATION_KEY
import com.gojol.notto.model.SharedPrefManager
import javax.inject.Inject

class OptionLocalDataSourceImpl @Inject constructor(
    private val sharedPreferences: SharedPrefManager
) : OptionLocalDataSource {

    override fun isPushNotificationChecked(): Boolean {
        return sharedPreferences.getBoolean(DAY_PUSH_NOTIFICATION_KEY)
    }

    override fun saveIsPushNotificationChecked(isPushChecked: Boolean) {
        sharedPreferences.setBoolean(DAY_PUSH_NOTIFICATION_KEY, isPushChecked)
    }
}
