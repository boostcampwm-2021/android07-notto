package com.gojol.notto.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gojol.notto.common.Event
import com.gojol.notto.model.data.LabelWithCheck
import com.gojol.notto.model.data.TodoWithTodayDailyTodo
import com.gojol.notto.model.database.label.Label
import com.gojol.notto.model.database.todo.DailyTodo
import com.gojol.notto.model.database.todo.Todo
import com.gojol.notto.model.datasource.todo.TodoAlarmManager
import com.gojol.notto.model.datasource.todo.TodoLabelRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: TodoLabelRepository,
    private val todoAlarmManager: TodoAlarmManager
) : ViewModel() {

    private val _isTodoCreateButtonClicked = MutableLiveData<Event<Boolean>>()
    val isTodoCreateButtonClicked: LiveData<Event<Boolean>> = _isTodoCreateButtonClicked

    private val _todoEditButtonClicked = MutableLiveData<Event<Todo>>()
    val todoEditButtonClicked: LiveData<Event<Todo>> = _todoEditButtonClicked

    private val _date = MutableLiveData(LocalDate.now())
    val date: LiveData<LocalDate> = _date

    private val _todoList = MutableLiveData<List<TodoWithTodayDailyTodo>>()
    val todoList: LiveData<List<TodoWithTodayDailyTodo>> = _todoList

    private val _labelList = MutableLiveData<List<LabelWithCheck>>()
    val labelList: LiveData<List<LabelWithCheck>> = _labelList

    fun setDummyData() {
        viewModelScope.launch {
            initLabelAndTodo()
        }
    }

    private suspend fun initLabelAndTodo() {
        viewModelScope.launch {
            launch { repository.insertLabel(totalLabel) }.join()
            _labelList.value = repository.getLabelsWithTodos()
                .asSequence()
                .map { LabelWithCheck(it, it.label.order == 0) }
                .sortedBy { it.labelWithTodo.label.order }
                .toList()
            _todoList.value =
                _date.value?.let { repository.getTodosWithTodayDailyTodos(it) }
        }
    }

    fun updateDate(year: Int? = null, month: Int? = null, day: Int? = null) {
        _date.value = if (year != null && month != null && day != null) {
            LocalDate.of(year, month, day)
        } else {
            return
        }
    }

    fun updateDailyTodo(dailyTodo: DailyTodo) {
        viewModelScope.launch {
            launch { repository.updateDailyTodo(dailyTodo) }.join()
            val currentShowTodoList = _labelList.value
                ?.asSequence()
                ?.filter { it.isChecked }
                ?.flatMap { it.labelWithTodo.todo }

            if (currentShowTodoList != null) {
                _todoList.value = _date.value?.let { date ->
                    repository.getTodosWithTodayDailyTodos(date)
                }?.filter { currentShowTodoList.contains(it.todo) }
            }

            todoAlarmManager.deleteAlarms()
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

        _date.value?.let { date ->
            _todoList.value =
                repository.getTodosWithTodayDailyTodos(date)
                    .filter { it.todo.todoId in todoIdList }
        }
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
                        checkedList.size + uncheckedList.indexOf(labelWithCheck) + 1,
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

    fun updateNavigateToTodoEdit() {
        _isTodoCreateButtonClicked.value = Event(true)
    }

    fun updateIsTodoEditButtonClicked(todo: Todo) {
        _todoEditButtonClicked.value = Event(todo)
    }

    companion object {
        val totalLabel = Label(0, "전체", 1)
    }
}
