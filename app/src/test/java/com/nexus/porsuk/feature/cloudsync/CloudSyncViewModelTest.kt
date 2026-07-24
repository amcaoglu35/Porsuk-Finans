package com.nexus.porsuk.feature.cloudsync

import com.nexus.porsuk.domain.model.CloudBackupPayload
import com.nexus.porsuk.domain.model.CloudProviderType
import com.nexus.porsuk.domain.model.SyncStatusState
import com.nexus.porsuk.domain.model.UserDeviceSession
import com.nexus.porsuk.domain.repository.BackupRepository
import com.nexus.porsuk.domain.repository.CloudRepository
import com.nexus.porsuk.domain.repository.DeviceRepository
import com.nexus.porsuk.domain.repository.SyncRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * Porsuk Cloud Sync Platform — ViewModel Unit Testleri
 */
@OptIn(ExperimentalCoroutinesApi::class)
class CloudSyncViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val fakeCloudRepository = object : CloudRepository {
        override fun getActiveProvider() = flowOf(CloudProviderType.FIREBASE)
    }

    private val fakeSyncRepository = object : SyncRepository {
        override fun getSyncStatus() = flowOf(SyncStatusState.SYNCED)
        override suspend fun triggerManualSync() = flowOf(SyncStatusState.SYNCED)
    }

    private val fakeBackupRepository = object : BackupRepository {
        override fun getAvailableBackups() = flowOf(listOf(CloudBackupPayload(backupName = "Test Yedek")))
        override suspend fun createBackup(name: String) = flowOf(CloudBackupPayload(backupName = name))
    }

    private val fakeDeviceRepository = object : DeviceRepository {
        override fun getRegisteredDevices() = flowOf(listOf(UserDeviceSession(deviceName = "Test Device")))
        override suspend fun removeDevice(deviceId: String) {}
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadData updates uiState with active provider and devices`() = runTest {
        val viewModel = CloudSyncViewModel(
            cloudRepository = fakeCloudRepository,
            syncRepository = fakeSyncRepository,
            backupRepository = fakeBackupRepository,
            deviceRepository = fakeDeviceRepository
        )

        testScheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(CloudProviderType.FIREBASE, state.activeProvider)
        assertEquals(SyncStatusState.SYNCED, state.syncStatus)
        assertEquals(1, state.devices.size)
        assertEquals("Test Device", state.devices[0].deviceName)
        assertEquals(false, state.isLoading)
    }
}
