package com.gojol.notto.model.data.todo

import androidx.lifecycle.MutableLiveData
import com.gojol.notto.common.Event

data class ClickWrapper(
    val isTodoEditing: MutableLiveData<Boolean>,
    val isCloseButtonCLicked: MutableLiveData<Event<Unit>>,
    val popLabelAddDialog: MutableLiveData<Boolean>,
    val labelAddClicked: MutableLiveData<Event<Unit>>,
    val repeatTypeClick: MutableLiveData<Event<Boolean>>,
    val repeatStartClick: MutableLiveData<Event<Boolean>>,
    val timeStartClick: MutableLiveData<Event<Boolean>>,
    val timeFinishClick: MutableLiveData<Event<Boolean>>,
    val timeRepeatClick: MutableLiveData<Event<Boolean>>,
    val deleteButtonClick: MutableLiveData<Event<Unit>>,
    val isDeletionExecuted: MutableLiveData<Event<Unit>>,
    val isSaveButtonEnabled: MutableLiveData<Boolean>,
    val isSaveButtonClicked: MutableLiveData<Pair<Boolean, Boolean>>
)
