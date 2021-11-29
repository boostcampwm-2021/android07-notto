package com.gojol.notto.model.datasource.todo

import android.app.AlarmManager
import android.content.Context
import com.gojol.notto.model.database.todo.Todo
import com.gojol.notto.util.TodoPushBroadcastReceiver
import javax.inject.Inject
import androidx.core.os.bundleOf
import com.gojol.notto.common.ALARM_EXTRA_TODO
import com.gojol.notto.common.TodoState
import java.io.Serializable
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

class TodoAlarmManagerImpl @Inject constructor(
    private val context: Context,
    private val alarmManager: AlarmManager,
    private val repository: TodoLabelRepository
) : TodoAlarmManager {

    override fun addAlarm(todo: Todo) {
        if (!todo.hasAlarm) return

        val pendingIntent = TodoPushBroadcastReceiver.getPendingIntent(
            context,
            todo.todoId,
            bundleOf(
                ALARM_EXTRA_TODO to (todo as Serializable)
            )
        )

        val fullDateTime = LocalDateTime.of(todo.startDate, todo.startTime)
        val repeatInterval: Long = todo.periodTime.time.toInt() * 60 * 1000L
        var triggerTime = Date.from(fullDateTime.atZone(ZoneId.systemDefault()).toInstant()).time
        triggerTime = modifyRepeatTime(triggerTime, repeatInterval)

        alarmManager.setRepeating(
            AlarmManager.RTC,
            triggerTime,
            repeatInterval,
            pendingIntent
        )
    }

    override fun addAlarm(todo: Todo, todoState: TodoState) {
        if (!todo.hasAlarm) return
        if (todoState == TodoState.SUCCESS) return

        val todoDateTime = LocalDateTime.of(todo.startDate, todo.endTime)
        val todoDate = Date.from(todoDateTime.atZone(ZoneId.systemDefault()).toInstant()).time
        if (todoDate < System.currentTimeMillis()) return

        val pendingIntent = TodoPushBroadcastReceiver.getPendingIntent(
            context,
            todo.todoId,
            bundleOf(
                ALARM_EXTRA_TODO to (todo as Serializable)
            )
        )

        val fullDateTime = LocalDateTime.of(todo.startDate, todo.startTime)
        val repeatInterval: Long = todo.periodTime.time.toInt() * 60 * 1000L
        var triggerTime = Date.from(fullDateTime.atZone(ZoneId.systemDefault()).toInstant()).time
        triggerTime = modifyRepeatTime(triggerTime, repeatInterval)

        alarmManager.setRepeating(
            AlarmManager.RTC,
            triggerTime,
            repeatInterval,
            pendingIntent
        )
    }

    override fun deleteAlarm(todo: Todo) {
        val todoDateTime = LocalDateTime.of(todo.startDate, todo.endTime)
        val todoDate = Date.from(todoDateTime.atZone(ZoneId.systemDefault()).toInstant()).time
        val endTime = todoDate - todo.periodTime.time.toInt() * 60 * 1000

        if (!todo.isRepeated || endTime < System.currentTimeMillis()) {
            val pendingIntent = TodoPushBroadcastReceiver.getPendingIntent(context, todo.todoId, null)
            alarmManager.cancel(pendingIntent)
        }
    }

    override fun deleteAlarm(todo: Todo, todoState: TodoState) {
        val todoDateTime = LocalDateTime.of(todo.startDate, todo.endTime)
        val todoDate = Date.from(todoDateTime.atZone(ZoneId.systemDefault()).toInstant()).time
        val endTime = todoDate - todo.periodTime.time.toInt() * 60 * 1000

        if (todoState == TodoState.SUCCESS || !todo.isRepeated || endTime < System.currentTimeMillis()) {
            val pendingIntent = TodoPushBroadcastReceiver.getPendingIntent(context, todo.todoId, null)
            alarmManager.cancel(pendingIntent)
        }
    }

    override suspend fun deleteAlarms() {
        val currentDate =
            Date(System.currentTimeMillis()).toInstant().atZone(ZoneId.systemDefault())
                .toLocalDate()

        repository.getTodosWithTodayDailyTodos(currentDate).filter {
            it.todayDailyTodo.todoState == TodoState.SUCCESS
        }.forEach {
            deleteAlarm(it.todo, TodoState.SUCCESS)
        }
    }

    private fun modifyRepeatTime(triggerTime: Long, interval: Long): Long {
        var time = triggerTime
        val currTime = System.currentTimeMillis()
        if (time < System.currentTimeMillis()) {
            time = (time / 1000) * 1000
            while (time <= currTime) {
                time += interval
            }
        }
        return time
    }
}

fun Long.getFullTimeString(): String {
    val simpleDateFormatTime = SimpleDateFormat("yyyy MM dd a hh:mm", Locale.KOREA)
    return simpleDateFormatTime.format(this)
}
