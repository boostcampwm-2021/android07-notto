package com.gojol.notto.model.datasource.todo

import com.gojol.notto.common.TodoSuccessType
import com.gojol.notto.model.data.RepeatType
import com.gojol.notto.model.database.label.Label
import com.gojol.notto.model.database.todo.Todo
import com.gojol.notto.model.database.todolabel.LabelWithTodo
import com.gojol.notto.model.database.todolabel.TodoWithLabel

class FakeTodoLabelRepository : TodoLabelDataSource {

    private var todos = mutableListOf(
        Todo(TodoSuccessType.NOTHING, "밥 굶지 않기", "1", false, RepeatType.DAY, false, "1:00", "2:00", "1:00", false, 0),
        Todo(TodoSuccessType.NOTHING, "과제 미루지 않기", "1", false, RepeatType.DAY, false, "1:00", "2:00", "1:00", false, 1),
        Todo(TodoSuccessType.NOTHING, "지각하지 않기", "1", false, RepeatType.DAY, false, "1:00", "2:00", "1:00", false, 2),
        Todo(TodoSuccessType.NOTHING, "밥 먹을 때 물 먹지 않기", "1", false, RepeatType.DAY, false, "1:00", "2:00", "1:00", false, 3),
        Todo(TodoSuccessType.NOTHING, "회의 지각 안하기", "1", false, RepeatType.DAY, false, "1:00", "2:00", "1:00", false, 4),
        Todo(TodoSuccessType.NOTHING, "핸드폰 보지 않기", "1", false, RepeatType.DAY, false, "1:00", "2:00", "1:00", false, 5),
        Todo(TodoSuccessType.NOTHING, "누워있지 않기", "1", false, RepeatType.DAY, false, "1:00", "2:00", "1:00", false, 6),
    )
    private var labels = mutableListOf(
        Label(1, "학교", 1),
        Label(2, "건강", 2),
        Label(3, "집", 3),
        Label(4, "과제", 4)
    )
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

    override suspend fun getLabelsWithTodos(): List<LabelWithTodo> {
        return labelsWithTodos
    }

    override suspend fun getAllTodo(): List<Todo> {
        return todos
    }

    override suspend fun getAllLabel(): List<Label> {
        return labels
    }

    override suspend fun insertTodo(todo: Todo) {
        todos.add(todo)
    }

    override suspend fun insertTodo(todo: Todo, label: Label) {
        labelsWithTodos.forEach {
            if (it.label == label && it.todo.contains(todo).not()) {
                labelsWithTodos.add(it.copy(todo = it.todo + todo))
                labelsWithTodos.remove(it)

                return@forEach
            }
        }

        todosWithLabels.forEach {
            if (it.todo == todo && it.labels.contains(label).not()) {
                todosWithLabels.add(it.copy(labels = it.labels + label))
                todosWithLabels.remove(it)

                return@forEach
            }
        }
    }

    override suspend fun insertLabel(label: Label) {
        labels.add(label)
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
}
