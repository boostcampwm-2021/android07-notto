package com.gojol.notto.util

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.gojol.notto.R
import com.gojol.notto.model.database.todo.Todo
import com.gojol.notto.model.datasource.todo.ALARM_EXTRA_TODO
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.gojol.notto.model.datasource.todo.TodoLabelRepository
import com.google.gson.Gson
import java.util.*
import com.google.gson.reflect.TypeToken

const val ACTION_FAIL = "actionFail"
const val ACTION_SUCCESS = "actionSuccess"

@AndroidEntryPoint
class TodoPushBroadcastReceiver : HiltBroadcastReceiver() {

    private lateinit var notificationManager: NotificationManager

    @Inject
    lateinit var repository: TodoLabelRepository

    @Inject
    lateinit var alarmManager: AlarmManager

    private val gson: Gson = Gson()

    companion object {
        const val CHANNEL_ID = "nottoChannel"
        const val GROUP_ID = "com.android.notto.util.TodoPushBroadcastReceiver.group"
        const val SUMMARY_ID = 0
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        notificationManager = context.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager

        val todoData = intent.extras?.getString(ALARM_EXTRA_TODO) ?: return
        val todo = gson.fromJson<Todo>(todoData, object : TypeToken<Todo?>() {}.type)

        repository.deleteAlarm(todo)
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
        // putExtra : 마지막에 설정한 값으로만 전달
        // data : 그 당시에 설정한 값을 반환
        val successIntent = Intent(context, TodoSuccessCheckBroadcastReceiver::class.java).apply {
            data = Uri.parse(gson.toJson(todo))
            action = ACTION_SUCCESS
        }

        val failIntent = Intent(context, TodoSuccessCheckBroadcastReceiver::class.java).apply {
            data = Uri.parse(gson.toJson(todo))
            action = ACTION_FAIL
        }

        val successPendingIntent = PendingIntent.getBroadcast(
            context, SUCCESS_INTENT_ID, successIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        val failPendingIntent = PendingIntent.getBroadcast(
            context, FAIL_INTENT_ID, failIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        val currentTime = Date(System.currentTimeMillis()).getTimeString()

        contentView.setTextViewText(R.id.tv_notification_todo_title, todo.content)
        contentView.setTextViewText(R.id.tv_notification_todo_time, currentTime)
        contentView.setOnClickPendingIntent(R.id.btn_notification_todo_success, successPendingIntent)
        contentView.setOnClickPendingIntent(R.id.btn_notification_todo_fail, failPendingIntent)
        return contentView
    }
}