package com.gojol.notto.model

interface SharedPrefManager {

    fun setBoolean(key: String, value: Boolean)

    fun getBoolean(key: String): Boolean
}
