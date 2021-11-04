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
import com.gojol.notto.model.database.todolabel.LabelWithTodo
import com.gojol.notto.model.datasource.todo.TodoLabelRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: TodoLabelRepository) : ViewModel() {

    private val dummyLabels = mutableListOf(
        Label(1, "학교"),
        Label(2, "동아리"),
        Label(3, "집"),
        Label(4, "학원")
    )

    private val dummyTodos = mutableListOf(
        Todo(TodoSuccessType.NOTHING, "hello1", "1", false, RepeatType.DAY, false, "1:00", "2:00", "1:00", false),
        Todo(TodoSuccessType.NOTHING, "hello2", "1", false, RepeatType.DAY, false, "1:00", "2:00", "1:00", false),
        Todo(TodoSuccessType.NOTHING, "hello3", "1", false, RepeatType.DAY, false, "1:00", "2:00", "1:00", false),
        Todo(TodoSuccessType.NOTHING, "hello4", "1", false, RepeatType.DAY, false, "1:00", "2:00", "1:00", false),
        Todo(TodoSuccessType.NOTHING, "hello5", "1", false, RepeatType.DAY, false, "1:00", "2:00", "1:00", false),
        Todo(TodoSuccessType.NOTHING, "hello6", "1", false, RepeatType.DAY, false, "1:00", "2:00", "1:00", false),
        Todo(TodoSuccessType.NOTHING, "hello7", "1", false, RepeatType.DAY, false, "1:00", "2:00", "1:00", false),
    )

    private val _date = MutableLiveData("2021년 11월")
    val date: LiveData<String> = _date

    private val _labelList = MutableLiveData<MutableList<LabelWithCheck>>()
    val labelList: LiveData<MutableList<LabelWithCheck>> = _labelList

    private val _todoList = MutableLiveData<MutableList<Todo>>()
    val todoList: LiveData<MutableList<Todo>> = _todoList

    private val _concatList = MutableLiveData<BindingData?>()
    val concatList: LiveData<BindingData?> = _concatList

    fun setDummyData() {
        viewModelScope.launch {
            insertDummyTodoAndLabel()
        }
    }

    suspend fun insertDummyTodoAndLabel() {
        dummyTodos.forEach {
            repository.insertTodo(it)
        }

        dummyLabels.forEach {
            repository.insertLabel(it)
        }


        insertTodoLabel()

        _todoList.value = repository.getAllTodo().toMutableList()
        val labels = repository.getLabelWithTodo()
        val nList = labels.map { label -> LabelWithCheck(label, false) }.toMutableList()
        val totalLabel = LabelWithTodo(
            Label(0, "전체"), _todoList.value!!
        )
        nList.add(0, LabelWithCheck(totalLabel, true))
        _labelList.value = nList
    }

    suspend fun insertTodoLabel() {
        val labels = repository.getAllLabel()
        val todos = repository.getAllTodo()

        //학교
        println(labels[0])
        repository.insertTodo(todos[2], labels[0])
        repository.insertTodo(todos[3], labels[0])
        repository.insertTodo(todos[4], labels[0])

        // 동아리
        println(labels[1])
        repository.insertTodo(todos[0], labels[1])
        repository.insertTodo(todos[1], labels[1])
        repository.insertTodo(todos[2], labels[1])

        // 집
        println(labels[2])
        repository.insertTodo(todos[2], labels[2])
        repository.insertTodo(todos[4], labels[2])
        repository.insertTodo(todos[5], labels[2])

        // 학원
        println(labels[3])
        repository.insertTodo(todos[1], labels[3])
        repository.insertTodo(todos[2], labels[3])
    }

    fun fetchTodoSuccessState(todo: Todo) {
        val nList = mutableListOf<Todo>()
        viewModelScope.launch {
            repository.updateTodo(todo)
            repository.getAllTodo().forEach { t1 ->
                _todoList.value?.forEach { t2 ->
                    if (t1.todoId == t2.todoId) {
                        nList.add(t1)
                    }
                }
            }
            _todoList.value = nList
        }
    }

    suspend fun addTodoListByLabels(labels: List<LabelWithCheck>) {
        val nList = mutableListOf<Todo>()

        val todoSet = mutableSetOf<Todo>()
        labels.forEach {
            todoSet.addAll(it.label.todo)
        }

        repository.getAllTodo().forEach {
            todoSet.forEach { setTodo ->
                if(it.todoId == setTodo.todoId) {
                    nList.add(it)
                }
            }
        }

        _todoList.value = nList
    }
}

