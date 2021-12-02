package com.gojol.notto.model.datasource.todo

import com.gojol.notto.model.data.TodoWithTodayDailyTodo
import com.gojol.notto.model.database.label.Label
import com.gojol.notto.model.database.todo.DailyTodo
import com.gojol.notto.model.database.todo.Todo
import com.gojol.notto.model.database.todo.TodoWithDailyTodo
import com.gojol.notto.model.database.todolabel.LabelWithTodo
import com.gojol.notto.model.database.todolabel.TodoWithLabel
import java.time.LocalDate
import javax.inject.Inject

class TodoLabelRepositoryImpl @Inject constructor(
    private val localDataSource: TodoLabelLocalDataSource
) : TodoLabelRepository {

    override suspend fun getTodosWithLabels(): List<TodoWithLabel> {
        return localDataSource.getTodosWithLabels()
    }

    override suspend fun getTodosWithDailyTodos(): List<TodoWithDailyTodo> {
        return localDataSource.getTodosWithDailyTodos()
    }

    override suspend fun getTodosWithTodayDailyTodos(selectedDate: LocalDate): List<TodoWithTodayDailyTodo> {
        return localDataSource.getTodosWithTodayDailyTodos(selectedDate)
    }

    override suspend fun getLabelsWithTodos(): List<LabelWithTodo> {
        return localDataSource.getLabelsWithTodos()
    }

    override suspend fun getAllTodo(): List<Todo> {
        return localDataSource.getAllTodo()
    }

    override suspend fun getAllLabel(): List<Label> {
        return localDataSource.getAllLabel()
    }

    override suspend fun getAllDailyTodos(): List<DailyTodo> {
        return localDataSource.getAllDailyTodos()
    }

    override suspend fun insertTodo(todo: Todo, selectedDate: LocalDate): Long {
        return localDataSource.insertTodo(todo, selectedDate)
    }

    override suspend fun insertTodo(todo: Todo, label: Label) {
        localDataSource.insertTodo(todo, label)
    }

    override suspend fun insertLabel(label: Label) {
        localDataSource.insertLabel(label)
    }

    override suspend fun insertDailyTodo(dailyTodo: DailyTodo) {
        localDataSource.insertDailyTodo(dailyTodo)
    }

    override suspend fun insertDailyTodosWithDateRange(dateRange: List<LocalDate>) {
        localDataSource.insertDailyTodosWithDateRange(dateRange)
    }

    override suspend fun updateTodo(todo: Todo, selectedDate: LocalDate) {
        localDataSource.updateTodo(todo, selectedDate)
    }

    override suspend fun updateTodo(todo: Todo, labels: List<Label>) {
        localDataSource.updateTodo(todo, labels)
    }

    override suspend fun updateLabel(label: Label) {
        localDataSource.updateLabel(label)
    }

    override suspend fun updateDailyTodo(dailyTodo: DailyTodo) {
        localDataSource.updateDailyTodo(dailyTodo)
    }

    override suspend fun deleteTodo(todo: Todo) {
        localDataSource.deleteTodo(todo)
    }

    override suspend fun deleteSelectedTodo(todoId: Int, selectedDate: LocalDate) {
        localDataSource.deleteSelectedTodo(todoId, selectedDate)
    }

    override suspend fun deleteSelectedAndFutureTodo(todoId: Int, selectedDate: LocalDate) {
        localDataSource.deleteSelectedAndFutureTodo(todoId, selectedDate)
    }

    override suspend fun deleteLabel(label: Label) {
        localDataSource.deleteLabel(label)
    }
}
