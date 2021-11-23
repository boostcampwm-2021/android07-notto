package com.gojol.notto.model.data

data class DayWithSuccessLevelAndSelect(
    val day: Int,
    val successLevel: DailyTodoSuccess.Level,
    val isSelected: Boolean
)
