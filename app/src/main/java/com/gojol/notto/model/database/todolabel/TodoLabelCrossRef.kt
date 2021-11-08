package com.gojol.notto.model.database.todolabel

import androidx.room.Entity

@Entity(primaryKeys = ["todoId", "labelId"])
data class TodoLabelCrossRef(
    val todoId: Int,
    val labelId: Int
)