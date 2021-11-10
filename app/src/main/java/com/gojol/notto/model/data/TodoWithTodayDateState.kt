package com.gojol.notto.model.data

import com.gojol.notto.model.database.todo.DateState
import com.gojol.notto.model.database.todo.Todo

data class TodoWithTodayDateState(
    val todo: Todo,
    val todayDateState: DateState
)
