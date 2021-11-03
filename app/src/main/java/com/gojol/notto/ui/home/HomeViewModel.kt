package com.gojol.notto.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gojol.notto.model.database.label.Label
import com.gojol.notto.model.data.RepeatType
import com.gojol.notto.model.database.todo.Todo

class HomeViewModel : ViewModel() {

    private val dummyLabels = listOf(
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

    private val dummyTodos = listOf(
       Todo(false, "안녕하세요", "1", false, RepeatType.DAY, false, "1:00", "2:00", "1:00", false),
       Todo(false, "안녕하세요", "1", false, RepeatType.DAY, false, "1:00", "2:00", "1:00", false),
       Todo(false, "안녕하세요", "1", false, RepeatType.DAY, false, "1:00", "2:00", "1:00", false),
       Todo(false, "안녕하세요", "1", false, RepeatType.DAY, false, "1:00", "2:00", "1:00", false),
       Todo(false, "안녕하세요", "1", false, RepeatType.DAY, false, "1:00", "2:00", "1:00", false),
       Todo(false, "안녕하세요", "1", false, RepeatType.DAY, false, "1:00", "2:00", "1:00", false),
       Todo(false, "안녕하세요", "1", false, RepeatType.DAY, false, "1:00", "2:00", "1:00", false),
    )

    private val _date = MutableLiveData<String>("2021년 11월")
    val date: LiveData<String> = _date

    private val _labelList = MutableLiveData<List<Label>>(dummyLabels)
    val labelList: LiveData<List<Label>> = _labelList

    private val _todoList = MutableLiveData<List<Todo>>(dummyTodos)
    val todoList: LiveData<List<Todo>> = _todoList
}
