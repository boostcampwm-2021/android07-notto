package com.gojol.notto.model.data

import com.gojol.notto.model.database.todo.DailyTodo
import com.gojol.notto.model.database.todo.Todo

data class TodoWithTodayDailyTodo(
    val todo: Todo,
    val todayDailyTodo: DailyTodo
)
