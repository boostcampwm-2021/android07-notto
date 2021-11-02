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
abstract class TodoDatabase : RoomDatabase() {
    abstract fun todoLabelDao(): TodoLabelDao

    companion object {
        private var instance: TodoDatabase? = null

        @Synchronized
        fun getInstance(context: Context): TodoDatabase? {
            if (instance == null) {
                synchronized(TodoDatabase::class) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        TodoDatabase::class.java,
                        "notto-database"
                    ).build()
                }
            }
            return instance
        }
    }
}