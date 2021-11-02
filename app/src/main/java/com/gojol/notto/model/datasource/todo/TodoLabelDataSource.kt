package com.gojol.notto.model.datasource.todo

import com.gojol.notto.model.database.label.Label
import com.gojol.notto.model.database.todo.Todo
import com.gojol.notto.model.database.todolabel.LabelWithTodo
import com.gojol.notto.model.database.todolabel.TodoWithLabel

interface TodoLabelDataSource {
    fun insertTodo(todo: Todo)

    fun insertTodo(todo: Todo, label: Label)

    fun updateTodo(todo: Todo)

    fun updateTodo(todo: Todo, label: Label)

    fun deleteTodo(todo: Todo)

    fun getTodoWithLabel(): List<TodoWithLabel>

    fun insertLabel(label: Label)

    fun insertLabel(todo: Todo, label: Label)

    fun updateLabel(label: Label)

    fun deleteLabel(label: Label)

    fun getLabelWithTodo(): List<LabelWithTodo>
}