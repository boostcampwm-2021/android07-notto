package com.gojol.notto.common

enum class TodoState {
    SUCCESS, FAIL, NOTHING
}

enum class AdapterViewType(val viewType: Int) {
    CALENDAR(0), LABELWRAPPER(1), LABEL(2), TODO(3)
}

enum class TimeRepeatType(val time: String, val text: String) {
    MINUTE_5("5", "5분"), MINUTE_10("10", "10분"), MINUTE_15("15", "15분"),
    MINUTE_30("30", "30분"), MINUTE_60("60", "1시간"), MINUTE_1440("1440", "1일")
}

enum class EditType(val text: String) {
    CREATE("라벨 추가"), UPDATE("라벨 수정")
}

enum class TodoDeleteType {
    TODAY, TODAY_AND_FUTURE
}

enum class SuccessLevel(val value: Int, val maxValue: Float) {
    ZERO(0, 0f), ONE(1, 0.25f), TWO(2, 0.5f),
    THREE(3, 0.75f), FOUR(4, 1f), FIVE(5, 1f);

    fun toIntAlpha(): Int {
        return 51 * value
    }
}
