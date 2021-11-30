package com.gojol.notto.ui.home.util

import java.time.LocalDate

fun interface DayClickListener {
    fun onClick(selectedDate: LocalDate)
}
