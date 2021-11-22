package com.gojol.notto.di

import android.app.AlarmManager
import android.content.Context
import com.gojol.notto.model.datasource.todo.TodoAlarmManager
import com.gojol.notto.model.datasource.todo.TodoAlarmManagerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class TodoAlarmModule {

    @Singleton
    @Provides
    fun provideAlarmManager(@ApplicationContext context: Context): AlarmManager {
        return context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    @Singleton
    @Provides
    fun provideTodoEditAlarmManager(
        @ApplicationContext context: Context,
        alarmManager: AlarmManager
    ): TodoAlarmManager {
        return TodoAlarmManagerImpl(context, alarmManager)
    }
}