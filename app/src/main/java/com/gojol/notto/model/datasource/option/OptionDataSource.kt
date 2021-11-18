package com.gojol.notto.model.datasource.option

interface OptionDataSource {

    fun loadIsPushNotificationChecked(): Boolean

    fun saveIsPushNotificationChecked(isPushChecked: Boolean)
}
