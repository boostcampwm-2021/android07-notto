package com.gojol.notto.ui.todo.dialog.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.time.LocalTime

class TimeStartDialogViewModel : DialogViewModel() {

    private val _timeStart = MutableLiveData<LocalTime>()
    val timeStart: LiveData<LocalTime> = _timeStart

    private val _setTimeStartCallback = MutableLiveData<(LocalTime) -> Unit>()
    val setTimeStartCallback: LiveData<(LocalTime) -> Unit> = _setTimeStartCallback


    fun setTimeStart(date: LocalTime) {
        _timeStart.value = date
    }

    fun setTimeStartCallback(callback: (LocalTime) -> Unit) {
        _setTimeStartCallback.value = callback
    }
}