package com.gojol.notto.ui.todo.dialog.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.gojol.notto.ui.DialogViewModel
import java.time.LocalDate

class RepeatTimeDialogViewModel : DialogViewModel() {

    private val _repeatTime = MutableLiveData<LocalDate>()
    val repeatTime: LiveData<LocalDate> = _repeatTime

    private val _repeatTimeCallback = MutableLiveData<(LocalDate) -> Unit>()
    val repeatTimeCallback: LiveData<(LocalDate) -> Unit> = _repeatTimeCallback

    fun setRepeatTime(time: LocalDate) {
        _repeatTime.value = time
    }

    fun setRepeatTimeCallback(callback: (LocalDate) -> Unit) {
        _repeatTimeCallback.value = callback
    }
}