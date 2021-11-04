package com.gojol.notto.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gojol.notto.common.TodoSuccessType
import com.gojol.notto.model.data.BindingData
import com.gojol.notto.model.database.label.Label
import com.gojol.notto.model.data.RepeatType
import com.gojol.notto.model.database.todo.Todo
import com.gojol.notto.model.datasource.todo.TodoLabelRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: TodoLabelRepository) : ViewModel() {

    private val dummyLabels = mutableListOf(
        Label(0, "학교"),
        Label(1, "동아리"),
        Label(2, "집"),
        Label(3, "회사"),
        Label(4, "운동"),
        Label(5, "평일"),
        Label(6, "주말"),
        Label(6, "주말"),
        Label(6, "주말"),
        Label(6, "주말"),
        Label(6, "주말"),
        Label(6, "주말"),
        Label(6, "주말"),
    )

    private val dummyTodos = mutableListOf(
        Todo(TodoSuccessType.NOTHING, "안녕하세요", "1", false, RepeatType.DAY, false, "1:00", "2:00", "1:00", false),
        Todo(TodoSuccessType.NOTHING, "안녕하세요", "1", false, RepeatType.DAY, false, "1:00", "2:00", "1:00", false),
        Todo(TodoSuccessType.NOTHING, "안녕하세요", "1", false, RepeatType.DAY, false, "1:00", "2:00", "1:00", false),
//        Todo(TodoSuccessType.NOTHING, "안녕하세요", "1", false, RepeatType.DAY, false, "1:00", "2:00", "1:00", false),
//        Todo(TodoSuccessType.NOTHING, "안녕하세요", "1", false, RepeatType.DAY, false, "1:00", "2:00", "1:00", false),
//        Todo(TodoSuccessType.NOTHING, "안녕하세요", "1", false, RepeatType.DAY, false, "1:00", "2:00", "1:00", false),
//        Todo(TodoSuccessType.NOTHING, "안녕하세요", "1", false, RepeatType.DAY, false, "1:00", "2:00", "1:00", false),
    )

    private val _date = MutableLiveData("2021년 11월")
    val date: LiveData<String> = _date

    private val _labelList = MutableLiveData<MutableList<LabelWithCheck>>()
    val labelList: LiveData<MutableList<LabelWithCheck>> = _labelList

    private val _todoList = MutableLiveData<MutableList<Todo>>()
    val todoList: LiveData<MutableList<Todo>> = _todoList

    private val _concatList = MutableLiveData<BindingData?>()
    val concatList: LiveData<BindingData?> = _concatList

    fun insertTodos() {
        viewModelScope.launch {
            dummyTodos.forEach {
                repository.insertTodo(it)
            }
            _todoList.value = repository.getAllTodo().toMutableList()
        }
    }

    fun insertLabels() {
        viewModelScope.launch {
            dummyLabels.forEach {
                repository.insertLabel(it)
            }
            val labels = repository.getAllLabel()
            _labelList.value = labels.map { label -> LabelWithCheck(label, false) }.toMutableList()
        }
    }

    fun fetchTodoSuccessState(todo: Todo) {
        viewModelScope.launch {
            repository.updateTodo(todo)
            _todoList.value = repository.getAllTodo().toMutableList()
        }
    }
}
