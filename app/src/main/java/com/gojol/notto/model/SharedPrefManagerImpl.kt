package com.gojol.notto.model

import android.content.Context
import javax.inject.Inject

class SharedPrefManagerImpl @Inject constructor(
    private val context: Context
) : SharedPrefManager {

    private val prefs = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)

    override fun setBoolean(key: String, value: Boolean) {
        prefs.edit()
            .putBoolean(key, value)
            .apply()
    }

    override fun getBoolean(key: String): Boolean {
        return prefs.getBoolean(key, false)
    }

    companion object {
        private const val FILE_NAME = "pushNotification"
    }
}
