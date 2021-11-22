package com.gojol.notto.di

import com.example.nottokeyword.FirebaseDB
import com.gojol.notto.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class PopularModule {

    @Singleton
    @Provides
    fun provideFirebaseDB(): FirebaseDB {
        return FirebaseDB(BuildConfig.FIREBASE_DB_URL)
    }
}
