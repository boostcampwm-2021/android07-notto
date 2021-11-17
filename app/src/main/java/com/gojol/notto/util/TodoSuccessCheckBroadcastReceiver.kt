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

const val SUCCESS_INTENT_ID = 1000000001
const val FAIL_INTENT_ID = 1000000002
const val NOTIFICATION_TODO = "notificationTodo"

class TodoSuccessCheckBroadcastReceiver : HiltBroadcastReceiver() {

    @Inject
    lateinit var repository: TodoLabelRepository

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        if (intent.getStringExtra(ACTION_STATE) == ACTION_SUCCESS) {
            intent.getBundleExtra(ACTION_BUNDLE)?.let {
                val todo = it.getSerializable(ACTION_TODO_DATA) as Todo
                val data = workDataOf(NOTIFICATION_TODO to todo.todoId)

                val workManager = WorkManager.getInstance(context)
                val workRequest =
                    OneTimeWorkRequestBuilder<SuccessButtonWorker>()
                        //.setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                        .setInputData(data)
                        .build()
                workManager.enqueue(workRequest)

                val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                manager.cancel(todo.todoId)
            }
        } else if (intent.getStringExtra(ACTION_STATE) == ACTION_FAIL) {
            intent.getBundleExtra(ACTION_BUNDLE)?.let {
                val todo = it.getSerializable(ACTION_TODO_DATA) as Todo
                val data = workDataOf(NOTIFICATION_TODO to todo.todoId)

                val workManager = WorkManager.getInstance(context)
                val workRequest =
                    OneTimeWorkRequestBuilder<FailButtonWorker>()
                        //.setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                        .setInputData(data)
                        .build()
                workManager.enqueue(workRequest)

                val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                manager.cancel(todo.todoId)
            }
        }
    }
}