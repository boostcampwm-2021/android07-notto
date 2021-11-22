package com.gojol.notto.di

import android.content.Context
import com.gojol.notto.model.SharedPrefManager
import com.gojol.notto.model.SharedPrefManagerImpl
import com.gojol.notto.model.datasource.option.OptionDataSource
import com.gojol.notto.model.datasource.option.OptionLocalDataSource
import com.gojol.notto.ui.option.DayAlarmManager
import com.gojol.notto.ui.option.DayAlarmManagerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class OptionModule {

    @Provides
    fun provideOptionLocalDataSource(sharedPrefManager: SharedPrefManager): OptionDataSource {
        return OptionLocalDataSource(sharedPrefManager)
    }

    @Singleton
    @Provides
    fun provideSharedPrefManagerImpl(@ApplicationContext context: Context): SharedPrefManager {
        return SharedPrefManagerImpl(context)
    }

    @Singleton
    @Provides
    fun provideDayAlarmManagerImpl(@ApplicationContext context: Context): DayAlarmManager {
        return DayAlarmManagerImpl(context)
    }
}
