package com.gojol.notto.common

enum class TodoState {
    SUCCESS, FAIL, NOTHING
}

enum class AdapterViewType(val viewType: Int) {
    CALENDAR(0), LABELWRAPPER(1), LABEL(2), TODO(3)
}

enum class EditType(val text: String) {
    CREATE("라벨 추가"), UPDATE("라벨 수정")
}
