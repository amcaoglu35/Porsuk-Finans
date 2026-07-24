package com.nexus.porsuk.domain.repository

import com.nexus.porsuk.domain.model.*
import kotlinx.coroutines.flow.Flow

/**
 * 1. Bulut Sağlayıcı Deposu Sözleşmesi (CloudRepository)
 */
interface CloudRepository {
    fun getActiveProvider(): Flow<CloudProviderType>
}

/**
 * 2. Senkronizasyon Deposu Sözleşmesi (SyncRepository)
 */
interface SyncRepository {
    fun getSyncStatus(): Flow<SyncStatusState>
    suspend fun triggerManualSync(): Flow<SyncStatusState>
}

/**
 * 3. Yedekleme Deposu Sözleşmesi (BackupRepository)
 */
interface BackupRepository {
    fun getAvailableBackups(): Flow<List<CloudBackupPayload>>
    suspend fun createBackup(name: String): Flow<CloudBackupPayload>
}

/**
 * 4. Cihaz Deposu Sözleşmesi (DeviceRepository)
 */
interface DeviceRepository {
    fun getRegisteredDevices(): Flow<List<UserDeviceSession>>
    suspend fun removeDevice(deviceId: String)
}
