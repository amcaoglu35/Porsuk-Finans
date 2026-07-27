package com.nexus.porsuk.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.nexus.porsuk.domain.model.ReportFormat
import com.nexus.porsuk.domain.model.ReportType
import com.nexus.porsuk.domain.repository.ReportingRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ScheduledReportWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface ReportingWorkerEntryPoint {
        fun reportingRepository(): ReportingRepository
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val entryPoint = EntryPointAccessors.fromApplication(
                applicationContext,
                ReportingWorkerEntryPoint::class.java
            )
            val repository = entryPoint.reportingRepository()
            
            // Logic to fetch scheduled reports from database and generate them
            repository.generateReport(ReportType.PORTFOLIO, ReportFormat.PDF)
            
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}
