package com.gojol.notto.model

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedPrefManager @Inject constructor(@ApplicationContext context: Context) {

    private val prefs = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)

    fun setBoolean(key: String, value: Boolean) {
        val editor = prefs.edit()
        editor.apply {
            putBoolean(key, value)
            apply()
        }
    }

    fun getBoolean(key: String): Boolean {
        val defaultValue = false
        return prefs.getBoolean(key, defaultValue)
    }

    companion object {
        private const val FILE_NAME = "pushNotification"
    }
}
