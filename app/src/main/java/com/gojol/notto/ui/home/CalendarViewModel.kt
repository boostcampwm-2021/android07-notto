package com.gojol.notto.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gojol.notto.common.SuccessLevel
import com.gojol.notto.common.TodoState
import com.gojol.notto.model.data.DayWithSuccessLevelAndSelect
import com.gojol.notto.model.database.todo.DailyTodo
import com.gojol.notto.model.datasource.todo.TodoLabelRepository
import com.gojol.notto.ui.home.CalendarFragment.Companion.selectedDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val repository: TodoLabelRepository
) : ViewModel() {

    private var currentCalendarYear = LocalDate.now().year
    private var currentCalendarMonth = LocalDate.now().monthValue
    private var monthStartDate: LocalDate = LocalDate.now()
    private var monthLastDate: LocalDate = LocalDate.now()
    private var monthDateList = emptyList<Int>()
    private var monthlyDailyTodos = emptyList<DailyTodo>()

    private val _monthlyAchievement = MutableLiveData<List<DayWithSuccessLevelAndSelect>>()
    val monthlyAchievement: LiveData<List<DayWithSuccessLevelAndSelect>> = _monthlyAchievement

    fun setMonthDate(year: Int, month: Int) {
        currentCalendarYear = year
        currentCalendarMonth = month

        val baseDate = LocalDate.of(year, month, 1)

        val startDate = baseDate.withDayOfMonth(1)
        monthStartDate = startDate

        val lastDate = startDate.withDayOfMonth(startDate.lengthOfMonth())
        monthLastDate = lastDate

        val dateList = (startDate.dayOfMonth..lastDate.dayOfMonth).toList()
        val dayOfWeek = startDate.dayOfWeek.value
        val prefixDateList = (0 until dayOfWeek).map { 0 }
        monthDateList = prefixDateList + dateList
    }

    fun setMonthlyDailyTodos() {
        viewModelScope.launch {
            // TODO 시점을 투두 저장할때로 변경
            launch {
                repository.getTodosWithTodayDailyTodos(
                    LocalDate.of(currentCalendarYear, currentCalendarMonth, selectedDate)
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
                .filter { it.date.dayOfMonth == date }

            DayWithSuccessLevelAndSelect(date, getSuccessLevel(todayDailyTodos), getSuccess(date))
        }
    }

    private fun getSuccessLevel(todayDailyTodos: List<DailyTodo>): SuccessLevel {
        val successCount = todayDailyTodos.count { it.todoState == TodoState.SUCCESS }
        val totalCount = todayDailyTodos.size

        val successRate = if (totalCount == 0) {
            0f
        } else {
            successCount.toFloat() / totalCount.toFloat()
        }

        val successLevel = when {
            totalCount == 0 || successCount == 0 -> SuccessLevel.ZERO
            successRate <= 0.25 -> SuccessLevel.ONE
            successRate <= 0.5 -> SuccessLevel.TWO
            successRate <= 0.75 -> SuccessLevel.THREE
            successRate < 1 -> SuccessLevel.FOUR
            else -> SuccessLevel.FIVE
        }

        return successLevel
    }

    private fun getSuccess(date: Int): Boolean {
        return if (CalendarFragment.selectedYear == currentCalendarYear &&
            CalendarFragment.selectedMonth == currentCalendarMonth
        ) {
            date == selectedDate
        } else {
            false
        }
    }
}
