package com.gojol.notto.ui.label.dialog.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gojol.notto.common.EditType
import com.gojol.notto.model.database.label.Label
import com.gojol.notto.model.datasource.todo.TodoLabelRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditLabelDialogViewModel @Inject constructor(private val repository: TodoLabelRepository) :
    ViewModel() {

    private val _type = MutableLiveData<EditType>()
    val type: LiveData<EditType> = _type

    private val _label = MutableLiveData<Label>()
    val label: LiveData<Label> = _label

    val title = MutableLiveData<String>()
    val name = MutableLiveData<String>()

    private val _close = MutableLiveData(false)
    val close: LiveData<Boolean> = _close

    fun clickCancel() {
        _close.value = true
    }

    fun clickOkay() {
        _close.value = true

        name.value ?: return
        // TODO: 에러 메시지 출력

        when (type.value) {
            EditType.CREATE -> createLabel(Label(1000, name.value!!))
            EditType.UPDATE -> {
                label.value ?: return
                updateLabel(label.value!!.copy(name = name.value!!))
            }
        }
    }

    private fun createLabel(label: Label) {
        viewModelScope.launch {
            repository.insertLabel(label)
        }
    }

    private fun updateLabel(label: Label) {
        viewModelScope.launch {
            repository.updateLabel(label)
        }
    }

    fun setEditType(label: Label?) {
        if (label == null) {
            _type.value = EditType.CREATE
            title.value = EditType.CREATE.text
        } else {
            _type.value = EditType.UPDATE
            title.value = EditType.UPDATE.text
            _label.value = label!!
            name.value = label.name
        }
    }
}
