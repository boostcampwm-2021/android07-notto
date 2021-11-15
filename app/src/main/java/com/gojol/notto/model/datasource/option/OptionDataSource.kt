package com.gojol.notto.model.datasource.option

interface OptionDataSource {

    fun loadIsPushNotificationChecked(key: String): Boolean

    fun saveIsPushNotificationChecked(key: String, isPushChecked: Boolean)
}
