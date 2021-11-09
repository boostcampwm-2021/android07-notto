package com.gojol.notto.common

enum class TodoSuccessType {
    SUCCESS, FAIL, NOTHING
}

enum class AdapterViewType(val viewType: Int) {
    CALENDAR(0), LABELWRAPPER(1), LABEL(2), TODO(3)
}

enum class EditType() {
    CREATE, UPDATE;
}
