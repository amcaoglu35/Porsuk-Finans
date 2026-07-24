package com.nexus.porsuk.feature.cloudsync

import com.nexus.porsuk.domain.model.*

/**
 * Porsuk Cloud Sync Platform — UI Ekran Durumu (CloudSyncUiState)
 */
data class CloudSyncUiState(
    val activeProvider: CloudProviderType = CloudProviderType.FIREBASE,
    val syncStatus: SyncStatusState = SyncStatusState.SYNCED,
    val devices: List<UserDeviceSession> = emptyList(),
    val backups: List<CloudBackupPayload> = emptyList(),
    val selectedModule: SyncModuleType = SyncModuleType.PORTFOLIO,
    val dataPolicy: SyncDataPolicy = SyncDataPolicy(),
    val storageMetrics: CloudStorageMetrics = CloudStorageMetrics(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
