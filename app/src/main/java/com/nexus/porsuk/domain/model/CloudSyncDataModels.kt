package com.nexus.porsuk.domain.model

/**
 * Kayıtlı Cihaz ve Oturum Modeli (UserDeviceSession)
 */
data class UserDeviceSession(
    val deviceId: String = "dev_${System.currentTimeMillis()}",
    val deviceName: String,
    val deviceType: String = "Android Phone",
    val isCurrentDevice: Boolean = false,
    val lastSyncTimestamp: Long = System.currentTimeMillis()
)

/**
 * Bulut Yedekleme Nesnesi Modeli (CloudBackupPayload)
 */
data class CloudBackupPayload(
    val backupId: String = "backup_${System.currentTimeMillis()}",
    val backupName: String,
    val providerType: CloudProviderType = CloudProviderType.FIREBASE,
    val payloadSizeBytes: Long = 1450000L,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Veri Politikaları Modeli (SyncDataPolicy)
 */
data class SyncDataPolicy(
    val syncOnlyWifi: Boolean = true,
    val syncOnMobileData: Boolean = false,
    val batterySaverMode: Boolean = true,
    val roamingPolicy: Boolean = false,
    val backgroundRestrictions: Boolean = false
)

/**
 * Depolama Yönetimi Metrikleri (CloudStorageMetrics)
 */
data class CloudStorageMetrics(
    val cloudUsageBytes: Long = 24500000L,   // ~24.5 MB
    val localStorageBytes: Long = 48200000L,  // ~48.2 MB
    val cacheUsageBytes: Long = 12100000L,   // ~12.1 MB
    val storageLimitBytes: Long = 10737418240L // 10 GB
)

/**
 * Çakışma Çözüm Nesnesi (SyncConflictResolution)
 */
data class SyncConflictResolution(
    val conflictId: String = "conf_${System.currentTimeMillis()}",
    val module: SyncModuleType = SyncModuleType.PORTFOLIO,
    val localTimestamp: Long = System.currentTimeMillis() - 5000,
    val cloudTimestamp: Long = System.currentTimeMillis(),
    val resolutionStrategy: String = "Last Write Wins (Cloud Version Applied)"
)

/**
 * Geleceğe Hazır Cross-Platform Sync Stub Modeli
 */
data class CrossPlatformSyncStub(
    val connectedPlatformsCount: Int = 3,
    val isDesktopSynced: Boolean = true,
    val isWebSynced: Boolean = true,
    val lastGlobalSyncTimeText: String = "Bugün 13:25"
)
