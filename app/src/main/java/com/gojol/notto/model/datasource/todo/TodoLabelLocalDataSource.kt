package com.gojol.notto.model.datasource.todo

import com.gojol.notto.common.TodoState
import com.gojol.notto.model.data.TodoWithTodayDateState
import com.gojol.notto.model.database.label.Label
import com.gojol.notto.model.database.todo.DateState
import com.gojol.notto.model.database.todo.Todo
import com.gojol.notto.model.database.todo.TodoWithDateState
import com.gojol.notto.model.database.todolabel.LabelWithTodo
import com.gojol.notto.model.database.todolabel.TodoLabelCrossRef
import com.gojol.notto.model.database.todolabel.TodoLabelDao
import com.gojol.notto.model.database.todolabel.TodoWithLabel

class TodoLabelLocalDataSource(private val todoLabelDao: TodoLabelDao) :
    TodoLabelDataSource {

    override suspend fun getTodosWithLabels(): List<TodoWithLabel> {
        return todoLabelDao.getTodosWithLabels()
    }

    override suspend fun getTodosWithDateStates(): List<TodoWithDateState> {
        return todoLabelDao.getTodosWithDateStates()
    }

    override suspend fun getTodosWithTodayDateState(selectedDate: String): List<TodoWithTodayDateState> {
        return todoLabelDao.getTodosWithDateStates().map { todoWithDateState ->
            val todo = todoWithDateState.todo
            val dateStates = todoWithDateState.dateStates

            var todayDateState =
                dateStates.find { it.parentTodoId == todo.todoId && it.date == selectedDate }

            if (todayDateState == null) {
                todayDateState = DateState(TodoState.NOTHING, todo.todoId, selectedDate)
                todoLabelDao.insertDateState(todayDateState)
            }

            TodoWithTodayDateState(todo, todayDateState)
        }
    }

    override suspend fun getLabelsWithTodos(): List<LabelWithTodo> {
        return todoLabelDao.getLabelsWithTodos()
    }

    override suspend fun getAllTodo(): List<Todo> {
        return todoLabelDao.getAllTodo()
    }

    override suspend fun getAllLabel(): List<Label> {
        return todoLabelDao.getAllLabel()
    }

    override suspend fun insertTodo(todo: Todo) {
        todoLabelDao.insertTodo(todo)
    }

    override suspend fun insertTodo(todo: Todo, label: Label) {
        todoLabelDao.insert(TodoLabelCrossRef(todo.todoId, label.labelId))
    }

    override suspend fun insertLabel(label: Label) {
        todoLabelDao.insertLabel(label)
    }

    override suspend fun insertDateState(dateState: DateState) {
        todoLabelDao.insertDateState(dateState)
    }

    override suspend fun updateTodo(todo: Todo) {
        todoLabelDao.updateTodo(todo)
    }

    override suspend fun updateTodo(todo: Todo, labels: List<Label>) {
        labels.forEach {
            todoLabelDao.update(TodoLabelCrossRef(todo.todoId, it.labelId))
        }
    }

    override suspend fun updateLabel(label: Label) {
        todoLabelDao.updateLabel(label)
    }

    override suspend fun updateDateState(dateState: DateState) {
        todoLabelDao.updateDateState(dateState)
    }

    override suspend fun deleteTodo(todo: Todo) {
        todoLabelDao.deleteTodo(todo)
        todoLabelDao.deleteTodoLabelCrossRefByTodo(todo.todoId)
    }

    override suspend fun deleteLabel(label: Label) {
        todoLabelDao.deleteLabel(label)
        todoLabelDao.deleteTodoLabelCrossRefByLabel(label.labelId)
    }
}
