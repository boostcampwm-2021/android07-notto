package com.example.nottokeyword

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object KeywordModule {

    @Provides
    fun provideKeywordDatabase(): KeywordDatabase {
        return KeywordDatabaseImpl(
            Firebase.database(BuildConfig.FIREBASE_DB_URL).getReference("keywords")
        )
    }
}
