package com.gojol.notto.ui.home

import com.gojol.notto.model.database.todolabel.LabelWithTodo

data class LabelWithCheck(
    val label: LabelWithTodo,
    val isChecked: Boolean,
)