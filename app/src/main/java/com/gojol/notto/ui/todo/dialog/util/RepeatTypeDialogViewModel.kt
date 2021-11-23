package com.gojol.notto.ui.todo.dialog.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.gojol.notto.common.RepeatType
import com.gojol.notto.ui.DialogViewModel

class RepeatTypeDialogViewModel : DialogViewModel() {

    private val _repeatType = MutableLiveData<RepeatType>()
    val repeatType: LiveData<RepeatType> = _repeatType

    private val _repeatTypeCallback = MutableLiveData<(RepeatType) -> Unit>()
    val repeatTypeCallback: LiveData<(RepeatType) -> Unit> = _repeatTypeCallback

    fun setRepeatType(type: RepeatType) {
        _repeatType.value = type
    }

    fun setRepeatTypeCallback(callback: (RepeatType) -> Unit) {
        _repeatTypeCallback.value = callback
    }
}