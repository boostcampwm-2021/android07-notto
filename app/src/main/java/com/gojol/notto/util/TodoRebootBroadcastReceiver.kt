package com.gojol.notto.util

import android.content.Context
import android.content.Intent
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.gojol.notto.common.DAY_PUSH_NOTIFICATION_KEY
import com.gojol.notto.model.SharedPrefManager
import com.gojol.notto.model.datasource.todo.TodoLabelRepository
import com.gojol.notto.ui.option.DayAlarmManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TodoRebootBroadcastReceiver : HiltBroadcastReceiver() {

    @Inject
    lateinit var repository: TodoLabelRepository

    @Inject
    lateinit var dayAlarmManager: DayAlarmManager

    @Inject
    lateinit var sharedPrefManager: SharedPrefManager

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        if (intent.action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            // DailyNotify
            val isPushChecked = sharedPrefManager.getBoolean(DAY_PUSH_NOTIFICATION_KEY)
            if (isPushChecked) dayAlarmManager.setAlarm()

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

            // 디바이스가 재부팅될 때는 즉시 알람을 다시 생성해야된다. 따라서 24시간마다 바뀌는 알람과는 관계없이 진행해야할 것
            val workRequest =
                OneTimeWorkRequestBuilder<AddTodoAlarmWorker>()
                    .build()

            val workManager = WorkManager.getInstance(context)
            workManager.enqueue(workRequest)
        }
    }
}
