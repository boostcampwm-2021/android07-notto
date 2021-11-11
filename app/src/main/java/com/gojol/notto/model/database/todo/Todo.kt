package com.gojol.notto.model.database.todo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.gojol.notto.model.data.RepeatType
import java.io.Serializable

@Entity
data class Todo(
    @ColumnInfo(name = "content") val content: String,
    @ColumnInfo(name = "is_repeated") val isRepeated: Boolean,
    @ColumnInfo(name = "repeat_type") val repeatType: RepeatType,
    @ColumnInfo(name = "start_date") val startDate: String,
    @ColumnInfo(name = "has_alarm") val hasAlarm: Boolean,
    @ColumnInfo(name = "start_time") val startTime: String,
    @ColumnInfo(name = "end_time") val endTime: String,
    @ColumnInfo(name = "period_time") val periodTime: String,
    @ColumnInfo(name = "is_keyword_open") val isKeywordOpen: Boolean,
    @ColumnInfo(name = "is_finished") val isFinished: Boolean,
    @ColumnInfo(name = "finish_date") val finishDate: String,
    @PrimaryKey(autoGenerate = true) var todoId: Int = 0
): Serializable
