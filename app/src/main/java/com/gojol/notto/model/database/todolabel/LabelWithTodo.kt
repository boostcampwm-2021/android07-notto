package com.gojol.notto.model.database.todolabel

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.gojol.notto.model.database.label.Label
import com.gojol.notto.model.database.todo.Todo

data class LabelWithTodo(
    @Embedded val label: Label,
    @Relation(
        parentColumn = "labelId",
        entityColumn = "todoId",
        associateBy = Junction(TodoLabelCrossRef::class)
    )
    val todo: List<Todo>
)