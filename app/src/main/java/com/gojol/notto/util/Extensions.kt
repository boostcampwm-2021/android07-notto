package com.gojol.notto.util

import android.util.Log
import java.util.*

fun Calendar.toYearMonth() : String {
    Log.d("Extensions", "${this.get(Calendar.YEAR)}/${this.get(Calendar.MONTH)}/${this.get(Calendar.DAY_OF_MONTH)}")
    return "${this.get(Calendar.YEAR)}년 ${this.get(Calendar.MONTH) + 1}월"
}
