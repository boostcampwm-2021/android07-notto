package com.gojol.notto.model.datasource.todo

import com.gojol.notto.model.database.label.Label
import com.gojol.notto.model.database.todo.Todo
import com.gojol.notto.model.database.todolabel.LabelWithTodo
import com.gojol.notto.model.database.todolabel.TodoLabelCrossRef
import com.gojol.notto.model.database.todolabel.TodoLabelDao
import com.gojol.notto.model.database.todolabel.TodoWithLabel

class TodoLabelLocalDataSource(private val todoLabelDao: TodoLabelDao) : TodoLabelDataSource {

    override fun insertTodo(todo: Todo) {
        todoLabelDao.insertTodo(todo)
    }

    override fun insertTodo(todo: Todo, label: Label) {
        todoLabelDao.insert(TodoLabelCrossRef(todo.todoId, label.labelId))
    }

    override fun updateTodo(todo: Todo) {
        todoLabelDao.updateTodo(todo)
    }

    override fun updateTodo(todo: Todo, label: Label) {
        todoLabelDao.update(TodoLabelCrossRef(todo.todoId, label.labelId))
    }

    override fun deleteTodo(todo: Todo) {
        todoLabelDao.deleteTodo(todo)
    }

    override fun getTodoWithLabel(): List<TodoWithLabel> {
        return todoLabelDao.getTodoWithLabel()
    }

    override fun insertLabel(label: Label) {
        todoLabelDao.insertLabel(label)
    }

    override fun insertLabel(todo: Todo, label: Label) {
        todoLabelDao.insert(TodoLabelCrossRef(todo.todoId, label.labelId))
    }

    override fun updateLabel(label: Label) {
        todoLabelDao.updateLabel(label)
    }

    override fun deleteLabel(label: Label) {
        todoLabelDao.deleteLabel(label)
    }

    override fun getLabelWithTodo(): List<LabelWithTodo> {
        return todoLabelDao.getLabelWithTodo()
    }
}