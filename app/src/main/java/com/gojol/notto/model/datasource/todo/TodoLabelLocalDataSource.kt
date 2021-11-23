package com.gojol.notto.model.datasource.todo

import com.gojol.notto.common.RepeatType
import com.gojol.notto.common.TodoState
import com.gojol.notto.model.data.TodoWithTodayDailyTodo
import com.gojol.notto.model.database.label.Label
import com.gojol.notto.model.database.todo.DailyTodo
import com.gojol.notto.model.database.todo.Todo
import com.gojol.notto.model.database.todo.TodoWithDailyTodo
import com.gojol.notto.model.database.todolabel.LabelWithTodo
import com.gojol.notto.model.database.todolabel.TodoLabelCrossRef
import com.gojol.notto.model.database.todolabel.TodoLabelDao
import com.gojol.notto.model.database.todolabel.TodoWithLabel
import java.time.LocalDate

class TodoLabelLocalDataSource(private val todoLabelDao: TodoLabelDao) :
    TodoLabelDataSource {

    override suspend fun getTodosWithLabels(): List<TodoWithLabel> {
        return todoLabelDao.getTodosWithLabels()
    }

    override suspend fun getTodosWithDailyTodos(): List<TodoWithDailyTodo> {
        return todoLabelDao.getTodosWithDailyTodos()
    }

    override suspend fun getTodosWithTodayDailyTodos(selectedDate: LocalDate): List<TodoWithTodayDailyTodo> {
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
                        null
                    }
                }

                todayDailyTodo =
                    repeatedDate?.let { DailyTodo(TodoState.NOTHING, true, todo.todoId, it) }
                todayDailyTodo?.let { dailyTodo -> todoLabelDao.insertDailyTodo(dailyTodo) }
            } else {
                if (!todayDailyTodo.isActive) todayDailyTodo = null
            }

            todayDailyTodo?.let { TodoWithTodayDailyTodo(todo, it) }
        }
    }

    private fun checkRepeatedWhenSelectedDate(todo: Todo, selectedDate: LocalDate): LocalDate? {
        return if (isValidRepeatedTodo(todo, selectedDate)) {
            selectedDate
        } else {
            null
        }
    }

    private fun isValidRepeatedTodo(todo: Todo, selectedDate: LocalDate): Boolean {
        val dateEqual = todo.startDate.dayOfMonth ==
                selectedDate.dayOfMonth
        val weekEqual = todo.startDate.dayOfWeek ==
                selectedDate.dayOfWeek
        val monthEqual = todo.startDate.month ==
                selectedDate.month

        return (selectedDate.isAfter(todo.startDate) || selectedDate.isEqual(todo.startDate)) &&
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

    override suspend fun insertTodo(todo: Todo, selectedDate: LocalDate): Long {
        val generatedTodoId = todoLabelDao.insertTodo(todo)
        if (todo.isRepeated.not()) todoLabelDao.insertDailyTodo(
            DailyTodo(
                TodoState.NOTHING,
                true,
                generatedTodoId.toInt(),
                selectedDate
            )
        )
        return generatedTodoId
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

    override suspend fun updateTodo(todo: Todo, selectedDate: LocalDate) {
        val beforeTodo = todoLabelDao.getTodoById(todo.todoId)
        if (beforeTodo.isRepeated) {
            when (todo.isRepeated) {
                true -> {
                    // 기존 투두 중 저장되지 않은 Daily 투두 저장
                    saveDailyTodosWhenUpdating(beforeTodo.startDate, todo.startDate, beforeTodo)
                    // 기존에 저장되어 있던 DailyTodo 중 새로 업데이트 된 시작일 이후 것 삭제
                    deleteDailyTodosWhenUpdating(todo.todoId, todo.startDate)
                }
                false -> {
                    saveDailyTodosWhenUpdating(beforeTodo.startDate, selectedDate, beforeTodo)
                    deleteDailyTodosWhenUpdating(todo.todoId, selectedDate)
                    todoLabelDao.insertDailyTodo(DailyTodo(TodoState.NOTHING, true, todo.todoId, selectedDate))
                }
            }
        }

        todoLabelDao.updateTodo(todo) // 투두 업데이트
    }

    private suspend fun saveDailyTodosWhenUpdating(startDate: LocalDate, endDate: LocalDate, beforeTodo: Todo) {
        var beforeDate = startDate
        while (beforeDate.isBefore(endDate)) {
            if (isValidRepeatedTodo(beforeTodo, beforeDate))
                todoLabelDao.insertDailyTodo(
                    DailyTodo(
                        TodoState.NOTHING,
                        true,
                        beforeTodo.todoId,
                        beforeDate
                    )
                )
            beforeDate = beforeDate.plusDays(1)
        }
    }

    private suspend fun deleteDailyTodosWhenUpdating(todoId: Int, date: LocalDate) {
        todoLabelDao.getDailyTodosByParentTodoId(todoId)
            .filter { dailyTodo ->
                dailyTodo.date.dayOfMonth >= date.dayOfMonth
            }
            .forEach { dailyTodo ->
                todoLabelDao.deleteDailyTodo(dailyTodo)
            }
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

    override suspend fun deleteSelectedTodo(todoId: Int, selectedDate: LocalDate) {
        todoLabelDao.updateDailyTodoIsActive(todoId, selectedDate, isActive = false)
    }

    override suspend fun deleteSelectedAndFutureTodo(todoId: Int, selectedDate: LocalDate) {
        todoLabelDao.getDailyTodosByParentTodoId(todoId)
            .filter { dailyTodo ->
                dailyTodo.date.isAfter(selectedDate) || dailyTodo.date == selectedDate
            }
            .forEach { dailyTodo ->
                todoLabelDao.updateDailyTodoIsActive(todoId, dailyTodo.date, false)
            }
    }

    override suspend fun deleteLabel(label: Label) {
        todoLabelDao.deleteLabel(label)
        todoLabelDao.deleteTodoLabelCrossRefByLabel(label.labelId)
    }
}
