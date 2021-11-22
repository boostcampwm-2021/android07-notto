package com.gojol.notto.di

import com.gojol.notto.model.SharedPrefManager
import com.gojol.notto.model.datasource.option.OptionDataSource
import com.gojol.notto.model.datasource.option.OptionLocalDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
class OptionModule {

    @Provides
    fun provideOptionLocalDataSource(sharedPrefManager: SharedPrefManager): OptionDataSource {
        return OptionLocalDataSource(sharedPrefManager)
    }
}
