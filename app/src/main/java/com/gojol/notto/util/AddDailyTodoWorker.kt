package com.gojol.notto.util

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.gojol.notto.model.datasource.todo.TodoAlarmManager
import com.gojol.notto.model.datasource.todo.TodoLabelRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.ZoneId
import java.util.*

@HiltWorker
class AddDailyTodoWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val repository: TodoLabelRepository,
    private val todoAlarmManager: TodoAlarmManager
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result = runCatching {
        addTomorrowDailyTodosAlarm()
        Result.success()
    }.getOrElse {
        Log.d(this.javaClass.name, it.toString())
        Result.failure()
    }

    private suspend fun addTomorrowDailyTodosAlarm() {
        val calendar = Calendar.getInstance()
        if (calendar.get(Calendar.HOUR_OF_DAY) < 23) return
        // 다음날을 가져오기 위해서 밤 11시 + 1시간 = 12시를 표시
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        repository.getTodosWithTodayDailyTodos(
            calendar.time.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
        ).forEach {
            val todo = it.todo
            val dailyTodo = it.todayDailyTodo
            todoAlarmManager.addAlarm(todo, dailyTodo.todoState)
        }
    }
}
