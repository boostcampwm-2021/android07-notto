package com.gojol.notto.model.database.todo

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(primaryKeys = ["date"])
data class Date(
    @ColumnInfo(name = "date") val date: String,
    @ColumnInfo(name = "todo_state") val todoState: String,
)
