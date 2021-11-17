package com.gojol.notto.ui.todo.dialog.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gojol.notto.common.TimeRepeatType
import com.gojol.notto.common.TodoDeleteType
import com.gojol.notto.model.data.RepeatType
import com.gojol.notto.model.datasource.todo.TodoLabelRepository
import com.gojol.notto.util.TouchEvent
import com.gojol.notto.util.getDate
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.*
import javax.inject.Inject

@HiltViewModel
class TodoBaseDialogViewModel @Inject constructor(
    private val repository: TodoLabelRepository
) : ViewModel() {

    private val _isConfirmClicked = MutableLiveData<TouchEvent<Boolean>>()
    val isConfirmClicked: LiveData<TouchEvent<Boolean>> = _isConfirmClicked

    private val _isDismissClicked = MutableLiveData<TouchEvent<Boolean>>()
    val isDismissClicked: LiveData<TouchEvent<Boolean>> = _isDismissClicked

    private val _repeatType = MutableLiveData<RepeatType>()
    val repeatType: LiveData<RepeatType> = _repeatType

    private val _repeatTime = MutableLiveData<Date>()
    val repeatTime: LiveData<Date> = _repeatTime

    private val _timeStart = MutableLiveData<String>()
    val timeStart: LiveData<String> = _timeStart

    private val _timeFinish = MutableLiveData<String>()
    val timeFinish: LiveData<String> = _timeFinish

    private val _timeRepeat = MutableLiveData<TimeRepeatType>()
    val timeRepeat: LiveData<TimeRepeatType> = _timeRepeat

    private val _editingTodoId = MutableLiveData<Int>()
    val editingTodoId: LiveData<Int> = _editingTodoId

    private val _todoDeleteType = MutableLiveData<TodoDeleteType>()
    val todoDeleteType: LiveData<TodoDeleteType> = _todoDeleteType

    fun onConfirmClick() {
        _isConfirmClicked.value = TouchEvent(true)
    }

    fun onDismissClick() {
        _isDismissClicked.value = TouchEvent(true)
    }

    fun setRepeatType(type: RepeatType) {
        _repeatType.value = type
    }

    fun setRepeatTime(time: Date) {
        _repeatTime.value = time
    }

    fun setRepeatTime(time: String) {
        _repeatTime.value = time.getDate()
    }

    fun setTimeStart(date: String) {
        _timeStart.value = date
    }

    fun setTimeFinish(date: String) {
        _timeFinish.value = date
    }

    fun setTimeRepeat(time: TimeRepeatType) {
        _timeRepeat.value = time
    }

    fun setEditingTodoId(todoId: Int) {
        _editingTodoId.value = todoId
    }

    fun setTodoDeleteType(type: TodoDeleteType) {
        _todoDeleteType.value = type
    }

    fun deleteTodo() {
        // TODO: 어떤 타입인지 확인하여 delete 처리
    }
}
