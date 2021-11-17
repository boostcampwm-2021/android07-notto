package com.gojol.notto.util

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.gojol.notto.R
import com.gojol.notto.common.TodoState
import com.gojol.notto.model.database.todo.Todo
import com.gojol.notto.model.datasource.todo.ALARM_EXTRA_TODO
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.gojol.notto.model.datasource.todo.ALARM_BUNDLE_TODO
import com.gojol.notto.model.datasource.todo.TodoLabelRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.TimeUnit

const val ACTION_TODO_DATA = "actionTodoData"
const val ACTION_BUNDLE = "actionBundle"

const val ACTION_STATE = "actionState"
const val ACTION_FAIL = "actionFail"
const val ACTION_SUCCESS = "actionSuccess"

// TODO: WorkManager 체크해보기
@AndroidEntryPoint
class TodoPushBroadcastReceiver : HiltBroadcastReceiver() {

    lateinit var notificationManager: NotificationManager

    @Inject
    lateinit var repository: TodoLabelRepository
    @Inject
    lateinit var alarmManager: AlarmManager

    var todo: Todo? = null

    companion object {
        const val CHANNEL_ID = "nottoChannel"
        const val GROUP_ID = "com.android.notto.util.TodoPushBroadcastReceiver.group"
        const val SUMMARY_ID = 0
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        notificationManager = context.getSystemService(
            Context.NOTIFICATION_SERVICE) as NotificationManager

        val bundle = intent.getBundleExtra(ALARM_BUNDLE_TODO)
        bundle?.getSerializable(ALARM_EXTRA_TODO)?.let { serialize ->
            todo = (serialize as Todo)
            todo?.let {
                removeAlarm(context, intent)
            }

            createNotificationChannel()
            deliverNotification(context)
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun removeAlarm(context: Context, intent: Intent) {
        todo?.let {
            val todoTime = it.endTime.getTime() ?: return
            val endTime = todoTime.time - it.periodTime.time.toInt() * 60 * 1000
            if (!it.isRepeated || endTime < System.currentTimeMillis()) {
                val pendingIntent = PendingIntent.getBroadcast(
                    context, it.todoId, intent, PendingIntent.FLAG_UPDATE_CURRENT
                )
                alarmManager.cancel(pendingIntent)
            }
        }
    }

    // TODO : Priority 를 HIGH로 하면 상태바가 아닌 화면에 등장하는 알림이 사라지기 전까지는 전부 펼쳐졌다가
    //  해당 알림이 끝나면 summary로 합쳐진다. 뭐가 좋을까? (잠깐동안 펼쳐지는 것도 나빠보이지는 않는다)
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

    private fun deliverNotification(context: Context) {
        val todo = todo ?: return

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setContentTitle(todo.content)
            .setCustomContentView(customContentView(context))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setGroup(GROUP_ID)
            .build()

        val summaryNotification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // 아이콘
            .setGroup(GROUP_ID)
            .setStyle(NotificationCompat.InboxStyle())
            .setGroupSummary(true)
            .build()

        NotificationManagerCompat.from(context).apply {
            notify(todo.todoId, builder)

            notify(SUMMARY_ID, summaryNotification)
        }
    }

    // TODO : 여기 다시 갈아엎을 것 -> 공통 코드 줄이기, Bundle 빼기, bundleOf extra?, Intent Action 다르게 지정하기
    @SuppressLint("RemoteViewLayout", "UnspecifiedImmutableFlag")
    private fun customContentView(context: Context): RemoteViews {
        val contentView = RemoteViews(context.packageName, R.layout.notification_todo)

        todo?.let {
            val bundle = Bundle()
            bundle.putSerializable(ACTION_TODO_DATA, it)

            val successIntent = Intent(context, TodoSuccessCheckBroadcastReceiver::class.java)
            successIntent.putExtra(ACTION_BUNDLE, bundle)
            successIntent.putExtra(ACTION_STATE, ACTION_SUCCESS)
            val successPendingIntent = PendingIntent.getBroadcast(
                context, SUCCESS_INTENT_ID, successIntent, PendingIntent.FLAG_UPDATE_CURRENT
            )

            val failIntent = Intent(context, TodoSuccessCheckBroadcastReceiver::class.java)
            failIntent.putExtra(ACTION_BUNDLE, bundle)
            failIntent.putExtra(ACTION_STATE, ACTION_FAIL)
            val failPendingIntent = PendingIntent.getBroadcast(
                context, FAIL_INTENT_ID, failIntent, PendingIntent.FLAG_UPDATE_CURRENT
            )

            val currentTime = Date(System.currentTimeMillis()).getTimeString()

            contentView.setTextViewText(R.id.tv_notification_todo_title, it.content)
            contentView.setTextViewText(R.id.tv_notification_todo_time, currentTime)
            contentView.setOnClickPendingIntent(R.id.btn_notification_todo_success, successPendingIntent)
            contentView.setOnClickPendingIntent(R.id.btn_notification_todo_fail, failPendingIntent)
        }

        return contentView
    }
}