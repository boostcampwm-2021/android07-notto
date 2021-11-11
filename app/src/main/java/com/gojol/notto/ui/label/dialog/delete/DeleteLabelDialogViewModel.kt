package com.gojol.notto.ui.label.dialog.delete

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gojol.notto.model.database.label.Label
import com.gojol.notto.model.datasource.todo.TodoLabelRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeleteLabelDialogViewModel @Inject constructor(private val repository: TodoLabelRepository) :
    ViewModel() {

    private val _label = MutableLiveData<Label>()
    val label: LiveData<Label> = _label

    private val _close = MutableLiveData(false)
    val close: LiveData<Boolean> = _close

    fun clickCancel() {
        _close.value = true
    }

    fun clickOkay() {
        _close.value = true
        label.value ?: return

        viewModelScope.launch {
            repository.deleteLabel(label.value!!)
        }
    }

    fun setLabel(label: Label) {
        _label.value = label
    }
}
