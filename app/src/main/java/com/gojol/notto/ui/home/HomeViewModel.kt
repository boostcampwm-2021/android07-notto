package com.gojol.notto.ui.home

import android.os.Build
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gojol.notto.model.data.RepeatType
import com.gojol.notto.model.database.label.Label
import com.gojol.notto.model.database.todo.Todo
import com.gojol.notto.model.datasource.todo.TodoLabelRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.*
import java.util.Calendar.Builder
import java.util.Calendar.getInstance
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: TodoLabelRepository) : ViewModel() {

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

    private val _date = MutableLiveData<Calendar>(getInstance())
    val date: LiveData<Calendar> = _date

    private val _labelList = MutableLiveData<List<Label>>(dummyLabels)
    val labelList: LiveData<List<Label>> = _labelList

    private val _todoList = MutableLiveData<List<Todo>>(dummyTodos)
    val todoList: LiveData<List<Todo>> = _todoList

    fun updateDate(year: Int, month: Int, day: Int) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            _date.value = Builder().setDate(year, month, day).build()
        } else {
            val calendar: Calendar = getInstance()
            calendar.set(year, month, day)

            _date.value = calendar
        }
    }
}
