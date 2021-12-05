package com.gojol.notto.util

import kr.bydelta.koala.isHangulEnding
import kr.bydelta.koala.isJongsungEnding
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

fun Date.getTimeString(): String {
    val simpleDateFormatTime = SimpleDateFormat("a hh:mm", Locale.KOREA)
    return simpleDateFormatTime.format(this)
}

fun String?.isShorterThanMinLabelLength(): Boolean {
    return if (this == null) false
    else this.isHangulEnding() && this.length < 2 || this.isHangulEnding().not() && this.length < 3
}

fun LocalTime.toDialogString(): String {
    return when {
        this.hour < 12 -> {
            this.format(DateTimeFormatter.ofPattern("a HH:mm"))
        }
        this.hour == 12 -> {
            this.format(DateTimeFormatter.ofPattern("a HH:mm"))
        }
        else -> {
            this.format(DateTimeFormatter.ofPattern("a hh:mm"))
        }
    }
}

fun String?.addEulLeul(): String {
    if (this == null) return "키워드를"
    return if (this.isJongsungEnding()) "[${this}]을" else "[${this}]를"
}
