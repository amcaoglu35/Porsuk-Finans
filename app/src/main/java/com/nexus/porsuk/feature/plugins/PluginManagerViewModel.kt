package com.nexus.porsuk.feature.plugins

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexus.porsuk.domain.model.*
import com.nexus.porsuk.domain.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Porsuk Plugin & Extension SDK Platform — ViewModel
 *
 * Eklenti yükleme, etkinleştirme/devre dışı bırakma, izin yönetimi ve sandbox izole çalıştırmayı yönetir.
 */
@HiltViewModel
class PluginManagerViewModel @Inject constructor(
    private val pluginRepository: PluginRepository,
    private val sdkRepository: SDKRepository,
    private val permissionRepository: PermissionRepository,
    private val manifestRepository: ManifestRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PluginManagerUiState(sdkVersion = sdkRepository.getSdkVersion()))
    val uiState: StateFlow<PluginManagerUiState> = _uiState.asStateFlow()

    init {
        loadPluginsData()
    }

    fun selectCategoryFilter(category: PluginCategory?) {
        _uiState.update { it.copy(selectedCategoryFilter = category) }
    }

    fun togglePluginState(pluginId: String, enable: Boolean) {
        viewModelScope.launch {
            val newState = if (enable) PluginState.ENABLED else PluginState.DISABLED
            pluginRepository.setPluginState(pluginId, newState)
        }
    }

    fun installPlugin(manifest: PluginManifest) {
        viewModelScope.launch {
            if (manifestRepository.verifyManifestSignature(manifest)) {
                pluginRepository.installPlugin(manifest)
            } else {
                _uiState.update { it.copy(errorMessage = "Eklenti imzası doğrulanamadı!") }
            }
        }
    }

    fun uninstallPlugin(pluginId: String) {
        viewModelScope.launch {
            pluginRepository.uninstallPlugin(pluginId)
        }
    }

    fun runPluginSandboxTest(pluginId: String) {
        viewModelScope.launch {
            val metrics = sdkRepository.executePluginInSandbox(pluginId, "TEST_RUN")
            _uiState.update { current ->
                val updatedMetrics = current.activeSandboxMetrics.toMutableMap()
                updatedMetrics[pluginId] = metrics
                current.copy(activeSandboxMetrics = updatedMetrics)
            }
        }
    }

    private fun loadPluginsData() {
        viewModelScope.launch {
            launch {
                pluginRepository.getInstalledPlugins().collect { installed ->
                    _uiState.update { it.copy(installedPlugins = installed, isLoading = false) }
                }
            }

            launch {
                pluginRepository.getAvailablePlugins().collect { available ->
                    _uiState.update { it.copy(availablePlugins = available) }
                }
            }
        }
    }
}
