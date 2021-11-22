package com.gojol.notto.model.data

import java.time.LocalDate

data class MonthlyCalendar(
    val year: Int,
    val month: Int,
    val selectedDay: Int
) {
    private val baseDate = LocalDate.of(year, month, 1)
    val startDate: LocalDate = baseDate.withDayOfMonth(1)
    val endDate: LocalDate = baseDate.withDayOfMonth(baseDate.lengthOfMonth())

    fun getMonthlyDateList(): List<Int> {
        val dateList = (startDate.dayOfMonth..endDate.dayOfMonth).toList()
        val prefixDateList = IntArray(startDate.dayOfWeek.value).toList()
        return prefixDateList + dateList
    }
}
