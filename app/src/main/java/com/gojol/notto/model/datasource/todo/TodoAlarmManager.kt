package com.gojol.notto.model.datasource.todo

import com.gojol.notto.model.database.todo.Todo

interface TodoAlarmManager {

    fun addAlarm(todo: Todo)

    fun deleteAlarm(todo: Todo)
}