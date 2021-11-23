package com.gojol.notto.ui.todo.dialog.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.gojol.notto.common.TodoDeleteType
import java.time.LocalTime

class DeletionDialogViewModel : DialogViewModel() {

    private val _todoDeleteType = MutableLiveData<TodoDeleteType>()
    val todoDeleteType: LiveData<TodoDeleteType> = _todoDeleteType

    private val _deletionCallback = MutableLiveData<(TodoDeleteType) -> Unit>()
    val deletionCallback: LiveData<(TodoDeleteType) -> Unit> = _deletionCallback

    fun setTodoDeleteType(type: TodoDeleteType) {
        _todoDeleteType.value = type
    }

    fun setDeletionCallback(callback: (TodoDeleteType) -> Unit) {
        _deletionCallback.value = callback
    }
}