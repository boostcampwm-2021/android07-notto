package com.gojol.notto.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gojol.notto.common.TodoState
import com.gojol.notto.model.database.todo.DailyTodo
import com.gojol.notto.model.datasource.todo.TodoLabelRepository
import com.gojol.notto.util.getDate
import com.gojol.notto.util.getDayOfWeek
import com.gojol.notto.util.getLastDayOfMonth
import com.gojol.notto.util.toYearMonthDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val repository: TodoLabelRepository
) : ViewModel() {

    private var _monthStartDate = MutableLiveData<String>()
    private var _monthLastDate = MutableLiveData<String>()
    private var _monthDateList = MutableLiveData<List<Int>>()
    private val _monthlyDailyTodos = MutableLiveData<List<DailyTodo>>()

    private val _monthlyAchievement = MutableLiveData<List<Pair<Int, Int>>>()
    val monthlyAchievement: LiveData<List<Pair<Int, Int>>> = _monthlyAchievement

    fun setMonthDate(year: Int, month: Int) {
        val monthStartDate = Calendar.getInstance().apply {
            set(year, month, 1)
        }
        _monthStartDate.value = monthStartDate.toYearMonthDate()

        val monthLastDate = Calendar.getInstance().apply {
            set(year, month, monthStartDate.getLastDayOfMonth())
        }
        _monthLastDate.value = monthLastDate.toYearMonthDate()

        val dateList = (monthStartDate.getDate()..monthLastDate.getDate()).toList()
        val dayOfWeek = monthStartDate.getDayOfWeek() - 1
        val prefixDateList = (0 until dayOfWeek).map { 0 }
        _monthDateList.value = prefixDateList + dateList
    }

    fun setMonthlyDailyTodos() {
        viewModelScope.launch {
            _monthStartDate.value?.let { startDate ->
                _monthLastDate.value?.let { lastDate ->
                    launch {
                        _monthlyDailyTodos.value = repository.getAllDailyTodos().filter {
                            it.date > startDate && it.date < lastDate
                        }
                    }.join()

                    setMonthlyAchievement()
                }
            }
        }
    }

    fun setMonthlyAchievement() {
        _monthlyAchievement.value = _monthDateList.value?.map { date ->
            date to (_monthlyDailyTodos.value
                ?.filter { it.date.takeLast(2).toInt() == date }
                ?.count { it.todoState == TodoState.SUCCESS } ?: 0)
        }
        Log.i("daily", _monthlyAchievement.value.toString())
    }
}
