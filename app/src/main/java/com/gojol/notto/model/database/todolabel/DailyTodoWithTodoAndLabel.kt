package com.gojol.notto.model.database.todolabel

import androidx.room.Embedded
import androidx.room.Relation
import com.gojol.notto.model.database.todo.DailyTodo
import com.gojol.notto.model.database.todo.Todo

data class DailyTodoWithTodoAndLabel(
    @Embedded val dailyTodo: DailyTodo,
    @Relation(
        entity = Todo::class,
        parentColumn = "parent_todo_id",
        entityColumn = "todoId"
    )
    val todos: List<TodoWithLabel>
)
