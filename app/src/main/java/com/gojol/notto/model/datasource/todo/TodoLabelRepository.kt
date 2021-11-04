package com.gojol.notto.model.datasource.todo

import com.gojol.notto.model.database.label.Label
import com.gojol.notto.model.database.todo.Todo
import com.gojol.notto.model.database.todolabel.LabelWithTodo
import com.gojol.notto.model.database.todolabel.TodoWithLabel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TodoLabelRepository @Inject constructor(
    private val localDataSource: TodoLabelDataSource
) : TodoLabelDataSource {

    override suspend fun getTodoWithLabel(): List<TodoWithLabel> {
        return localDataSource.getTodoWithLabel()
    }

    override suspend fun getLabelWithTodo(): List<LabelWithTodo> {
        return localDataSource.getLabelWithTodo()
    }

    override suspend fun getAllTodo(): List<Todo> {
        return localDataSource.getAllTodo()
    }

    override suspend fun getAllLabel(): List<Label> {
        return localDataSource.getAllLabel()
    }

    override suspend fun insertTodo(todo: Todo) {
        localDataSource.insertTodo(todo)
    }

    override suspend fun insertTodo(todo: Todo, label: Label) {
        localDataSource.insertTodo(todo, label)
    }

    override suspend fun insertLabel(label: Label) {
        localDataSource.insertLabel(label)
    }

    override suspend fun insertLabel(todo: Todo, label: Label) {
        localDataSource.insertLabel(todo, label)
    }

    override suspend fun updateTodo(todo: Todo) {
        localDataSource.updateTodo(todo)
    }

    override suspend fun updateTodo(todo: Todo, label: Label) {
        localDataSource.updateTodo(todo, label)
    }

    override suspend fun updateLabel(label: Label) {
        localDataSource.updateLabel(label)
    }

    override suspend fun deleteTodo(todo: Todo) {
        localDataSource.deleteTodo(todo)
    }

    override suspend fun deleteLabel(label: Label) {
        localDataSource.deleteLabel(label)
    }
}