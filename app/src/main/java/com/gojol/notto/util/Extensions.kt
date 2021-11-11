package com.gojol.notto.util

import java.text.SimpleDateFormat
import java.util.*

fun Calendar.toYearMonth(): String {
    return "${this.get(Calendar.YEAR)}년 ${this.get(Calendar.MONTH) + 1}월"
}

fun Calendar.getYear(): Int {
    return this.get(Calendar.YEAR)
}

fun Calendar.getMonth(): Int {
    return this.get(Calendar.MONTH)
}

fun Calendar.getDayOfWeek(): Int{
    return this.get(Calendar.DAY_OF_WEEK)
}

fun Calendar.getFirstDayOfMonth(): Int {
    return this.get(Calendar.DAY_OF_MONTH)
}

fun Calendar.getLastDayOfMonth(): Int {
    return this.getActualMaximum(Calendar.DATE)
}

fun Date.getDateString(): String {
    val simpleDateFormatDate = SimpleDateFormat("yyyy년 MM월 dd일", Locale.KOREA)
    return simpleDateFormatDate.format(this)
}

fun Date.getTimeString(): String {
    val simpleDateFormatTime = SimpleDateFormat("a hh:mm", Locale.KOREA)
    return simpleDateFormatTime.format(this)
}

fun String.getDate(): Date? {
    val simpleDateFormatDate = SimpleDateFormat("yyyy년 MM월 dd일", Locale.KOREA)
    return simpleDateFormatDate.parse(this)
}

fun String.getTime(): Date? {
    val simpleDateFormatTime = SimpleDateFormat("a hh:mm", Locale.KOREA)
    return simpleDateFormatTime.parse(this)
}

fun String.get12Time(): String {
    val simpleDateFormatTime = SimpleDateFormat("kk:mm", Locale.KOREA)
     simpleDateFormatTime.parse(this)?.let {
        return SimpleDateFormat("a hh:mm", Locale.KOREA).format(it)
    } ?: run {
        return SimpleDateFormat("a hh:mm", Locale.KOREA).format(System.currentTimeMillis())
     }
}

fun String.get24Time(): String {
    val simpleDateFormatTime = SimpleDateFormat("a hh:mm", Locale.KOREA)
    simpleDateFormatTime.parse(this)?.let {
        return SimpleDateFormat("kk:mm", Locale.KOREA).format(it)
    } ?: run {
        return SimpleDateFormat("kk:mm", Locale.KOREA).format(System.currentTimeMillis())
    }
}
