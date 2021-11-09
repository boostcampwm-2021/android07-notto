package com.gojol.notto.model.data

import com.gojol.notto.model.database.todolabel.LabelWithTodo

data class LabelWithCheck(
    val labelWithTodo: LabelWithTodo,
    var isChecked: Boolean,
)