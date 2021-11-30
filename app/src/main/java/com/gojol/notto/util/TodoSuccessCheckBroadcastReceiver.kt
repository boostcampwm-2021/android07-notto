package com.gojol.notto.util

import android.content.Context
import android.content.Intent
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.gojol.notto.model.database.todo.Todo
import android.app.NotificationManager
import android.app.PendingIntent
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.WorkRequest
import com.gojol.notto.common.ACTION_FAIL
import com.gojol.notto.common.ACTION_SUCCESS
import com.gojol.notto.common.NOTIFICATION_TODO
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class TodoSuccessCheckBroadcastReceiver : HiltBroadcastReceiver() {

    private lateinit var workRequest: WorkRequest
    private val gson: Gson = Gson()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
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
        if(notificationManager.activeNotifications.size == LAST_NOTIFICATION_SIZE) {
            notificationManager.cancelAll()
        } else {
            notificationManager.cancel(todo.todoId)
        }
    }

    companion object {
        const val LAST_NOTIFICATION_SIZE = 2

        fun getPendingIntent(context: Context, id: Int, data: Uri, action: String): PendingIntent {
            val intent = Intent(context, TodoSuccessCheckBroadcastReceiver::class.java).apply {
                setData(data)
                setAction(action)
            }
            return PendingIntent.getBroadcast(
                context, id, intent,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                } else {
                    PendingIntent.FLAG_UPDATE_CURRENT
                }
            )
        }
    }
}
