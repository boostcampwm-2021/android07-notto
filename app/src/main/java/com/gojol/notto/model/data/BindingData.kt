package com.gojol.notto.model.data

import com.gojol.notto.model.database.todo.Todo

data class BindingData(
    val todoList: List<Todo>?,
    val labelList: List<LabelWithCheck>?
)