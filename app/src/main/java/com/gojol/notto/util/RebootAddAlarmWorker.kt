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

@HiltWorker
class RebootAddAlarmWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    private val repository: TodoLabelRepository,
    private val todoAlarmManager: TodoAlarmManager
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = runCatching {
        recreateAlarm()
        Result.success()
    }.getOrElse {
        Log.d(this.javaClass.name, it.toString())
        Result.failure()
    }

    private suspend fun recreateAlarm() {
        todoAlarmManager.updateAlarms()
    }
}
