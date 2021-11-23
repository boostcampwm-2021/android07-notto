package com.gojol.notto.ui.todo.dialog.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gojol.notto.common.Event
import com.gojol.notto.common.TimeRepeatType
import com.gojol.notto.common.TodoDeleteType
import com.gojol.notto.common.RepeatType
import java.time.LocalDate
import java.time.LocalTime

class TodoBaseDialogViewModel : ViewModel() {

    private val _isConfirmClicked = MutableLiveData<Event<Boolean>>()
    val isConfirmClicked: LiveData<Event<Boolean>> = _isConfirmClicked

    private val _isDismissClicked = MutableLiveData<Event<Boolean>>()
    val isDismissClicked: LiveData<Event<Boolean>> = _isDismissClicked

    private val _repeatType = MutableLiveData<RepeatType>()
    val repeatType: LiveData<RepeatType> = _repeatType

    private val _repeatTime = MutableLiveData<LocalDate>()
    val repeatTime: LiveData<LocalDate> = _repeatTime

    private val _timeStart = MutableLiveData<LocalTime>()
    val timeStart: LiveData<LocalTime> = _timeStart

    private val _timeFinish = MutableLiveData<LocalTime>()
    val timeFinish: LiveData<LocalTime> = _timeFinish

    private val _timeRepeat = MutableLiveData<TimeRepeatType>()
    val timeRepeat: LiveData<TimeRepeatType> = _timeRepeat

    private val _todoDeleteType = MutableLiveData<TodoDeleteType>()
    val todoDeleteType: LiveData<TodoDeleteType> = _todoDeleteType

    fun onConfirmClick() {
        _isConfirmClicked.value = Event(true)
    }

    fun onDismissClick() {
        _isDismissClicked.value = Event(true)
    }

    fun setRepeatType(type: RepeatType) {
        _repeatType.value = type
    }

    fun setRepeatTime(time: LocalDate) {
        _repeatTime.value = time
    }

    fun setTimeStart(date: LocalTime) {
        _timeStart.value = date
    }

    fun setTimeFinish(date: LocalTime) {
        _timeFinish.value = date
    }

    fun setTimeRepeat(time: TimeRepeatType) {
        _timeRepeat.value = time
    }

    fun setTodoDeleteType(type: TodoDeleteType) {
        _todoDeleteType.value = type
    }
}
