package com.gojol.notto.model.database.todolabel

import androidx.room.*
import com.gojol.notto.model.database.label.Label
import com.gojol.notto.model.database.todo.Todo

@Dao
interface TodoLabelDao {
    @Transaction
    @Query("SELECT * FROM Todo")
    suspend fun getTodoWithLabel(): List<TodoWithLabel>

    @Transaction
    @Query("SELECT * FROM Label")
    suspend fun getLabelWithTodo(): List<LabelWithTodo>

    @Transaction
    @Query("SELECT * FROM Todo")
    suspend fun getAllTodo(): List<Todo>

    @Transaction
    @Query("SELECT * FROM Label")
    suspend fun getAllLabel(): List<Label>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTodo(todo: Todo)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertLabel(label: Label)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(todoLabelCrossRef: TodoLabelCrossRef)

    @Update
    suspend fun updateTodo(todo: Todo)

    @Update
    suspend fun updateLabel(label: Label)

    @Update
    suspend fun update(todoLabelCrossRef: TodoLabelCrossRef)

    @Delete
    suspend fun deleteTodo(todo: Todo)

    @Delete
    suspend fun deleteLabel(label: Label)

    @Delete
    suspend fun delete(todoLabelCrossRef: TodoLabelCrossRef)

    @Query("DELETE FROM TodoLabelCrossRef WHERE labelId = :labelId")
    suspend fun deleteTodoLabelCrossRefByLabel(labelId: Int)

    @Query("DELETE FROM TodoLabelCrossRef WHERE todoId = :todoId")
    suspend fun deleteTodoLabelCrossRefByTodo(todoId: Int)
}