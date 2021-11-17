package com.gojol.notto.model.datasource.option

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OptionRepository @Inject constructor(
    private val optionLocalDataSource: OptionDataSource
) : OptionDataSource {

    override fun loadIsPushNotificationChecked(): Boolean {
        return optionLocalDataSource.loadIsPushNotificationChecked()
    }

    override fun saveIsPushNotificationChecked(isPushChecked: Boolean) {
        optionLocalDataSource.saveIsPushNotificationChecked(isPushChecked)
    }
}
