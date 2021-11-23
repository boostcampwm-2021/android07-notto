package com.gojol.notto.ui.todo.dialog.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gojol.notto.common.Event

abstract class DialogViewModel : ViewModel() {

    private val _isConfirmClicked = MutableLiveData<Event<Boolean>>()
    val isConfirmClicked: LiveData<Event<Boolean>> = _isConfirmClicked

    private val _isDismissClicked = MutableLiveData<Event<Boolean>>()
    val isDismissClicked: LiveData<Event<Boolean>> = _isDismissClicked

    fun onConfirmClick() {
        _isConfirmClicked.value = Event(true)
    }

    fun onDismissClick() {
        _isDismissClicked.value = Event(true)
    }
}
