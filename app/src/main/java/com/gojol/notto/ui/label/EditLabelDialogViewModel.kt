package com.gojol.notto.ui.label

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gojol.notto.model.database.label.Label
import com.gojol.notto.model.datasource.todo.TodoLabelRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditLabelDialogViewModel @Inject constructor(private val repository: TodoLabelRepository) : ViewModel() {

    // TODO: 데이터 변경 내용 적용
    fun createLabel(label: Label, callback: () -> Unit) {
        viewModelScope.launch {
            repository.insertLabel(label)
            callback()
        }
    }

    fun updateLabel(label: Label, callback: () -> Unit) {
        viewModelScope.launch {
            repository.updateLabel(label)
            callback()
        }
    }
}
