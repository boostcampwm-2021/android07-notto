package com.gojol.notto.model.data

import com.gojol.notto.common.SuccessLevel

data class DayWithSuccessLevelAndSelect(
    val day: Int,
    val successLevel: SuccessLevel,
    val isSelected: Boolean
)
