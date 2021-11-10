package com.gojol.notto.ui.label

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
class EditLabelViewModel @Inject constructor(private val repository: TodoLabelRepository) :
    ViewModel() {

    private val _items = MutableLiveData<List<Label>>().apply { value = emptyList() }
    val items: LiveData<List<Label>> = _items

    private val _dialogToShow = MutableLiveData<Label?>()
    val dialogToShow: LiveData<Label?> = _dialogToShow

    fun loadLabels() {
        viewModelScope.launch {
            _items.value = repository.getAllLabel()
        }
    }

    fun deleteLabel(label: Label) {
        viewModelScope.launch {
            repository.deleteLabel(label)

            _items.value = repository.getAllLabel()
        }
    }

    fun onClickCreateButton() {
        _dialogToShow.value = null
    }
}
