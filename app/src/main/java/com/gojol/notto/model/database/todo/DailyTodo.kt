package com.gojol.notto.model.database.todo

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.gojol.notto.common.TodoState

@Entity(primaryKeys = ["date", "parent_todo_id"])
data class DailyTodo(
    @ColumnInfo(name = "todo_state") val todoState: TodoState,
    @ColumnInfo(name = "is_active") val isActive: Boolean,
    @ColumnInfo(name = "parent_todo_id") val parentTodoId: Int,
    @ColumnInfo(name = "date") val date: String
)
