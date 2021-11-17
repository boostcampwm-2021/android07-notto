package com.gojol.notto.util

import android.content.Context
import android.content.Intent
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import com.gojol.notto.model.datasource.todo.TodoLabelRepository
import javax.inject.Inject
import androidx.work.PeriodicWorkRequestBuilder
import java.util.concurrent.TimeUnit


class TodoRebootBroadcastReceiver : HiltBroadcastReceiver() {

    @Inject
    lateinit var repository: TodoLabelRepository

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        if (intent.action.equals(Intent.ACTION_BOOT_COMPLETED)) {

            // TODO : 아래 workmanager를 이용한 부분을 주석처리하고 이 부분을 주석 해제한 후
            //  재부팅했을 때 이 로직이 실행되는 분은 말씀해주세요.
//            CoroutineScope(Dispatchers.IO).launch {
//                val currentDate = Date(System.currentTimeMillis()).getDateString()
//                repository.getTodosWithTodayDailyTodos(currentDate).forEach {
//                    val todo = it.todo
//                    val dailyTodo = it.todayDailyTodo
//                    if (todo.hasAlarm && dailyTodo != null && dailyTodo.todoState != TodoState.SUCCESS) {
//                        println(todo)
//                        repository.addAlarm(todo)
//                    }
//                    delay(1000)
//                }
//            }

            // TODO : WorkManager는 작업을 WorkManager DB에 저장하고 꺼내쓰기 때문에
            //  재부팅을 해도 해당 작업이 유지가 된다. 그럼 이 로직을 어느 시점에 작성하면 될까? 앱이 실행될 때마다?
            //  일단 분리만 하면 이 브로드캐스트 리시버는 필요가 없어진다.
            val workRequest =
                PeriodicWorkRequestBuilder<ResetAlarmWorker>(24, TimeUnit.HOURS)
                    .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                    .addTag(REPEAT_ALARM_WORK)
                    .build()

            val workManager = WorkManager.getInstance(context)
            workManager.enqueueUniquePeriodicWork(REPEAT_ALARM_WORK, ExistingPeriodicWorkPolicy.KEEP, workRequest)
        }
    }
}