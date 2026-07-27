package com.nexus.porsuk.feature.plugins

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexus.porsuk.domain.model.*
import com.nexus.porsuk.domain.repository.PluginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PluginUiState(
    val installedPlugins: List<PluginItem> = emptyList(),
    val availablePlugins: List<PluginItem> = emptyList(),
    val selectedTab: Int = 0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class PluginViewModel @Inject constructor(
    private val repository: PluginRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PluginUiState())
    val uiState: StateFlow<PluginUiState> = _uiState.asStateFlow()

    init {
        loadPlugins()
    }

    private fun loadPlugins() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            combine(
                repository.getInstalledPlugins(),
                repository.getAvailablePlugins()
            ) { installed, available ->
                PluginUiState(
                    installedPlugins = installed,
                    availablePlugins = available,
                    isLoading = false,
                    selectedTab = _uiState.value.selectedTab
                )
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }

    fun selectTab(index: Int) {
        _uiState.update { it.copy(selectedTab = index) }
    }

    fun togglePlugin(pluginId: String, enable: Boolean) {
        viewModelScope.launch {
            repository.setPluginState(
                pluginId,
                if (enable) PluginState.ENABLED else PluginState.DISABLED
            )
        }
    }

    fun installPlugin(manifest: PluginManifest) {
        viewModelScope.launch {
            repository.installPlugin(manifest)
        }
    }

    fun uninstallPlugin(pluginId: String) {
        viewModelScope.launch {
            repository.uninstallPlugin(pluginId)
        }
    }

    fun testConnection(pluginId: String) {
        // Mock connection test logic
    }
}
