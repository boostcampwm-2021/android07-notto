package com.gojol.notto.model.database.converter

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.LocalTime

class Converter {
    @TypeConverter
    fun stringToDate(date: String?): LocalDate? {
        return date?.let { LocalDate.parse(date) }
    }

    @TypeConverter
    fun dateToString(date: LocalDate?): String? {
        return date?.toString()
    }

    @TypeConverter
    fun stringToTime(time: String?): LocalTime? {
        return time?.let { LocalTime.parse(time) }
    }

    @TypeConverter
    fun timeToString(time: LocalTime?): String? {
        return time?.toString()
    }
}
