package com.nexus.porsuk

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * Porsuk Finans Uygulaması — Hilt Application Sınıfı
 */
@HiltAndroidApp
class PorsukApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var priceAlertManager: com.nexus.porsuk.worker.PriceAlertManager

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        // Initialize Core Infrastructure
        com.nexus.porsuk.core.common.GlobalCrashHandler.initialize(this)

        // Ensure PriceAlertManager is started
        priceAlertManager.hashCode() 

        // Schedule System Workers
        com.nexus.porsuk.worker.AutomationWorker.schedule(this)
        com.nexus.porsuk.worker.DailySummaryWorker.schedule(this)
    }
}
