package com.gojol.notto.model.data

data class DayWithSuccessLevelAndSelect(
    val day: Int,
    val successLevel: Success.Level,
    val isSelected: Boolean
)
