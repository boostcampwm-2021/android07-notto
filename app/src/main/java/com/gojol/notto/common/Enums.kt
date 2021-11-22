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
    SELECTED, SELECTED_AND_FUTURE
}
