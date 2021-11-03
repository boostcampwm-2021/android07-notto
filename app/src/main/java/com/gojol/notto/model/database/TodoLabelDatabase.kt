package com.gojol.notto.model.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.gojol.notto.model.database.todolabel.TodoLabelDao
import com.gojol.notto.model.database.label.Label
import com.gojol.notto.model.database.todo.Todo
import com.gojol.notto.model.database.todolabel.TodoLabelCrossRef

@Database(entities = [Todo::class, Label::class, TodoLabelCrossRef::class], version = 1)
abstract class TodoLabelDatabase : RoomDatabase() {
    abstract fun todoLabelDao(): TodoLabelDao

    companion object {
        private var instance: TodoLabelDatabase? = null

        @Synchronized
        fun getInstance(context: Context): TodoLabelDatabase {
            return instance ?: synchronized(TodoLabelDatabase::class) {
                Room.databaseBuilder(
                    context.applicationContext,
                    TodoLabelDatabase::class.java,
                    "notto-database"
                ).build()
            }
        }
    }
}