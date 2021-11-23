package com.gojol.notto.model.database.todolabel

import androidx.room.*
import com.gojol.notto.model.database.label.Label
import com.gojol.notto.model.database.todo.DailyTodo
import com.gojol.notto.model.database.todo.Keyword
import com.gojol.notto.model.database.todo.Todo
import com.gojol.notto.model.database.todo.TodoWithDailyTodo
import com.gojol.notto.model.database.todo.TodoWithKeyword
import java.time.LocalDate

@Dao
interface TodoLabelDao {
    @Transaction
    @Query("SELECT * FROM Todo")
    suspend fun getTodosWithLabels(): List<TodoWithLabel>

    @Transaction
    @Query("SELECT * FROM Todo")
    suspend fun getTodosWithDailyTodos(): List<TodoWithDailyTodo>

    @Transaction
    @Query("SELECT * FROM Todo")
    suspend fun getTodosWithKeywords(): List<TodoWithKeyword>

    @Transaction
    @Query("SELECT * FROM Label")
    suspend fun getLabelsWithTodos(): List<LabelWithTodo>

    @Transaction
    @Query("SELECT * FROM DailyTodo")
    suspend fun getDailyTodosWithTodoAndLabel(): List<DailyTodoWithTodoAndLabel>

    @Transaction
    @Query("SELECT * FROM Todo")
    suspend fun getAllTodo(): List<Todo>

    @Transaction
    @Query("SELECT * FROM Label")
    suspend fun getAllLabel(): List<Label>

    @Transaction
    @Query("SELECT * FROM DailyTodo")
    suspend fun getAllDailyTodo(): List<DailyTodo>

    @Query("SELECT * FROM DailyTodo WHERE parent_todo_id=:parentTodoId")
    suspend fun getDailyTodosByParentTodoId(parentTodoId: Int): List<DailyTodo>

    @Query("SELECT * FROM Todo WHERE todoId=:id")
    suspend fun getTodoById(id: Int): Todo

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTodo(todo: Todo)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertLabel(label: Label)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDailyTodo(dailyTodo: DailyTodo)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertKeyword(keyword: Keyword)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(todoLabelCrossRef: TodoLabelCrossRef)

    @Update
    suspend fun updateTodo(todo: Todo)

    @Update
    suspend fun updateLabel(label: Label)

    @Update
    suspend fun updateDailyTodo(dailyTodo: DailyTodo)

    @Query("UPDATE DailyTodo SET is_active=:isActive WHERE parent_todo_id=:parentTodoId and date=:date")
    suspend fun updateDailyTodoIsActive(parentTodoId: Int, date: LocalDate, isActive: Boolean)

    @Update
    suspend fun update(todoLabelCrossRef: TodoLabelCrossRef)

    @Delete
    suspend fun deleteTodo(todo: Todo)

    @Delete
    suspend fun deleteLabel(label: Label)

    @Delete
    suspend fun deleteDailyTodo(dailyTodo: DailyTodo)

    @Query("DELETE FROM DailyTodo WHERE parent_todo_id=:todoId and date=:date")
    suspend fun deleteDailyTodoByTodoIdAndDate(todoId: Int, date: LocalDate)

    @Delete
    suspend fun deleteKeyword(keyword: Keyword)

    @Delete
    suspend fun delete(todoLabelCrossRef: TodoLabelCrossRef)

    @Query("DELETE FROM TodoLabelCrossRef WHERE labelId = :labelId")
    suspend fun deleteTodoLabelCrossRefByLabel(labelId: Int)

    @Query("DELETE FROM TodoLabelCrossRef WHERE todoId = :todoId")
    suspend fun deleteTodoLabelCrossRefByTodo(todoId: Int)
}
