package com.gojol.notto.model.datasource.option

interface OptionDataSource {

    fun isPushNotificationChecked(): Boolean

    fun saveIsPushNotificationChecked(isPushChecked: Boolean)
}
