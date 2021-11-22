package com.gojol.notto.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gojol.notto.common.TodoState
import com.gojol.notto.model.data.DateWithCountAndSelect
import com.gojol.notto.model.database.todo.DailyTodo
import com.gojol.notto.model.datasource.todo.TodoLabelRepository
import com.gojol.notto.ui.home.CalendarFragment.Companion.selectedDate
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

    private var currentCalendarYear = 0
    private var currentCalendarMonth = 0
    private var monthStartDate: String = ""
    private var monthLastDate: String = ""
    private var monthDateList = emptyList<Int>()
    private var monthlyDailyTodos = emptyList<DailyTodo>()

    private val _monthlyAchievement = MutableLiveData<List<DateWithCountAndSelect>>()
    val monthlyAchievement: LiveData<List<DateWithCountAndSelect>> = _monthlyAchievement

    fun setMonthDate(year: Int, month: Int) {
        currentCalendarYear = year
        currentCalendarMonth = month

        val startDate = Calendar.getInstance().apply {
            set(year, month - 1, 1)
        }
        monthStartDate = startDate.toYearMonthDate()

        val lastDate = Calendar.getInstance().apply {
            set(year, month - 1, startDate.getLastDayOfMonth())
        }
        monthLastDate = lastDate.toYearMonthDate()

        val dateList = (startDate.getDate()..lastDate.getDate()).toList()
        val dayOfWeek = startDate.getDayOfWeek() - 1
        val prefixDateList = (0 until dayOfWeek).map { 0 }
        monthDateList = prefixDateList + dateList
    }

    fun setMonthlyDailyTodos() {
        val formatMonth = if (currentCalendarMonth.toString().length == 1) {
            "0$currentCalendarMonth"
        } else {
            currentCalendarMonth.toString()
        }

        val formatDate = if (selectedDate.toString().length == 1) {
            "0$selectedDate"
        } else {
            selectedDate.toString()
        }

        viewModelScope.launch {
            // TODO 시점을 투두 저장할때로 변경
            launch {
                repository.getTodosWithTodayDailyTodos(
                    "$currentCalendarYear$formatMonth$formatDate"
                )
            }.join()
            launch {
                monthlyDailyTodos = repository.getAllDailyTodos().filter {
                    it.date in monthStartDate..monthLastDate
                }
            }.join()

            setMonthlyAchievement()
        }
    }

    private fun setMonthlyAchievement() {
        _monthlyAchievement.value = monthDateList.map { date ->
            val todayDailyTodos = monthlyDailyTodos
                .filter { it.date.takeLast(2).toInt() == date }

            DateWithCountAndSelect(date, getSuccessLevel(todayDailyTodos), getSuccess(date))
        }
    }

    private fun getSuccessLevel(todayDailyTodos: List<DailyTodo>): Int {
        val successCount = todayDailyTodos.count { it.todoState == TodoState.SUCCESS }
        val totalCount = todayDailyTodos.size

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

        return successLevel
    }

    private fun getSuccess(date: Int): Boolean {
        return if (CalendarFragment.selectedYear == currentCalendarYear &&
            CalendarFragment.selectedMonth == currentCalendarMonth
        ) {
            date == CalendarFragment.selectedDate ?: 1
        } else {
            false
        }
    }
}
