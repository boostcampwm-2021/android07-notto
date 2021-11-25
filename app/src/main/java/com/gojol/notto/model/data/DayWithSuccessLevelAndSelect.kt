package com.gojol.notto.model.data

import com.gojol.notto.model.database.todo.DailyTodo

data class DayWithSuccessLevelAndSelect(
    val day: Int,
    val dailyTodos: List<DailyTodo>,
    val isSelected: Boolean
){
    val todoCount = dailyTodos.size
    val successLevel = DailyTodoSuccess(dailyTodos).getSuccessLevel()
}
