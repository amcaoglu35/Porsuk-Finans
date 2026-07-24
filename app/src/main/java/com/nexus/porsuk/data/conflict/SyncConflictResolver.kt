package com.nexus.porsuk.data.conflict

import com.nexus.porsuk.domain.model.SyncConflictResolution
import com.nexus.porsuk.domain.model.SyncModuleType
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Porsuk Cloud Sync — Çakışma Çözücü Arayüzü (SyncConflictResolver)
 */
interface SyncConflictResolver {
    fun resolveConflict(
        module: SyncModuleType,
        localJson: String,
        cloudJson: String,
        localTimestamp: Long,
        cloudTimestamp: Long
    ): SyncConflictResolution
}

@Singleton
class LastWriteWinsResolver @Inject constructor() : SyncConflictResolver {
    override fun resolveConflict(
        module: SyncModuleType,
        localJson: String,
        cloudJson: String,
        localTimestamp: Long,
        cloudTimestamp: Long
    ): SyncConflictResolution {
        val winner = if (cloudTimestamp >= localTimestamp) "Cloud Version Applied" else "Local Version Retained"
        return SyncConflictResolution(
            module = module,
            localTimestamp = localTimestamp,
            cloudTimestamp = cloudTimestamp,
            resolutionStrategy = "Last Write Wins ($winner)"
        )
    }
}
