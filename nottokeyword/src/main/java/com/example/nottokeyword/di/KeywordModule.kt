package com.example.nottokeyword.di

import android.content.Context
import com.example.nottokeyword.BuildConfig
import com.example.nottokeyword.KeywordDatabase
import com.example.nottokeyword.KeywordDatabaseImpl
import com.example.nottokeyword.cache.CacheManager
import com.example.nottokeyword.cache.CacheManagerImpl
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
    fun provideCacheManager(@ApplicationContext context: Context): CacheManager {
        return CacheManagerImpl(context)
    }

    @Provides
    @Singleton
    fun provideKeywordDatabase(cacheManager: CacheManager): KeywordDatabase {
        val database = Firebase.database(BuildConfig.FIREBASE_DB_URL)
            .apply { setPersistenceEnabled(true) }

        return KeywordDatabaseImpl(
            database.getReference("keywords"),
            cacheManager
        )
    }
}
