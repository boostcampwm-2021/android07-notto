package com.gojol.notto.di

import android.content.Context
import com.gojol.notto.model.SharedPrefManager
import com.gojol.notto.model.SharedPrefManagerImpl
import com.gojol.notto.model.datasource.option.OptionDataSource
import com.gojol.notto.model.datasource.option.OptionLocalDataSource
import com.gojol.notto.model.datasource.option.OptionRemoteDataSource
import com.gojol.notto.ui.option.DayAlarmManager
import com.gojol.notto.ui.option.DayAlarmManagerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import com.gojol.notto.network.GithubService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class OptionModule {

    @Provides
    @Named("optionLocalDataSource")
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
    @Named("optionRemoteDataSource")
    fun provideOptionRemoteDataSource(githubService: GithubService): OptionDataSource {
        return OptionRemoteDataSource(githubService)
    }

    companion object {
        private const val BASE_URL = "https://api.github.com/"
    }
}
