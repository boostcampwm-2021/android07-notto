package com.gojol.notto.model.datasource.todo

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import com.gojol.notto.model.database.todo.Todo
import com.gojol.notto.util.TodoPushBroadcastReceiver
import com.gojol.notto.util.get24Hour
import com.gojol.notto.util.get24Time
import com.gojol.notto.util.getDate
import com.gojol.notto.util.getTime
import com.gojol.notto.util.getTimeString
import com.gojol.notto.util.getTotalTimeString
import dagger.hilt.android.qualifiers.ActivityContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.os.Bundle

const val ALARM_EXTRA_TODO = "alarmExtraTodo"
const val ALARM_BUNDLE_TODO = "alarmBundleTodo"

class TodoAlarmManagerImpl @Inject constructor(
    private val context: Context,
    private val alarmManager: AlarmManager
) : TodoAlarmManager {

    @SuppressLint("UnspecifiedImmutableFlag")
    override fun addAlarm(todo: Todo) {
        val intent = Intent(context, TodoPushBroadcastReceiver::class.java)
        println("id: " + todo.todoId)

        val bundle = Bundle()
        bundle.putSerializable(ALARM_EXTRA_TODO, todo)
        intent.putExtra(ALARM_BUNDLE_TODO, bundle)

        val pendingIntent = PendingIntent.getBroadcast(
            context, todo.todoId, intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val fullDate = ("${todo.startDate} ${todo.startTime}").getDate("yyyyMMdd a hh:mm")
        fullDate?.let { it ->
            println(it.getTotalTimeString())

            //val repeatInterval: Long = todo.periodTime.time.toInt()  * 60 * 1000L
            val repeatInterval: Long = 1 * 10 * 1000L
            val triggerTime = it.time

            alarmManager.setRepeating(
                AlarmManager.RTC,
                triggerTime, repeatInterval,
                pendingIntent
            )
        }
    }

    override fun updateAlarm(todo: Todo) {

    }

    override fun deleteAlarm(todo: Todo) {

    }
}