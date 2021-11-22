package com.gojol.notto.util

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.gojol.notto.common.TodoState
import com.gojol.notto.model.datasource.todo.TodoAlarmManager
import com.gojol.notto.model.datasource.todo.TodoLabelRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.lang.Exception
import java.util.*

const val UPDATE_TOMORROW_DAILY_TODO = "com.notto.util.AddDailyTodoWorker"

@HiltWorker
class AddDailyTodoWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val repository: TodoLabelRepository,
    private val todoAlarmManager: TodoAlarmManager
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        return try {
            addTomorrowDailyTodosAlarm()
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    private suspend fun addTomorrowDailyTodosAlarm() {
        val calendar = Calendar.getInstance()
        if (calendar.get(Calendar.HOUR_OF_DAY) >= 23) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
            repository.getTodosWithTodayDailyTodos(calendar.time.getDateString()).forEach {
                val todo = it.todo
                val dailyTodo = it.todayDailyTodo
                todoAlarmManager.addAlarm(todo, dailyTodo.todoState)
            }
        }
    }
}