package com.gojol.notto.model.datasource.option

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OptionRepository @Inject constructor(
    private val optionLocalDataSource: OptionDataSource
) : OptionDataSource {

}
