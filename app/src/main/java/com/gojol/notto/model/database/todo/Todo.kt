package com.gojol.notto.model.database.todo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.gojol.notto.common.TimeRepeatType
import com.gojol.notto.model.data.RepeatType
import java.io.Serializable
import java.time.LocalDate
import java.time.LocalTime

@Entity
data class Todo(
    @ColumnInfo(name = "content") val content: String,
    @ColumnInfo(name = "is_repeated") val isRepeated: Boolean,
    @ColumnInfo(name = "repeat_type") val repeatType: RepeatType,
    @ColumnInfo(name = "start_date") val startDate: LocalDate,
    @ColumnInfo(name = "has_alarm") val hasAlarm: Boolean,
    @ColumnInfo(name = "start_time") val startTime: LocalTime,
    @ColumnInfo(name = "end_time") val endTime: LocalTime,
    @ColumnInfo(name = "period_time") val periodTime: TimeRepeatType,
    @ColumnInfo(name = "is_keyword_open") val isKeywordOpen: Boolean,
    @ColumnInfo(name = "is_finished") val isFinished: Boolean,
    @ColumnInfo(name = "finish_date") val finishDate: LocalDate,
    @PrimaryKey(autoGenerate = true) var todoId: Int = 0
): Serializable
