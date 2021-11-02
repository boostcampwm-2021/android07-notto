package com.gojol.notto.model.database.todolabel

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.gojol.notto.model.database.label.Label
import com.gojol.notto.model.database.todo.Todo

data class TodoWithLabel(
    @Embedded val todo: Todo,
    @Relation(
        parentColumn = "todoId",
        entityColumn = "labelId",
        associateBy = Junction(TodoLabelCrossRef::class)
    )
    val labels: List<Label>
)