package com.gojol.notto.model.database.todo

import androidx.room.Embedded
import androidx.room.Relation

data class TodoWithDailyTodo(
    @Embedded val todo: Todo,
    @Relation(
        parentColumn = "todoId",
        entityColumn = "parent_todo_id"
    )
    val dailyTodos: List<DailyTodo>
)
