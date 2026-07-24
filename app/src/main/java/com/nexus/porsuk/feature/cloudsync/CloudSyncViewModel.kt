package com.nexus.porsuk.feature.cloudsync

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexus.porsuk.domain.model.CloudProviderType
import com.nexus.porsuk.domain.model.SyncModuleType
import com.nexus.porsuk.domain.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Porsuk Cloud Sync Platform — ViewModel
 *
 * 5 Bulut sağlayıcıyı, 13 senkronizasyon modülünü, kayıtlı cihaz oturumlarını ve yedekleme altyapısını yönetir.
 */
@HiltViewModel
class CloudSyncViewModel @Inject constructor(
    private val cloudRepository: CloudRepository,
    private val syncRepository: SyncRepository,
    private val backupRepository: BackupRepository,
    private val deviceRepository: DeviceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CloudSyncUiState())
    val uiState: StateFlow<CloudSyncUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun selectModule(module: SyncModuleType) {
        _uiState.update { it.copy(selectedModule = module) }
    }

    fun triggerSync() {
        viewModelScope.launch {
            syncRepository.triggerManualSync().collect { state ->
                _uiState.update { it.copy(syncStatus = state) }
            }
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            launch {
                cloudRepository.getActiveProvider().collect { provider ->
                    _uiState.update { it.copy(activeProvider = provider) }
                }
            }

            launch {
                syncRepository.getSyncStatus().collect { status ->
                    _uiState.update { it.copy(syncStatus = status, isLoading = false) }
                }
            }

            launch {
                deviceRepository.getRegisteredDevices().collect { list ->
                    _uiState.update { it.copy(devices = list) }
                }
            }

            launch {
                backupRepository.getAvailableBackups().collect { list ->
                    _uiState.update { it.copy(backups = list) }
                }
            }
        }
    }
}
