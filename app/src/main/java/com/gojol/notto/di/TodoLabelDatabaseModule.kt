package com.gojol.notto.di

import android.content.Context
import androidx.room.Room
import com.gojol.notto.model.database.TodoLabelDatabase
import com.gojol.notto.model.database.todolabel.TodoLabelDao
import com.gojol.notto.model.datasource.todo.TodoLabelDataSource
import com.gojol.notto.model.datasource.todo.TodoLabelLocalDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class TodoLabelDatabaseModule {

    @Singleton
    @Provides
    fun provideTodoLabelDatabase(@ApplicationContext context: Context): TodoLabelDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            TodoLabelDatabase::class.java,
            "notto-database"
        ).build()
    }

    @Provides
    fun provideTodoLabelDao(todoLabelDatabase: TodoLabelDatabase): TodoLabelDao {
        return todoLabelDatabase.todoLabelDao()
    }

    @Provides
    fun provideLocalDataSource(todoLabelDao: TodoLabelDao): TodoLabelDataSource {
        return TodoLabelLocalDataSource(todoLabelDao)
    }
}