package com.example.nottokeyword

import android.content.Context
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(ViewModelComponent::class)
object KeywordModule {

    @Provides
    fun provideKeywordDatabase(@ApplicationContext context: Context): KeywordDatabase {
        return KeywordDatabaseImpl(
            context,
            Firebase.database(BuildConfig.FIREBASE_DB_URL).getReference("keywords")
        )
    }
}
