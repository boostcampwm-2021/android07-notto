package com.gojol.notto.model.database.todo

import androidx.room.Embedded
import androidx.room.Relation

data class TodoWithDateState(
    @Embedded val todo: Todo,
    @Relation(
        parentColumn = "todoId",
        entityColumn = "parent_todo_id"
    )
    val dateStates: List<DateState>
)
