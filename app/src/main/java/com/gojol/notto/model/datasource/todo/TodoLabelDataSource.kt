package com.gojol.notto.model.datasource.todo

import com.gojol.notto.model.data.TodoWithTodayDailyTodo
import com.gojol.notto.model.database.label.Label
import com.gojol.notto.model.database.todo.DailyTodo
import com.gojol.notto.model.database.todo.Todo
import com.gojol.notto.model.database.todo.TodoWithDailyTodo
import com.gojol.notto.model.database.todolabel.LabelWithTodo
import com.gojol.notto.model.database.todolabel.TodoWithLabel

interface TodoLabelDataSource {

    suspend fun getTodosWithLabels(): List<TodoWithLabel>

    suspend fun getTodosWithDailyTodos(): List<TodoWithDailyTodo>

    suspend fun getTodosWithTodayDailyTodos(selectedDate: String): List<TodoWithTodayDailyTodo>

    suspend fun getLabelsWithTodos(): List<LabelWithTodo>

    suspend fun getAllTodo(): List<Todo>

    suspend fun getAllLabel(): List<Label>

    suspend fun insertTodo(todo: Todo)

    suspend fun insertTodo(todo: Todo, label: Label)

    suspend fun insertLabel(label: Label)

    suspend fun insertDailyTodo(dailyTodo: DailyTodo)

    suspend fun updateTodo(todo: Todo)

    suspend fun updateTodo(todo: Todo, labels: List<Label>)

    suspend fun updateLabel(label: Label)

    suspend fun updateDailyTodo(dailyTodo: DailyTodo)

    suspend fun deleteTodo(todo: Todo)

    suspend fun deleteTodayTodo(todoId: Int, selectedDate: String)

    suspend fun deleteTodayAndFutureTodo(todoId: Int, selectedDate: String)

    suspend fun deleteLabel(label: Label)
}
