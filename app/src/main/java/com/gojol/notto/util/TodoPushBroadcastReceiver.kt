package com.gojol.notto.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.widget.Toast
import androidx.annotation.CallSuper
import androidx.core.app.NotificationCompat
import com.gojol.notto.R
import com.gojol.notto.model.database.todo.Todo
import com.gojol.notto.model.datasource.todo.ALARM_EXTRA_TODO
import com.gojol.notto.model.datasource.todo.TodoLabelRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import android.os.Bundle
import com.gojol.notto.model.datasource.todo.ALARM_BUNDLE_TODO


abstract class HiltBroadcastReceiver : BroadcastReceiver() {
    @CallSuper
    override fun onReceive(context: Context, intent: Intent) { }
}

@AndroidEntryPoint
class TodoPushBroadcastReceiver : HiltBroadcastReceiver() {

    lateinit var notificationManager: NotificationManager

    @Inject
    lateinit var repository: TodoLabelRepository

    var todo: Todo? = null

    companion object {
        const val CHANNEL_ID = "nottoChannel"
        const val NOTIFICATION_ID = 1
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        notificationManager = context.getSystemService(
            Context.NOTIFICATION_SERVICE) as NotificationManager

        val bundle = intent.getBundleExtra(ALARM_BUNDLE_TODO)
        println(bundle?.getSerializable(ALARM_EXTRA_TODO))
        bundle?.getSerializable(ALARM_EXTRA_TODO)?.let {

            todo = it as Todo
            createNotificationChannel()
            deliverNotification(context)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID, // 채널의 아이디
                "notto",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.description = "notto 푸시 알림 채널" // 채널 정보
            notificationManager.createNotificationChannel(
                notificationChannel
            )
        }
    }

    private fun deliverNotification(context: Context) {
//        val contentIntent = Intent(context, MainActivity::class.java)
//        val contentPendingIntent = PendingIntent.getActivity(
//            context,
//            NOTIFICATION_ID, // requestCode
//            contentIntent, // 알림 클릭 시 이동할 인텐트
//            PendingIntent.FLAG_UPDATE_CURRENT
//            /*
//            1. FLAG_UPDATE_CURRENT : 현재 PendingIntent를 유지하고, 대신 인텐트의 extra data는 새로 전달된 Intent로 교체
//            2. FLAG_CANCEL_CURRENT : 현재 인텐트가 이미 등록되어있다면 삭제, 다시 등록
//            3. FLAG_NO_CREATE : 이미 등록된 인텐트가 있다면, null
//            4. FLAG_ONE_SHOT : 한번 사용되면, 그 다음에 다시 사용하지 않음
//             */
//        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // 아이콘
            .setContentTitle(todo?.content) // 제목
            .setContentText(todo?.content) // 내용
            //   .setContentIntent(contentPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)

        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }
}