package com.gojol.notto

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.facebook.stetho.Stetho
import com.gojol.notto.util.AddDailyTodoWorker
import com.gojol.notto.util.UPDATE_TOMORROW_DAILY_TODO
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class NottoApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()

        Stetho.initializeWithDefaults(this)

        val dailyWorkRequest =
            PeriodicWorkRequestBuilder<AddDailyTodoWorker>(1, TimeUnit.HOURS)
                .addTag(UPDATE_TOMORROW_DAILY_TODO)
                .build()

        val workManager = WorkManager.getInstance(this)
        workManager.enqueueUniquePeriodicWork(
            UPDATE_TOMORROW_DAILY_TODO,
            ExistingPeriodicWorkPolicy.KEEP,
            dailyWorkRequest
        )
    }

    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}
