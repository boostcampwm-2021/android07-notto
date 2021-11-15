package com.gojol.notto.model.datasource.option

import com.gojol.notto.model.SharedPrefManager
import javax.inject.Inject

class OptionLocalDataSource @Inject constructor(
    private val sharedPreferences: SharedPrefManager
) : OptionDataSource {
}
