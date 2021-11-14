package com.gojol.notto.ui.todo.dialog.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gojol.notto.common.TimeRepeatType
import com.gojol.notto.model.data.RepeatType
import com.gojol.notto.util.TouchEvent
import com.gojol.notto.util.getDate
import com.gojol.notto.util.getDateString
import java.util.*

class TodoBaseDialogViewModel : ViewModel() {

    private val _isConfirmClicked = MutableLiveData<TouchEvent<Boolean>>()
    val isConfirmClicked: LiveData<TouchEvent<Boolean>> = _isConfirmClicked

    private val _isDismissClicked = MutableLiveData<TouchEvent<Boolean>>()
    val isDismissClicked: LiveData<TouchEvent<Boolean>> = _isDismissClicked

    private val _repeatType = MutableLiveData<RepeatType>()
    val repeatType: LiveData<RepeatType> = _repeatType

    private val _repeatTime = MutableLiveData<String>()
    val repeatTime: LiveData<String> = _repeatTime

    private val _timeStart = MutableLiveData<String>()
    val timeStart: LiveData<String> = _timeStart

    private val _timeFinish = MutableLiveData<String>()
    val timeFinish: LiveData<String> = _timeFinish

    private val _timeRepeat = MutableLiveData<TimeRepeatType>()
    val timeRepeat: LiveData<TimeRepeatType> = _timeRepeat

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
        _repeatTime.value = time.getDateString()
    }

    fun setRepeatTime(time: String) {
        _repeatTime.value = time
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

    fun repeatTimeFormatter(): Date? {
        return _repeatTime.value?.getDate()
    }

    fun timeStartFormatter(): List<String>? {
        return _timeStart.value?.split(":")
    }

    fun timeFinishFormatter(): List<String>? {
        return _timeFinish.value?.split(":")
    }
}