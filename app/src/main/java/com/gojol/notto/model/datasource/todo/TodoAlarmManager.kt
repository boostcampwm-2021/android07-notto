package com.gojol.notto.model.datasource.todo

import com.gojol.notto.common.TodoState
import com.gojol.notto.model.database.todo.Todo

interface TodoAlarmManager {

    fun addAlarm(todo: Todo)

    fun addAlarm(todo: Todo, todoState: TodoState)

    fun deleteAlarm(todo: Todo)

    fun deleteAlarm(todo: Todo, todoState: TodoState)

    suspend fun deleteAlarms()
}