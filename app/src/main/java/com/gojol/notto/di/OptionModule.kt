package com.gojol.notto.di

import android.content.Context
import com.gojol.notto.model.SharedPrefManager
import com.gojol.notto.model.SharedPrefManagerImpl
import com.gojol.notto.model.datasource.option.OptionLocalDataSource
import com.gojol.notto.model.datasource.option.OptionLocalDataSourceImpl
import com.gojol.notto.model.datasource.option.OptionRemoteDataSource
import com.gojol.notto.model.datasource.option.OptionRemoteDataSourceImpl
import com.gojol.notto.model.datasource.option.OptionRepository
import com.gojol.notto.model.datasource.option.OptionRepositoryImpl
import com.gojol.notto.network.GithubService
import com.gojol.notto.ui.option.DayAlarmManager
import com.gojol.notto.ui.option.DayAlarmManagerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class OptionModule {

    @Singleton
    @Provides
    fun provideSharedPrefManagerImpl(@ApplicationContext context: Context): SharedPrefManager {
        return SharedPrefManagerImpl(context)
    }

    @Provides
    fun provideOptionLocalDataSource(sharedPrefManager: SharedPrefManager): OptionLocalDataSource {
        return OptionLocalDataSourceImpl(sharedPrefManager)
    }

    @Singleton
    @Provides
    fun provideGithubService(): GithubService {

        val logger = HttpLoggingInterceptor()
        logger.level = HttpLoggingInterceptor.Level.BASIC

        val client = OkHttpClient.Builder()
            .addInterceptor(logger)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GithubService::class.java)
    }

    @Provides
    fun provideOptionRemoteDataSource(githubService: GithubService): OptionRemoteDataSource {
        return OptionRemoteDataSourceImpl(githubService)
    }

    @Singleton
    @Provides
    fun provideOptionRepository(
        optionLocalDataSource: OptionLocalDataSource,
        optionRemoteDataSource: OptionRemoteDataSource
    ): OptionRepository {
        return OptionRepositoryImpl(optionLocalDataSource, optionRemoteDataSource)
    }

    @Singleton
    @Provides
    fun provideDayAlarmManagerImpl(@ApplicationContext context: Context): DayAlarmManager {
        return DayAlarmManagerImpl(context)
    }

    companion object {
        private const val BASE_URL = "https://api.github.com/"
    }
}
