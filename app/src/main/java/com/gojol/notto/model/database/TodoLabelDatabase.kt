package com.gojol.notto.model.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.gojol.notto.model.database.label.Label
import com.gojol.notto.model.database.todo.DailyTodo
import com.gojol.notto.model.database.todo.Keyword
import com.gojol.notto.model.database.todo.Todo
import com.gojol.notto.model.database.todolabel.TodoLabelCrossRef
import com.gojol.notto.model.database.todolabel.TodoLabelDao

@Database(entities = [Todo::class, Label::class, DailyTodo::class, Keyword::class, TodoLabelCrossRef::class], version = 1)
abstract class TodoLabelDatabase : RoomDatabase() {

    abstract fun todoLabelDao(): TodoLabelDao
}
