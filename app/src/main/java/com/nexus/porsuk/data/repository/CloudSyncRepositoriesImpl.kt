package com.nexus.porsuk.data.repository

import com.nexus.porsuk.data.local.dao.CloudSyncDao
import com.nexus.porsuk.data.local.entity.UserDeviceEntity
import com.nexus.porsuk.data.provider.FirebaseCloudSyncProvider
import com.nexus.porsuk.domain.model.*
import com.nexus.porsuk.domain.repository.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CloudRepositoryImpl @Inject constructor(
    private val firebaseProvider: FirebaseCloudSyncProvider
) : CloudRepository {
    override fun getActiveProvider(): Flow<CloudProviderType> = flow {
        emit(firebaseProvider.getProviderType())
    }
}

@Singleton
class SyncRepositoryImpl @Inject constructor() : SyncRepository {
    override fun getSyncStatus(): Flow<SyncStatusState> = flow {
        emit(SyncStatusState.SYNCED)
    }

    override suspend fun triggerManualSync(): Flow<SyncStatusState> = flow {
        emit(SyncStatusState.SYNCING)
        emit(SyncStatusState.SYNCED)
    }
}

@Singleton
class BackupRepositoryImpl @Inject constructor() : BackupRepository {
    override fun getAvailableBackups(): Flow<List<CloudBackupPayload>> = flow {
        emit(
            listOf(
                CloudBackupPayload(backupName = "Tam Portföy & Alarm Yedeklemesi"),
                CloudBackupPayload(backupName = "Otomatik Haftalık Bulut Yedeği")
            )
        )
    }

    override suspend fun createBackup(name: String): Flow<CloudBackupPayload> = flow {
        emit(CloudBackupPayload(backupName = name))
    }
}

@Singleton
class DeviceRepositoryImpl @Inject constructor(
    private val dao: CloudSyncDao
) : DeviceRepository {

    override fun getRegisteredDevices(): Flow<List<UserDeviceSession>> {
        return dao.getAllDevices().map { list ->
            if (list.isEmpty()) {
                listOf(
                    UserDeviceSession(deviceName = "Bu Cihaz (Samsung Galaxy S24)", isCurrentDevice = true),
                    UserDeviceSession(deviceName = "iPad Pro 12.9", deviceType = "Tablet"),
                    UserDeviceSession(deviceName = "MacBook Pro M3", deviceType = "Desktop")
                )
            } else {
                list.map { entity ->
                    UserDeviceSession(
                        deviceId = entity.deviceId,
                        deviceName = entity.deviceName,
                        deviceType = entity.deviceType,
                        isCurrentDevice = entity.isCurrent,
                        lastSyncTimestamp = entity.lastSyncTimestamp
                    )
                }
            }
        }
    }

    override suspend fun removeDevice(deviceId: String) {
        // Cihaz kaldırma mantığı
    }
}
