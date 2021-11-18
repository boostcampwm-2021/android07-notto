package com.gojol.notto.model.datasource.todo

import java.util.Calendar
import com.gojol.notto.common.TodoState
import com.gojol.notto.model.data.RepeatType
import com.gojol.notto.model.data.TodoWithTodayDailyTodo
import com.gojol.notto.model.database.label.Label
import com.gojol.notto.model.database.todo.DailyTodo
import com.gojol.notto.model.database.todo.Todo
import com.gojol.notto.model.database.todo.TodoWithDailyTodo
import com.gojol.notto.model.database.todolabel.LabelWithTodo
import com.gojol.notto.model.database.todolabel.TodoLabelCrossRef
import com.gojol.notto.model.database.todolabel.TodoLabelDao
import com.gojol.notto.model.database.todolabel.TodoWithLabel
import com.gojol.notto.util.getDate
import com.gojol.notto.util.getDayOfWeek
import com.gojol.notto.util.getMonth
import com.gojol.notto.util.toCalendar
import com.gojol.notto.util.toYearMonthDate

class TodoLabelLocalDataSource(private val todoLabelDao: TodoLabelDao) :
    TodoLabelDataSource {

    override suspend fun getTodosWithLabels(): List<TodoWithLabel> {
        return todoLabelDao.getTodosWithLabels()
    }

    override suspend fun getTodosWithDailyTodos(): List<TodoWithDailyTodo> {
        return todoLabelDao.getTodosWithDailyTodos()
    }

    override suspend fun getTodosWithTodayDailyTodos(selectedDate: String): List<TodoWithTodayDailyTodo> {
        return todoLabelDao.getTodosWithDailyTodos().mapNotNull { todoWithDailyTodo ->
            val todo = todoWithDailyTodo.todo
            val dailyTodos = todoWithDailyTodo.dailyTodos

            var todayDailyTodo =
                dailyTodos.find { it.parentTodoId == todo.todoId && it.date == selectedDate }

            if (todayDailyTodo == null) {
                val repeatedDate = when {
                    // 반복설정한 경우 반복 조건에 따라 오늘의 Daily를 추가할지 결정
                    todo.isRepeated -> {
                        checkRepeatedWhenSelectedDate(todo, selectedDate)
                    }
                    else -> {
                        // 반복설정을 하지 않고 투두를 생성한 경우 오늘의 Daily 추가
                        if (selectedDate == Calendar.getInstance().toYearMonthDate()) {
                            selectedDate
                        } else {
                            null
                        }
                    }
                }

                todayDailyTodo = repeatedDate?.let { DailyTodo(TodoState.NOTHING, todo.todoId, it) }
            }

            if (todayDailyTodo != null) {
                todoLabelDao.insertDailyTodo(todayDailyTodo)
            }

            todayDailyTodo?.let { TodoWithTodayDailyTodo(todo, it) }
        }
    }

    private fun checkRepeatedWhenSelectedDate(todo: Todo, selectedDate: String): String? {
        return if (isValidRepeatedTodo(todo, selectedDate)) {
            selectedDate
        } else {
            null
        }
    }

    private fun isValidRepeatedTodo(todo: Todo, selectedDate: String): Boolean {
        val dateEqual = todo.startDate.toCalendar().getDate() ==
                selectedDate.toCalendar().getDate()
        val weekEqual = todo.startDate.toCalendar().getDayOfWeek() ==
                selectedDate.toCalendar().getDayOfWeek()
        val monthEqual = todo.startDate.toCalendar().getMonth() ==
                selectedDate.toCalendar().getMonth()

        return (selectedDate.toInt() >= todo.startDate.toInt()) &&
                ((todo.repeatType == RepeatType.DAY) ||
                        (todo.repeatType == RepeatType.WEEK && weekEqual) ||
                        (todo.repeatType == RepeatType.MONTH && dateEqual) ||
                        (todo.repeatType == RepeatType.YEAR && dateEqual && monthEqual))
    }

    override suspend fun getLabelsWithTodos(): List<LabelWithTodo> {
        return todoLabelDao.getLabelsWithTodos()
    }

    override suspend fun getAllTodo(): List<Todo> {
        return todoLabelDao.getAllTodo()
    }

    override suspend fun getAllDailyTodos(): List<DailyTodo> {
        return todoLabelDao.getAllDailyTodo()
    }

    override suspend fun getAllLabel(): List<Label> {
        return todoLabelDao.getAllLabel()
    }

    override suspend fun insertTodo(todo: Todo) {
        todoLabelDao.insertTodo(todo)
    }

    override suspend fun insertTodo(todo: Todo, label: Label) {
        todoLabelDao.insert(TodoLabelCrossRef(todo.todoId, label.labelId))
    }

    override suspend fun insertLabel(label: Label) {
        todoLabelDao.insertLabel(label)
    }

    override suspend fun insertDailyTodo(dailyTodo: DailyTodo) {
        todoLabelDao.insertDailyTodo(dailyTodo)
    }

    override suspend fun updateTodo(todo: Todo) {
        todoLabelDao.updateTodo(todo)
    }

    override suspend fun updateTodo(todo: Todo, labels: List<Label>) {
        val todosWithLabels = todoLabelDao.getTodosWithLabels().find {
            it.todo.todoId == todo.todoId
        }
        val labelList = todosWithLabels?.labels?.filterNot { it.order == 0 }

        labelList?.forEach {
            todoLabelDao.delete(TodoLabelCrossRef(todo.todoId, it.labelId))
        }
        labels.forEach { label ->
            todoLabelDao.insert(TodoLabelCrossRef(todo.todoId, label.labelId))
        }
    }

    override suspend fun updateLabel(label: Label) {
        todoLabelDao.updateLabel(label)
    }

    override suspend fun updateDailyTodo(dailyTodo: DailyTodo) {
        todoLabelDao.updateDailyTodo(dailyTodo)
    }

    override suspend fun deleteTodo(todo: Todo) {
        todoLabelDao.deleteTodo(todo)
        todoLabelDao.deleteTodoLabelCrossRefByTodo(todo.todoId)
    }

    override suspend fun deleteLabel(label: Label) {
        todoLabelDao.deleteLabel(label)
        todoLabelDao.deleteTodoLabelCrossRefByLabel(label.labelId)
    }
}
