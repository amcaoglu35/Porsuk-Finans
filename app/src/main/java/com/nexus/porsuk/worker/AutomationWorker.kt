package com.nexus.porsuk.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.nexus.porsuk.data.engine.AutomationEngine
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AutomationWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface AutomationWorkerEntryPoint {
        fun automationEngine(): AutomationEngine
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val entryPoint = EntryPointAccessors.fromApplication(
                applicationContext,
                AutomationWorkerEntryPoint::class.java
            )
            val engine = entryPoint.automationEngine()
            
            engine.runAutomations()
            
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    companion object {
        fun schedule(context: Context) {
            val request = androidx.work.PeriodicWorkRequestBuilder<AutomationWorker>(
                1, java.util.concurrent.TimeUnit.HOURS
            ).build()
            
            androidx.work.WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "AI_Automation_Periodic",
                androidx.work.ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }
    }
}
