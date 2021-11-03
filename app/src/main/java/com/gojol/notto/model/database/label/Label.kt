package com.gojol.notto.model.database.label

import androidx.room.*

@Entity
data class Label(
    @ColumnInfo(name = "order") val order: Int,
    @ColumnInfo(name = "name") val name: String,
    @PrimaryKey(autoGenerate = true) var labelId: Int = 0
)