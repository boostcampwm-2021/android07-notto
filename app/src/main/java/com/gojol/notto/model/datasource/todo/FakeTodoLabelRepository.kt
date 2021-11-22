package com.gojol.notto.model.datasource.todo

import com.gojol.notto.common.TimeRepeatType
import com.gojol.notto.common.TodoState
import com.gojol.notto.model.data.RepeatType
import com.gojol.notto.model.data.TodoWithTodayDailyTodo
import com.gojol.notto.model.database.label.Label
import com.gojol.notto.model.database.todo.DailyTodo
import com.gojol.notto.model.database.todo.Todo
import com.gojol.notto.model.database.todo.TodoWithDailyTodo
import com.gojol.notto.model.database.todolabel.LabelWithTodo
import com.gojol.notto.model.database.todolabel.TodoWithLabel
import java.time.LocalDate
import java.time.LocalTime

// not use
class FakeTodoLabelRepository : TodoLabelDataSource {

    var todoId = 10

    private val today = LocalDate.now()
    private val time = LocalTime.now()

    private var selectedDate: LocalDate = LocalDate.now()

    private var todos = mutableListOf(
        Todo("밥 굶지 않기", false, RepeatType.DAY, today, false,time, time, TimeRepeatType.MINUTE_5, false, false, today, 1),
        Todo("과제 미루지 않기", false, RepeatType.DAY, today, false, time, time, TimeRepeatType.MINUTE_5, false, false, today, 2),
        Todo("지각하지 않기", false, RepeatType.DAY, today, false, time, time, TimeRepeatType.MINUTE_5, false, false,today, 3),
        Todo("밥 먹을 때 물 먹지 않기", false, RepeatType.DAY, today, false, time, time, TimeRepeatType.MINUTE_5, false, false, today, 4),
        Todo("회의 지각 안하기", false, RepeatType.DAY, today, false, time, time, TimeRepeatType.MINUTE_5, false, false, today, 5),
        Todo("핸드폰 보지 않기", false, RepeatType.DAY, today, false, time, time, TimeRepeatType.MINUTE_5, false, false, today, 6),
        Todo("누워있지 않기", false, RepeatType.DAY, today, false, time, time, TimeRepeatType.MINUTE_5, false, false, today, 7),
    )

    private var labels = mutableListOf(
        Label(1, "학교", 2),
        Label(2, "건강", 3),
        Label(3, "집", 4),
        Label(4, "과제", 5)
    )

    private var dailyTodos = mutableListOf<DailyTodo>()

    private var todosWithLabels = mutableListOf(
        TodoWithLabel(todos[0], listOf(labels[1])),
        TodoWithLabel(todos[1], listOf(labels[1], labels[3])),
        TodoWithLabel(todos[2], listOf(labels[0], labels[1], labels[2], labels[3])),
        TodoWithLabel(todos[3], listOf(labels[0])),
        TodoWithLabel(todos[4], listOf(labels[0], labels[2])),
        TodoWithLabel(todos[5], listOf(labels[2]))
    )

    private var labelsWithTodos = mutableListOf(
        LabelWithTodo(labels[0], listOf(todos[2], todos[3], todos[4])),
        LabelWithTodo(labels[1], listOf(todos[0], todos[1], todos[2])),
        LabelWithTodo(labels[2], listOf(todos[2], todos[4], todos[5])),
        LabelWithTodo(labels[3], listOf(todos[1], todos[2]))
    )

    override suspend fun getTodosWithLabels(): List<TodoWithLabel> {
        return todosWithLabels
    }

    override suspend fun getTodosWithDailyTodos(): List<TodoWithDailyTodo> {
        return todos.map { todo ->
            TodoWithDailyTodo(todo, dailyTodos.filter { it.parentTodoId == todo.todoId })
        }
    }

    override suspend fun getTodosWithTodayDailyTodos(selectedDate: LocalDate): List<TodoWithTodayDailyTodo> {
        return todos.mapNotNull { todo ->
            var todayDailyTodo =
                dailyTodos.find { it.parentTodoId == todo.todoId && it.date == this.selectedDate }

            if (todayDailyTodo == null) {
                todayDailyTodo = DailyTodo(TodoState.NOTHING, true, todo.todoId, this.selectedDate)
                dailyTodos.add(todayDailyTodo)
            } else {
                if (!todayDailyTodo.isActive) todayDailyTodo = null
            }

            todayDailyTodo?.let { TodoWithTodayDailyTodo(todo, it) }
        }
    }

    override suspend fun getLabelsWithTodos(): List<LabelWithTodo> {
        return labelsWithTodos
    }

    override suspend fun getAllTodo(): List<Todo> {
        return todos
    }

    override suspend fun getAllLabel(): List<Label> {
        return labels
    }

    override suspend fun getAllDailyTodos(): List<DailyTodo> {
        return dailyTodos
    }

    override suspend fun insertTodo(todo: Todo) {
        todos.add(todo)
    }

