package com.gojol.notto.model.data

import com.gojol.notto.model.database.label.Label
import com.gojol.notto.model.database.todo.Todo
import com.gojol.notto.ui.home.LabelWithCheck

data class BindingData(
    val todoList: List<Todo>?,
    val labelList: List<LabelWithCheck>?
)