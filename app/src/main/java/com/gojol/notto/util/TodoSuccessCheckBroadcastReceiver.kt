package com.gojol.notto.util

import android.content.Context
import android.content.Intent
import com.gojol.notto.model.database.todo.Todo
import com.gojol.notto.model.datasource.todo.TodoLabelRepository
import javax.inject.Inject

class TodoSuccessCheckBroadcastReceiver : HiltBroadcastReceiver() {

    @Inject
    lateinit var repository: TodoLabelRepository

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if(intent.getStringExtra(ACTION_STATE) == ACTION_SUCCESS) {
            intent.getSerializableExtra(ACTION_BUNDLE)?.let {
                val todo = it as Todo
                // TODO : todo에서 dailyTodo 찾아서 copy하고 success 체크하고 update하기
                //  repository.updateDailyTodo(todo)
                //  coroutine이든 work manager든..
            }
        } else if(intent.getStringExtra(ACTION_STATE) == ACTION_FAIL) {
            intent.getSerializableExtra(ACTION_BUNDLE)?.let {
                val todo = it as Todo
                // TODO : todo에서 dailyTodo 찾아서 copy하고 fail 체크하고 update하기
                //  repository.updateDailyTodo(todo)
                //  coroutine이든 work manager든..
            }
        }
    }
}