    override suspend fun insertTodo(todo: Todo, label: Label) {
        val labelsWithTodosIterator = labelsWithTodos.iterator()
        while (labelsWithTodosIterator.hasNext()) {
            val labelWithTodo = labelsWithTodosIterator.next()
            if (labelWithTodo.label == label && labelWithTodo.todo.contains(todo).not()) {
                labelsWithTodos.add(labelWithTodo.copy(todo = labelWithTodo.todo + todo))
                labelsWithTodos.remove(labelWithTodo)
                break
            }
        }

        val todosWithLabelsIterator = todosWithLabels.iterator()
        while (todosWithLabelsIterator.hasNext()) {
            val todoWithLabel = todosWithLabelsIterator.next()
            if (todoWithLabel.todo == todo && todoWithLabel.labels.contains(label).not()) {
                todosWithLabels.add(todoWithLabel.copy(labels = todoWithLabel.labels + label))
                todosWithLabels.remove(todoWithLabel)
                break
            }
        }
    }

    override suspend fun insertLabel(label: Label) {
        labels.add(label)
    }

    override suspend fun insertDailyTodo(dailyTodo: DailyTodo) {
        dailyTodos.add(dailyTodo)
    }

    override suspend fun updateTodo(todo: Todo) {
        for (i in todos.indices) {
            if (todos[i].todoId == todo.todoId) {
                todos[i] = todo

                return
            }
        }
    }

    override suspend fun updateTodo(todo: Todo, labels: List<Label>) {
        for (i in todos.indices) {
            if (todos[i].todoId == todo.todoId) {
                todos[i] = todo

                break
            }
        }

        todosWithLabels.forEach {
            if (it.todo.todoId == todo.todoId) {
                todosWithLabels.add(TodoWithLabel(todo, labels))
                todosWithLabels.remove(it)
            }
        }

        for (i in 0 until labelsWithTodos.size) {
            val oldTodo = labelsWithTodos[i].todo.find {
                it.todoId == todo.todoId
            }
            val newTodos = labelsWithTodos[i].todo.toMutableList()

            newTodos.remove(oldTodo)
            newTodos.add(todo)

            labelsWithTodos[i] = labelsWithTodos[i].copy(todo = newTodos)
        }
    }

    override suspend fun updateLabel(label: Label) {
        labels.forEachIndexed { index, _ ->
            if (labels[index].labelId == label.labelId) {
                labels[index] = label

                return
            }
        }

        labelsWithTodos.forEach {
            if (it.label.labelId == label.labelId) {
                labelsWithTodos.add(it.copy(label = label))
                labelsWithTodos.remove(it)
            }
        }

        for (i in 0 until todosWithLabels.size) {
            val oldLabel = todosWithLabels[i].labels.find {
                it.labelId == label.labelId
            }
            val newLabels = todosWithLabels[i].labels.toMutableList()

            newLabels.remove(oldLabel)
            newLabels.add(label)

            todosWithLabels[i] = todosWithLabels[i].copy(labels = newLabels)
        }
    }

    override suspend fun updateDailyTodo(dailyTodo: DailyTodo) {
        val selectedDailyTodo =
            dailyTodos.find { it.date == dailyTodo.date && it.parentTodoId == dailyTodo.parentTodoId }
        val selectedIndex = dailyTodos.indexOf(selectedDailyTodo)

        dailyTodos[selectedIndex] = dailyTodo
    }

    override suspend fun deleteTodo(todo: Todo) {
        todos.remove(todo)

        todosWithLabels = todosWithLabels.filter {
            it.todo != todo
        }.toMutableList()

        for (i in 0 until labelsWithTodos.size) {
            if (labelsWithTodos[i].todo.contains(todo)) {
                labelsWithTodos[i] =
                    LabelWithTodo(labelsWithTodos[i].label, labelsWithTodos[i].todo - todo)
            }
        }
    }

    override suspend fun deleteSelectedTodo(todoId: Int, selectedDate: LocalDate) {
        throw Exception(ERROR_MSG)
    }

    override suspend fun deleteSelectedAndFutureTodo(todoId: Int, selectedDate: LocalDate) {
        throw Exception(ERROR_MSG)
    }

    override suspend fun deleteLabel(label: Label) {
        labels.remove(label)

        labelsWithTodos = labelsWithTodos.filter {
            it.label != label
        }.toMutableList()

        for (i in 0 until todosWithLabels.size) {
            if (todosWithLabels[i].labels.contains(label)) {
                todosWithLabels[i] =
                    TodoWithLabel(todosWithLabels[i].todo, todosWithLabels[i].labels - label)
            }
        }
    }

    companion object {
        private const val ERROR_MSG = "잘못된 호출입니다."
        private var INSTANCE: FakeTodoLabelRepository? = null

        fun getInstance(): FakeTodoLabelRepository =
            INSTANCE ?: FakeTodoLabelRepository().apply {
                INSTANCE = this
            }
    }
}
