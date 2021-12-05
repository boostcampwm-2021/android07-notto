package com.gojol.notto.model.datasource.option

interface OptionLocalDataSource {

    fun isPushNotificationChecked(): Boolean

    fun saveIsPushNotificationChecked(isPushChecked: Boolean)
}
