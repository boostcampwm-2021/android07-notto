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

const val ALARM_EXTRA_TODO = "alarmExtraTodo"
const val ALARM_BUNDLE_TODO = "alarmBundleTodo"

class TodoAlarmManagerImpl @Inject constructor(
    private val context: Context,
    private val alarmManager: AlarmManager
) : TodoAlarmManager {

    @SuppressLint("UnspecifiedImmutableFlag")
    override fun addAlarm(todo: Todo) {
        if (!todo.hasAlarm) return

        val intent = Intent(context, TodoPushBroadcastReceiver::class.java)
        val bundle = Bundle()
        bundle.putSerializable(ALARM_EXTRA_TODO, todo)
        intent.putExtra(ALARM_BUNDLE_TODO, bundle)

        val pendingIntent = PendingIntent.getBroadcast(
            context, todo.todoId, intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        // TODO : Extension에 비슷한 기능을 하는 중복된 코드가 많은 것 같다. pattern을 넣어서 선언할 수 있도록 수정해보자.
        //  또한 패턴을 const 등으로 정리하고 저장해서 가져다 쓰는 식으로 하면 좋을 것 같다.
        val fullDate = ("${todo.startDate} ${todo.startTime}").getDate("yyyyMMdd a hh:mm")
        fullDate?.let { it ->
            val repeatInterval: Long = todo.periodTime.time.toInt() * 60 * 1000L
            val triggerTime = it.time

            alarmManager.setRepeating(
                AlarmManager.RTC,
                triggerTime, repeatInterval,
                pendingIntent
            )
            println("register : " + System.currentTimeMillis().getTotalTimeString())
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    override fun deleteAlarm(todo: Todo) {
        val todoTime = ("${todo.startDate} ${todo.endTime}").getDate("yyyyMMdd a hh:mm") ?: return
        val endTime = todoTime.time - todo.periodTime.time.toInt() * 60 * 1000
        println("${todo.startDate} ${todo.endTime}")
        println(endTime.getTotalTimeString())
        println(System.currentTimeMillis().getTotalTimeString())
        if (!todo.isRepeated || endTime < System.currentTimeMillis()) {
            val intent = Intent(context, TodoPushBroadcastReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context, todo.todoId, intent, PendingIntent.FLAG_UPDATE_CURRENT
            )
            alarmManager.cancel(pendingIntent)
        }
    }
}