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
import java.time.ZoneId
import java.util.*

@HiltWorker
class SuccessButtonWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    private val repository: TodoLabelRepository,
    private val alarmManager: TodoAlarmManager
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        return try {
            onButtonClick()
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    private suspend fun onButtonClick() {
        val currentDate =
            Date(System.currentTimeMillis()).toInstant().atZone(ZoneId.systemDefault())
                .toLocalDate()
        val todoId = inputData.getInt(NOTIFICATION_TODO, -1)

        repository.getTodosWithTodayDailyTodos(currentDate).forEach {
            val dailyTodo = it.todayDailyTodo
            if (todoId == it.todo.todoId) {
                repository.updateDailyTodo(dailyTodo.copy(todoState = TodoState.SUCCESS))
                alarmManager.deleteAlarm(it.todo, TodoState.SUCCESS)
            }
        }
    }
}
