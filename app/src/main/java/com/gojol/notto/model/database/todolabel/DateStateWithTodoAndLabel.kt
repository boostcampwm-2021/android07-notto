package com.gojol.notto.model.database.todolabel

import androidx.room.Embedded
import androidx.room.Relation
import com.gojol.notto.model.database.todo.DateState
import com.gojol.notto.model.database.todo.Todo

data class DateStateWithTodoAndLabel(
    @Embedded val dateState: DateState,
    @Relation(
        entity = Todo::class,
        parentColumn = "parent_todo_id",
        entityColumn = "todoId"
    )
    val todos: List<TodoWithLabel>
)
