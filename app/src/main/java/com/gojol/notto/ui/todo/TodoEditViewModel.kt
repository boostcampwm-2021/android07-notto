package com.gojol.notto.ui.todo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gojol.notto.model.database.label.Label
import com.gojol.notto.model.datasource.todo.FakeTodoLabelRepository
import com.gojol.notto.model.datasource.todo.TodoLabelRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TodoEditViewModel @Inject constructor(private val repository: TodoLabelRepository) :
    ViewModel() {

    private val fakeRepository = FakeTodoLabelRepository()

    private val _labelList = MutableLiveData<List<Label>>()
    val labelList: LiveData<List<Label>> = _labelList

    private val _selectedLabelList = MutableLiveData<List<Label>>()
    val selectedLabelList: LiveData<List<Label>> = _selectedLabelList

    init {
        _selectedLabelList.value = listOf()
    }

    fun setDummyLabelData() {
        viewModelScope.launch {
            _labelList.value = fakeRepository.getAllLabel()
        }
    }

    fun addLabelToSelectedLabelList(labelName: String) {
        if (labelName == "선택") return
        if (selectedLabelList.value?.find { it.name == labelName } != null) return

        val label = labelList.value?.find { it.name == labelName } ?: return
        val newLabelList = selectedLabelList.value?.toMutableList()?.apply {
            add(label)
        } ?: return
        _selectedLabelList.value = newLabelList
    }

    fun removeLabelFromSelectedLabelList(label: Label) {
        println("label clicked: $label")
        val newLabelList = selectedLabelList.value?.toMutableList()?.apply{
            remove(label)
        } ?: return
        _selectedLabelList.value = newLabelList
    }
}
