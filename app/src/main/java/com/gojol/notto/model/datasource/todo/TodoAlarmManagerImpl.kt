package com.gojol.notto.model.datasource.todo

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.gojol.notto.model.database.todo.Todo
import com.gojol.notto.util.TodoPushBroadcastReceiver
import javax.inject.Inject
import com.gojol.notto.common.TodoState
import com.google.gson.Gson
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

const val ALARM_EXTRA_TODO = "alarmExtraTodo"

class TodoAlarmManagerImpl @Inject constructor(
    private val context: Context,
    private val alarmManager: AlarmManager
) : TodoAlarmManager {

    private val gson: Gson = Gson()

    @SuppressLint("UnspecifiedImmutableFlag")
    override fun addAlarm(todo: Todo) {
        if (!todo.hasAlarm) return

        val intent = Intent(context, TodoPushBroadcastReceiver::class.java).apply {
            putExtra(ALARM_EXTRA_TODO, gson.toJson(todo))
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context, todo.todoId, intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val fullDateTime = LocalDateTime.of(todo.startDate, todo.startTime)
        val repeatInterval: Long = todo.periodTime.time.toInt() * 60 * 1000L
        val triggerTime = Date.from(fullDateTime.atZone(ZoneId.systemDefault()).toInstant()).time

        alarmManager.setRepeating(
            AlarmManager.RTC,
            triggerTime, repeatInterval,
            pendingIntent
        )
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    override fun deleteAlarm(todo: Todo) {
        val todoDateTime = LocalDateTime.of(todo.startDate, todo.endTime)
        val todoDate = Date.from(todoDateTime.atZone(ZoneId.systemDefault()).toInstant()).time
        val endTime = todoDate - todo.periodTime.time.toInt() * 60 * 1000

        if (!todo.isRepeated || endTime < System.currentTimeMillis()) {
            val intent = Intent(context, TodoPushBroadcastReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context, todo.todoId, intent, PendingIntent.FLAG_UPDATE_CURRENT
            )
            alarmManager.cancel(pendingIntent)
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    override fun deleteAlarm(todo: Todo, todoState: TodoState) {
        if (todoState == TodoState.SUCCESS) {
            val intent = Intent(context, TodoPushBroadcastReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context, todo.todoId, intent, PendingIntent.FLAG_UPDATE_CURRENT
            )
            alarmManager.cancel(pendingIntent)
        }
    }
}
