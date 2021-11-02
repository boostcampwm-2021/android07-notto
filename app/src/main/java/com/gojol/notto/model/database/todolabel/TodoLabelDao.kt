package com.gojol.notto.model.database.todolabel

import androidx.room.*
import com.gojol.notto.model.database.label.Label
import com.gojol.notto.model.database.todo.Todo

@Dao
interface TodoLabelDao {
    @Transaction
    @Query("SELECT * FROM Todo")
    fun getTodoWithLabel(): List<TodoWithLabel>

    @Transaction
    @Query("SELECT * FROM Label")
    fun getLabelWithTodo(): List<LabelWithTodo>

    @Transaction
    @Query("SELECT * FROM Todo")
    fun getAllTodo(): List<Todo>

    @Transaction
    @Query("SELECT * FROM Label")
    fun getAllLabel(): List<Label>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertTodo(todo: Todo)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertLabel(label: Label)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(todoLabelCrossRef: TodoLabelCrossRef)

    @Update
    fun updateTodo(todo: Todo)

    @Update
    fun updateLabel(label: Label)

    @Update
    fun update(todoLabelCrossRef: TodoLabelCrossRef)

    @Delete
    fun deleteTodo(todo: Todo)

    @Delete
    fun deleteLabel(label: Label)

    @Delete
    fun delete(todoLabelCrossRef: TodoLabelCrossRef)
}