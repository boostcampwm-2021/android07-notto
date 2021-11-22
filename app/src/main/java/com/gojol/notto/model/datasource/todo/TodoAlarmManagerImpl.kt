package com.gojol.notto.model.datasource.todo

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.gojol.notto.model.database.todo.Todo
import com.gojol.notto.util.TodoPushBroadcastReceiver
import com.gojol.notto.util.getDate
import com.gojol.notto.util.getTotalTimeString
import javax.inject.Inject
import android.os.Bundle
import androidx.core.os.bundleOf
import com.gojol.notto.common.TodoState
import com.gojol.notto.model.database.todo.DailyTodo
import com.google.gson.Gson
import java.io.Serializable

const val ALARM_EXTRA_TODO = "alarmExtraTodo"

class TodoAlarmManagerImpl @Inject constructor(
    private val context: Context,
    private val alarmManager: AlarmManager
) : TodoAlarmManager {

    private val gson: Gson = Gson()

    @SuppressLint("UnspecifiedImmutableFlag")
    override fun addAlarm(todo: Todo) {
        if (!todo.hasAlarm) return

        val pendingIntent = TodoPushBroadcastReceiver.getPendingIntent(
            context,
            todo.todoId,
            bundleOf(
                // TODO: ALARM_EXTRA_TODO to todo로 하면 TodoPushBroadcastReceiver에서
                //  intent.extras?.getSerializable(ALARM_EXTRA_TODO)로 받아올 때 null이다??
                ALARM_EXTRA_TODO to gson.toJson(todo)
            )
        )

        val fullDate = ("${todo.startDate} ${todo.startTime}").getDate("yyyyMMdd a hh:mm")
        fullDate?.let { it ->
            val repeatInterval: Long = todo.periodTime.time.toInt() * 60 * 1000L
            val triggerTime = it.time

            alarmManager.setRepeating(
                AlarmManager.RTC,
                triggerTime, repeatInterval,
                pendingIntent
            )
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    override fun addAlarm(todo: Todo, todoState: TodoState) {
        if (!todo.hasAlarm) return
        if (todoState == TodoState.SUCCESS) return

        val pendingIntent = TodoPushBroadcastReceiver.getPendingIntent(
            context,
            todo.todoId,
            bundleOf(
                ALARM_EXTRA_TODO to gson.toJson(todo)
            )
        )

        val fullDate = ("${todo.startDate} ${todo.startTime}").getDate("yyyyMMdd a hh:mm")
        fullDate?.let { it ->
            val repeatInterval: Long = todo.periodTime.time.toInt() * 60 * 1000L
            val triggerTime = it.time

            alarmManager.setRepeating(
                AlarmManager.RTC,
                triggerTime, repeatInterval,
                pendingIntent
            )
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    override fun deleteAlarm(todo: Todo) {
        val todoTime = ("${todo.startDate} ${todo.endTime}").getDate("yyyyMMdd a hh:mm") ?: return
        val endTime = todoTime.time - todo.periodTime.time.toInt() * 60 * 1000

        if (!todo.isRepeated || endTime < System.currentTimeMillis()) {
            val pendingIntent = TodoPushBroadcastReceiver.getPendingIntent(context, todo.todoId)
            alarmManager.cancel(pendingIntent)
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    override fun deleteAlarm(todo: Todo, todoState: TodoState) {
        if (todoState == TodoState.SUCCESS) {
            val pendingIntent = TodoPushBroadcastReceiver.getPendingIntent(context, todo.todoId)
            alarmManager.cancel(pendingIntent)
        }
    }
}