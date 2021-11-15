package com.gojol.notto.model.datasource.option

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OptionRepository @Inject constructor(
    private val optionLocalDataSource: OptionDataSource
) : OptionDataSource {

    override fun loadIsPushNotificationChecked(key: String): Boolean {
        return optionLocalDataSource.loadIsPushNotificationChecked(key)
    }

    override fun saveIsPushNotificationChecked(key: String, isPushChecked: Boolean) {
        optionLocalDataSource.saveIsPushNotificationChecked(key, isPushChecked)
    }
}
