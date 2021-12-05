package com.gojol.notto.model.data.todo

import androidx.lifecycle.MutableLiveData
import com.gojol.notto.common.Event

data class ClickWrapper(
    val isTodoEditing: MutableLiveData<Boolean>,
    val isBeforeToday: MutableLiveData<Boolean>,
    val isCloseButtonCLicked: MutableLiveData<Event<Unit>>,
    val deleteButtonClick: MutableLiveData<Event<Unit>>,
    val popLabelAddDialog: MutableLiveData<Event<Boolean>>,
    val labelAddClicked: MutableLiveData<Event<Unit>>,
    val repeatTypeClick: MutableLiveData<Event<Boolean>>,
    val repeatStartClick: MutableLiveData<Event<Boolean>>,
    val timeStartClick: MutableLiveData<Event<Boolean>>,
    val timeFinishClick: MutableLiveData<Event<Boolean>>,
    val timeRepeatClick: MutableLiveData<Event<Boolean>>,
    val isDeletionExecuted: MutableLiveData<Event<Unit>>,
    val isSaveButtonEnabled: MutableLiveData<Boolean>,
    val isSaveButtonClicked: MutableLiveData<Pair<Boolean, Boolean>>
) {

    fun onIsCloseButtonClicked() {
        isCloseButtonCLicked.value = Event(Unit)
    }

    fun onDeleteButtonCLick() {
        deleteButtonClick.value = Event(Unit)
    }

    fun onPopLabelAddDialogClick() {
        popLabelAddDialog.value = Event(true)
    }

    fun onRepeatTypeClick() {
        repeatTypeClick.value = Event(true)
    }

    fun onRepeatStartClick() {
        repeatStartClick.value = Event(true)
    }

    fun onTimeStartClick() {
        timeStartClick.value = Event(true)
    }

    fun onTimeFinishClick() {
        timeFinishClick.value = Event(true)
    }

    fun onTimeRepeatClick() {
        timeRepeatClick.value = Event(true)
    }

    companion object {
        fun getClickWrapper(): ClickWrapper {
            return ClickWrapper(
                MutableLiveData(),
                MutableLiveData(),
                MutableLiveData(),
                MutableLiveData(),
                MutableLiveData(),
                MutableLiveData(),
                MutableLiveData(),
                MutableLiveData(),
                MutableLiveData(),
                MutableLiveData(),
                MutableLiveData(),
                MutableLiveData(),
                MutableLiveData(),
                MutableLiveData()
            )
        }
    }
}
