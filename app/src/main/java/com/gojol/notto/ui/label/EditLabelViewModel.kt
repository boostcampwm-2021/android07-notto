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

    private val _updatedItems = MutableLiveData<List<Label>>().apply { value = emptyList() }

    private val _dialogToShow = MutableLiveData<Label?>()
    val dialogToShow: LiveData<Label?> = _dialogToShow

    fun loadLabels() {
        viewModelScope.launch {
            val labels = repository.getAllLabel().sortedBy { it.order }
            _items.value = labels
            _updatedItems.value = labels
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

    fun moveItem(from: Int, to: Int) {
        _updatedItems.value ?: return

        val labels = _updatedItems.value!!.toMutableList()
        val labelToMove = labels[from]
        labels.remove(labelToMove)
        labels.add(to, labelToMove)

        val updatedLabels = mutableListOf<Label>()
        labels.forEachIndexed { index, label ->
            updatedLabels.add(label.copy(order = index + 1))

            _updatedItems.value = updatedLabels
        }

        updateLabels()
    }

    fun updateLabels() {
        val labels = _updatedItems.value ?: return

        viewModelScope.launch {
            labels.forEach { label ->
                repository.updateLabel(label)
            }
        }
    }
}