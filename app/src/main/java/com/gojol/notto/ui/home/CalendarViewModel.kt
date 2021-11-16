package com.gojol.notto.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gojol.notto.common.TodoState
import com.gojol.notto.model.data.DateWithCountAndSelect
import com.gojol.notto.model.database.todo.DailyTodo
import com.gojol.notto.model.datasource.todo.TodoLabelRepository
import com.gojol.notto.util.getDate
import com.gojol.notto.util.getDayOfWeek
import com.gojol.notto.util.getLastDayOfMonth
import com.gojol.notto.util.getMonth
import com.gojol.notto.util.getYear
import com.gojol.notto.util.toYearMonthDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val repository: TodoLabelRepository
) : ViewModel() {

    private var _year = 0
    private var _month = 0
    private val _monthStartDate = MutableLiveData<String>()
    private val _monthLastDate = MutableLiveData<String>()
    private val _monthDateList = MutableLiveData<List<Int>>()
    private val _monthlyDailyTodos = MutableLiveData<List<DailyTodo>>()

    private val _monthlyAchievement = MutableLiveData<List<DateWithCountAndSelect>>()
    val monthlyAchievement: LiveData<List<DateWithCountAndSelect>> = _monthlyAchievement

    private val _selectedDate = MutableLiveData<Int>()
    val selectedDate: LiveData<Int> = _selectedDate

    fun setMonthDate(year: Int, month: Int) {
        _year = year
        _month = month

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
            val todayDailyTodos = _monthlyDailyTodos.value
                ?.filter { it.date.takeLast(2).toInt() == date }

            val successCount = todayDailyTodos?.count { it.todoState == TodoState.SUCCESS } ?: 0

            val totalCount = todayDailyTodos?.size ?: 0

            val successRate = if (totalCount == 0) {
                0.toFloat()
            } else {
                successCount.toFloat() / totalCount.toFloat()
            }

            var successLevel = when {
                successRate <= 0.25 -> 1
                successRate <= 0.5 -> 2
                successRate <= 0.75 -> 3
                successRate < 1 -> 4
                else -> 5
            }
            if (totalCount == 0 || successCount == 0) {
                successLevel = 0
            }

            val select =
                if (CalendarFragment.selectedYear == _year &&
                    CalendarFragment.selectedMonth == _month
                ) {
                    date == CalendarFragment.selectedDate ?: 1
                } else {
                    false
                }

            DateWithCountAndSelect(
                date,
                successLevel,
                select
            )
        }
    }
}
