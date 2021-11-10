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

    private val _isRepeatChecked = MutableLiveData<Boolean>()
    val isRepeatChecked: LiveData<Boolean> = _isRepeatChecked

    private val _repeatType = MutableLiveData<String>()
    val repeatType: LiveData<String> = _repeatType

    private val _repeatStart = MutableLiveData<String>()
    val repeatStart: LiveData<String> = _repeatStart

    private val _isTimeChecked = MutableLiveData<Boolean>()
    val isTimeChecked: LiveData<Boolean> = _isTimeChecked

    private val _timeStart = MutableLiveData<String>()
    val timeStart: LiveData<String> = _timeStart

    private val _timeFinish = MutableLiveData<String>()
    val timeFinish: LiveData<String> = _timeFinish

    private val _timeRepeat = MutableLiveData<String>()
    val timeRepeat: LiveData<String> = _timeRepeat

    private val _isKeywordChecked = MutableLiveData<Boolean>()
    val isKeywordChecked: LiveData<Boolean> = _isKeywordChecked

    init {
        _selectedLabelList.value = listOf()

        //나중에 지우기
        _repeatType.value = "test"
        _repeatStart.value = "test"
        _timeStart.value = "test"
        _timeFinish.value = "test"
        _timeRepeat.value = "test"
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
        val newLabelList = selectedLabelList.value?.toMutableList()?.apply {
            remove(label)
        } ?: return
        _selectedLabelList.value = newLabelList
    }

    fun updateIsRepeatChecked(isChecked: Boolean) {
        _isRepeatChecked.value = isChecked
    }

    fun updateIsTimeChecked(isChecked: Boolean) {
        _isTimeChecked.value = isChecked
    }

    fun updateIsKeywordChecked(isChecked: Boolean) {
        _isKeywordChecked.value = isChecked
    }
}
