package com.gojol.notto.model.data.todo

import com.gojol.notto.common.TodoDeleteType
import com.gojol.notto.model.database.todo.Todo
import java.time.LocalDate

data class TodoWrapper(
    val todo: Todo,
    val existedTodo: Todo?,
    val selectedDate: LocalDate,
    val todoDeleteType: TodoDeleteType?
)
