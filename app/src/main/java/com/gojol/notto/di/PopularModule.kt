package com.gojol.notto.di

import com.example.nottokeyword.FirebaseDB
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
class PopularModule {

    @Provides
    fun provideFirebaseDB(): FirebaseDB {
        return FirebaseDB()
    }
}
