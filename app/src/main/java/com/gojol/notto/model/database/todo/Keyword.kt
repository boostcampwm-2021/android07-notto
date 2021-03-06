package com.gojol.notto.model.database.todo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Keyword (
    @ColumnInfo(name = "word") val word: String,
    @ColumnInfo(name = "parent_todo_id") val parentTodoId: Int,
    @PrimaryKey(autoGenerate = true) var keywordId: Int = 0
)
