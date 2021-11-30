package com.example.nottokeyword

import android.content.Context
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
    fun provideKeywordDatabase(@ApplicationContext context: Context): KeywordDatabase {
        val database = Firebase.database(BuildConfig.FIREBASE_DB_URL)
            .apply { setPersistenceEnabled(true) }

        return KeywordDatabaseImpl(
            context,
            database.getReference("keywords")
        )
    }
}
