package com.gojol.notto.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gojol.notto.model.data.LabelWithCheck
import com.gojol.notto.model.data.TodoWithTodayDateState
import com.gojol.notto.model.database.label.Label
import com.gojol.notto.model.database.todo.DateState
import com.gojol.notto.model.database.todolabel.LabelWithTodo
import com.gojol.notto.model.datasource.todo.FakeTodoLabelRepository
import com.gojol.notto.model.datasource.todo.TodoLabelRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: TodoLabelRepository) : ViewModel() {

    private val fakeRepository = FakeTodoLabelRepository()

    private val _date = MutableLiveData(Calendar.getInstance())
    val date: LiveData<Calendar> = _date

    private val _todoList = MutableLiveData<List<TodoWithTodayDateState>>()
    val todoList: LiveData<List<TodoWithTodayDateState>> = _todoList

    private val _labelList = MutableLiveData<List<LabelWithCheck>>()
    val labelList: LiveData<List<LabelWithCheck>> = _labelList

    fun setDummyData() {
        viewModelScope.launch {
            insertDummyTodoAndLabel()
        }
    }

    private suspend fun insertDummyTodoAndLabel() {
        val totalLabel = LabelWithTodo(Label(0, LABEL_NAME_ALL), fakeRepository.getAllTodo())
        val newLabelList = fakeRepository.getLabelsWithTodos()
            .asSequence()
            .map { label -> LabelWithCheck(label, false) }
            .toMutableList()
            .apply { add(0, LabelWithCheck(totalLabel, true)) }

        _labelList.value = newLabelList
        _todoList.value = fakeRepository.getTodosWithTodayDateState()
    }

    fun updateDate(year: Int, month: Int, day: Int) {
        val calendar: Calendar = Calendar.getInstance()
        calendar.set(year, month, day)

        _date.value = calendar
    }

    fun updateDateState(dateState: DateState) {
        viewModelScope.launch {
            fakeRepository.updateDateState(dateState)
            _todoList.value = fakeRepository.getTodosWithTodayDateState()
        }
    }

    fun updateTodoList(list: List<LabelWithCheck>) {
        val checkList = list.filter { it.isChecked }
        viewModelScope.launch { addTodoListByLabels(checkList) }
    }

    private suspend fun addTodoListByLabels(labels: List<LabelWithCheck>) {
        val todoIdList = labels
            .asSequence()
            .flatMap { it.labelWithTodo.todo }
            .map { it.todoId }

        val newTodoList = fakeRepository.getTodosWithTodayDateState()
            .filter { it.todo.todoId in todoIdList }

        _todoList.value = newTodoList
    }

    fun setLabelClickListener(labelWithCheck: LabelWithCheck) {
        if (labelWithCheck.labelWithTodo.label.order == 0) {
            allChipChecked()
        } else {
            itemLabelClick(labelWithCheck)
        }
    }

    private fun itemLabelClick(labelWithCheck: LabelWithCheck) {
        _labelList.value?.let { list ->
            if (labelWithCheck.isChecked) {
                moveItem(1, true, labelWithCheck)
            } else {
                val checkedList = list.filter {
                    it.isChecked &&
                            it.labelWithTodo.label.labelId != labelWithCheck.labelWithTodo.label.labelId &&
                            it.labelWithTodo.label.order != 0
                }

                val uncheckedList = list
                    .asSequence()
                    .filter { !it.isChecked && it.labelWithTodo.label.order != 0 }
                    .toMutableList()
                    .apply { add(labelWithCheck) }
                    .sortedBy { it.labelWithTodo.label.order }

                if (checkedList.isEmpty()) {
                    allChipChecked()
                } else {
                    moveItem(
                        checkedList.size + uncheckedList.indexOf(labelWithCheck),
                        false,
                        labelWithCheck
                    )
                }
            }
        }
    }

    private fun moveItem(to: Int, isChecked: Boolean, labelWithCheck: LabelWithCheck) {
        _labelList.value?.let { list ->
            val newList = list.toMutableList().filter {
                it.labelWithTodo.label.labelId != labelWithCheck.labelWithTodo.label.labelId
            }.toMutableList()
            newList[0] = newList[0].copy(isChecked = false)
            newList.add(to, labelWithCheck.copy(isChecked = isChecked))

            _labelList.value = newList
        }
    }

    private fun allChipChecked() {
        _labelList.value?.let { list ->
            val header = list[0].copy(isChecked = true)
            val newList = list
                .asSequence()
                .filter { it.labelWithTodo.label.order != 0 }
                .map { it.copy(isChecked = false) }
                .sortedBy { it.labelWithTodo.label.order }
                .toMutableList()
            newList.add(0, header)
            _labelList.value = newList
        }
    }

    companion object {
        const val LABEL_NAME_ALL = "전체"
    }
}

