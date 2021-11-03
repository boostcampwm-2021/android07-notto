package com.gojol.notto.model.datasource.todo

import com.gojol.notto.model.database.label.Label
import com.gojol.notto.model.database.todo.Todo
import com.gojol.notto.model.database.todolabel.LabelWithTodo
import com.gojol.notto.model.database.todolabel.TodoWithLabel
import javax.inject.Inject

class TodoLabelRepository @Inject constructor(
    private val localDataSource: TodoLabelDataSource
) : TodoLabelDataSource {

    // TODO suspend로 변경
    override fun insertTodo(todo: Todo) {
        localDataSource.insertTodo(todo)
    }

    override fun insertTodo(todo: Todo, label: Label) {
        localDataSource.insertTodo(todo, label)
    }

    override fun updateTodo(todo: Todo) {
        localDataSource.updateTodo(todo)
    }

    override fun updateTodo(todo: Todo, label: Label) {
        localDataSource.updateTodo(todo, label)
    }

    override fun deleteTodo(todo: Todo) {
        localDataSource.deleteTodo(todo)
    }

    override fun getTodoWithLabel(): List<TodoWithLabel> {
        return localDataSource.getTodoWithLabel()
    }

    override fun insertLabel(label: Label) {
        localDataSource.insertLabel(label)
    }

    override fun insertLabel(todo: Todo, label: Label) {
        localDataSource.insertLabel(todo, label)
    }

    override fun updateLabel(label: Label) {
        localDataSource.updateLabel(label)
    }

    override fun deleteLabel(label: Label) {
        localDataSource.deleteLabel(label)
    }

    override fun getLabelWithTodo(): List<LabelWithTodo> {
        return localDataSource.getLabelWithTodo()
    }
}