package com.gojol.notto.model.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Todo(
    @ColumnInfo(name = "is_success") val isSuccess: Boolean,
    @ColumnInfo(name = "label_list") val labelList: List<Label>,
    @ColumnInfo(name = "content") val content: String,
    @ColumnInfo(name = "target_date") val targetDate: String,
    @ColumnInfo(name = "is_repeat") val isRepeat: Boolean,
    @ColumnInfo(name = "repeat_type") val repeatType: RepeatType,
    @ColumnInfo(name = "has_alarm") val hasAlarm: Boolean,
    @ColumnInfo(name = "start_time") val startTime: String,
    @ColumnInfo(name = "end_time") val endTime: String,
    @ColumnInfo(name = "period_time") val periodTime: String,
    @ColumnInfo(name = "is_keyword_open") val isKeywordOpen: Boolean
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") var id: Int = 0
}