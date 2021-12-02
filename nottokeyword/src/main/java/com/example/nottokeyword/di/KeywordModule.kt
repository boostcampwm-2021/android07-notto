package com.example.nottokeyword.di

import android.content.Context
import com.example.nottokeyword.BuildConfig
import com.example.nottokeyword.KeywordRepository
import com.example.nottokeyword.KeywordRepositoryImpl
import com.example.nottokeyword.datasource.local.KeywordLocalDataSource
import com.example.nottokeyword.datasource.local.KeywordLocalDataSourceImpl
import com.example.nottokeyword.datasource.remote.KeywordRemoteDataSource
import com.example.nottokeyword.datasource.remote.KeywordRemoteDataSourceImpl
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object KeywordModule {

    @Provides
    @Singleton
    fun provideKeywordLocalDataSource(@ApplicationContext context: Context): KeywordLocalDataSource {
        return KeywordLocalDataSourceImpl(context)
    }

    @Provides
    @Singleton
    fun provideKeywordRemoteDataSource(): KeywordRemoteDataSource {
        val database = Firebase.database(BuildConfig.FIREBASE_DB_URL)
            .apply { setPersistenceEnabled(true) }

        return KeywordRemoteDataSourceImpl(
            database.getReference("keywords")
        )
    }

    @Provides
    @Singleton
    fun provideKeywordRepository(
        keywordRemoteDataSource: KeywordRemoteDataSource,
        keywordLocalDataSource: KeywordLocalDataSource
    ): KeywordRepository {
        return KeywordRepositoryImpl(keywordRemoteDataSource, keywordLocalDataSource)
    }
}
