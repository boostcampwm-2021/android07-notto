package com.gojol.notto.util

import android.content.Context
import android.content.Intent
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.gojol.notto.model.database.todo.Todo
import com.gojol.notto.model.datasource.todo.TodoLabelRepository
import javax.inject.Inject
import android.app.NotificationManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.WorkRequest
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

const val SUCCESS_INTENT_ID = 1000000001
const val FAIL_INTENT_ID = 1000000002
const val NOTIFICATION_TODO = "notificationTodo"

class TodoSuccessCheckBroadcastReceiver : HiltBroadcastReceiver() {

    @Inject
    lateinit var repository: TodoLabelRepository

    lateinit var notificationManager: NotificationManager

    private lateinit var workRequest: WorkRequest
    private val gson: Gson = Gson()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val workManager = WorkManager.getInstance(context)

        val type: Type = object : TypeToken<Todo?>() {}.type
        val todo = gson.fromJson<Todo>(intent.data.toString(), type)
        todo ?: return

        if (intent.action == ACTION_SUCCESS) {
            val data = workDataOf(NOTIFICATION_TODO to todo.todoId)
            workRequest = OneTimeWorkRequestBuilder<SuccessButtonWorker>()
                .setInputData(data)
                .build()

        } else if (intent.action == ACTION_FAIL) {
            val data = workDataOf(NOTIFICATION_TODO to todo.todoId)
            workRequest = OneTimeWorkRequestBuilder<FailButtonWorker>()
                .setInputData(data)
                .build()
        }
        workManager.enqueue(workRequest)
        notificationManager.cancel(todo.todoId)
        if(notificationManager.activeNotifications.size == 1) {
            notificationManager.cancelAll()
        }
    }
}