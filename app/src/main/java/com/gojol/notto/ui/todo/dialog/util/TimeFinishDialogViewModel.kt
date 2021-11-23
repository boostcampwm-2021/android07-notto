package com.gojol.notto.ui.todo.dialog.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.time.LocalTime

class TimeFinishDialogViewModel : DialogViewModel() {

    private val _timeFinish = MutableLiveData<LocalTime>()
    val timeFinish: LiveData<LocalTime> = _timeFinish

    private val _setTimeFinishCallback = MutableLiveData<(LocalTime) -> Unit>()
    val setTimeFinishCallback: LiveData<(LocalTime) -> Unit> = _setTimeFinishCallback

    fun setTimeFinish(date: LocalTime) {
        _timeFinish.value = date
    }

    fun setTimeFinishCallback(callback: (LocalTime) -> Unit) {
        _setTimeFinishCallback.value = callback
    }
}