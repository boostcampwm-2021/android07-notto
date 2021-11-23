package com.gojol.notto.ui.todo.dialog.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.gojol.notto.common.TimeRepeatType
import java.time.LocalTime

class AlarmPeriodDialogViewModel : DialogViewModel() {

    private val _timeRepeat = MutableLiveData<TimeRepeatType>()
    val timeRepeat: LiveData<TimeRepeatType> = _timeRepeat

    private val _alarmPeriodCallback = MutableLiveData<(TimeRepeatType) -> Unit>()
    val alarmPeriodCallback: LiveData<(TimeRepeatType) -> Unit> = _alarmPeriodCallback

    fun setTimeRepeat(time: TimeRepeatType) {
        _timeRepeat.value = time
    }

    fun setAlarmPeriodCallback(callback: (TimeRepeatType) -> Unit) {
        _alarmPeriodCallback.value = callback
    }
}