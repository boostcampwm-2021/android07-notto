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
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

class TodoAlarmManagerImpl @Inject constructor(
    private val context: Context,
    private val alarmManager: AlarmManager,
    private val repository: TodoLabelRepository
) : TodoAlarmManager {

    // 투두 생성 시 알림 등록
    override fun addAlarm(todo: Todo) {
        if (!todo.hasAlarm) return

        // 어차피 시작일 기준으로 알림을 등록하는 것이므로 따로 거를 필요가 없다. Reboot나 앱 실행 시에만 체크하면 됨

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

    // Reboot 또는 앱 실행 시 데일리 투두에 대하여 혹시 알림이 꺼져있을 경우를 대비해 재등록.
    override fun addAlarm(todo: Todo, todoState: TodoState) {
        if (!todo.hasAlarm) return
        if (todoState == TodoState.SUCCESS) return
        if (todo.isFinished) return

        // 오늘 기준으로 이미 지나가버린 알림이라면 등록할 필요 없다. (WorkManager가 내일부터 등록할것임)
        val todayAlarmDateTime = LocalDateTime.of(LocalDate.now(), todo.endTime)
        val todayAlarmDate = Date.from(todayAlarmDateTime.atZone(ZoneId.systemDefault()).toInstant()).time
        if (todayAlarmDate < System.currentTimeMillis()) return

        val pendingIntent = TodoPushBroadcastReceiver.getPendingIntent(
            context,
            todo.todoId,
            bundleOf(
                ALARM_EXTRA_TODO to (todo as Serializable)
            )
        )

        val fullDateTime = LocalDateTime.of(LocalDate.now(), todo.startTime)
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

    override suspend fun updateAlarm(todo: Todo) {
        val currentDate =
            Date(System.currentTimeMillis()).toInstant().atZone(ZoneId.systemDefault())
                .toLocalDate()

        repository.getTodosWithTodayDailyTodos(currentDate).filter {
            todo.todoId == it.todo.todoId
        }.forEach {
            if (it.todayDailyTodo.todoState == TodoState.SUCCESS) {
                deleteAlarm(it.todo, TodoState.SUCCESS)
            } else {
                addAlarm(it.todo)
            }
        }
    }

    override suspend fun updateAlarms() {
        val currentDate =
            Date(System.currentTimeMillis()).toInstant().atZone(ZoneId.systemDefault())
                .toLocalDate()

        repository.getTodosWithTodayDailyTodos(currentDate).forEach {
            if (it.todayDailyTodo.todoState == TodoState.SUCCESS) {
                deleteAlarm(it.todo, TodoState.SUCCESS)
            } else {
                addAlarm(it.todo)
            }
        }
    }

    override fun deleteAlarm(todo: Todo) {
        val todayAlarmDateTime = LocalDateTime.of(LocalDate.now(), todo.endTime)
        val todayAlarmDate = Date.from(todayAlarmDateTime.atZone(ZoneId.systemDefault()).toInstant()).time
        val endTime = todayAlarmDate - todo.periodTime.time.toInt() * 60 * 1000

        if (!todo.isRepeated || todo.isFinished || endTime < System.currentTimeMillis()) {
            val pendingIntent = TodoPushBroadcastReceiver.getPendingIntent(context, todo.todoId, null)
            alarmManager.cancel(pendingIntent)
        }
    }

    override fun deleteAlarm(todo: Todo, todoState: TodoState) {
        val todayAlarmDateTime = LocalDateTime.of(LocalDate.now(), todo.endTime)
        val todayAlarmDate = Date.from(todayAlarmDateTime.atZone(ZoneId.systemDefault()).toInstant()).time
        val endTime = todayAlarmDate - todo.periodTime.time.toInt() * 60 * 1000

        if (todoState == TodoState.SUCCESS || !todo.isRepeated ||
            todo.isFinished || endTime < System.currentTimeMillis()
        ) {
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
