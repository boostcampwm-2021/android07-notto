package com.gojol.notto.util

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.gojol.notto.R
import com.gojol.notto.model.database.todo.Todo
import com.gojol.notto.model.datasource.todo.ALARM_EXTRA_TODO
import com.gojol.notto.model.datasource.todo.TodoAlarmManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.google.gson.Gson
import java.util.*

const val ALARM_BUNDLE_TODO = "alarmBundleTodo"
const val ACTION_FAIL = "actionFail"
const val ACTION_SUCCESS = "actionSuccess"

@AndroidEntryPoint
class TodoPushBroadcastReceiver : HiltBroadcastReceiver() {

    private lateinit var notificationManager: NotificationManager

    @Inject
    lateinit var todoAlarmManager: TodoAlarmManager

    private val gson: Gson = Gson()

    companion object {
        const val CHANNEL_ID = "nottoChannel"
        const val GROUP_ID = "com.android.notto.util.TodoPushBroadcastReceiver.group"
        const val SUMMARY_ID = 0

        @SuppressLint("UnspecifiedImmutableFlag")
        fun getPendingIntent(context: Context, id: Int): PendingIntent {
            val intent = Intent(context, TodoPushBroadcastReceiver::class.java)
            return PendingIntent.getBroadcast(
                context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

        @SuppressLint("UnspecifiedImmutableFlag")
        fun getPendingIntent(context: Context, id: Int, bundle: Bundle): PendingIntent {
            val intent = Intent(context, TodoPushBroadcastReceiver::class.java).apply {
                putExtra(ALARM_BUNDLE_TODO, bundle)
            }
            return PendingIntent.getBroadcast(
                context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        notificationManager = context.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager

        val todoData = intent.getBundleExtra(ALARM_BUNDLE_TODO)?.getSerializable(ALARM_EXTRA_TODO) ?: return
        val todo = todoData as Todo

        todoAlarmManager.deleteAlarm(todo)
        createNotificationChannel()
        deliverNotification(context, todo)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID, // 채널의 아이디
                "notto",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationChannel.description = "notto 푸시 알림 채널" // 채널 정보
            notificationManager.createNotificationChannel(
                notificationChannel
            )
        }
    }

    private fun deliverNotification(context: Context, todo: Todo) {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_notto_launcher_foreground)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setContentTitle(todo.content)
            .setCustomContentView(customContentView(context, todo))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setGroup(GROUP_ID)
            .build()

        val summaryNotification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_notto_launcher_foreground) // 아이콘
            .setGroup(GROUP_ID)
            .setStyle(NotificationCompat.InboxStyle())
            .setGroupSummary(true)
            .build()

        NotificationManagerCompat.from(context).apply {
            notify(todo.todoId, builder)

            notify(SUMMARY_ID, summaryNotification)
        }
    }

    @SuppressLint("RemoteViewLayout", "UnspecifiedImmutableFlag")
    private fun customContentView(context: Context, todo: Todo): RemoteViews {
        val contentView = RemoteViews(context.packageName, R.layout.notification_todo)

        val successPendingIntent = TodoSuccessCheckBroadcastReceiver.getPendingIntent(
            context, SUCCESS_INTENT_ID, Uri.parse(gson.toJson(todo)), ACTION_SUCCESS
        )
        val failPendingIntent = TodoSuccessCheckBroadcastReceiver.getPendingIntent(
            context, FAIL_INTENT_ID, Uri.parse(gson.toJson(todo)), ACTION_FAIL
        )

        val currentTime = Date(System.currentTimeMillis()).getTimeString()

        contentView.setTextViewText(R.id.tv_notification_todo_title, todo.content)
        contentView.setTextViewText(R.id.tv_notification_todo_time, currentTime)
        contentView.setOnClickPendingIntent(R.id.btn_notification_todo_success, successPendingIntent)
        contentView.setOnClickPendingIntent(R.id.btn_notification_todo_fail, failPendingIntent)
        return contentView
    }
}