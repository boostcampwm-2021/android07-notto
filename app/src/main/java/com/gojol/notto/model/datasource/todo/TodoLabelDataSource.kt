package com.gojol.notto.model.datasource.todo

import com.gojol.notto.model.database.label.Label
import com.gojol.notto.model.database.todo.Todo
import com.gojol.notto.model.database.todolabel.LabelWithTodo
import com.gojol.notto.model.database.todolabel.TodoWithLabel

interface TodoLabelDataSource {

    suspend fun getTodoWithLabel(): List<TodoWithLabel>

    suspend fun getLabelWithTodo(): List<LabelWithTodo>

    suspend fun getAllTodo(): List<Todo>

    suspend fun getAllLabel(): List<Label>

    suspend fun insertTodo(todo: Todo)

    suspend fun insertTodo(todo: Todo, label: Label)

    suspend fun insertLabel(label: Label)

    suspend fun updateTodo(todo: Todo)

    suspend fun updateTodo(todo: Todo, labels: List<Label>)

    suspend fun updateLabel(label: Label)

    suspend fun deleteTodo(todo: Todo)

    suspend fun deleteLabel(label: Label)
}
