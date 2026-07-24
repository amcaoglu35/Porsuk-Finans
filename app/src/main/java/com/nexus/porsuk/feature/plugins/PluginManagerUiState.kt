package com.nexus.porsuk.feature.plugins

import com.nexus.porsuk.domain.model.*

/**
 * Porsuk Plugin & Extension SDK Platform — UI Ekran Durumu (PluginManagerUiState)
 */
data class PluginManagerUiState(
    val installedPlugins: List<PluginItem> = emptyList(),
    val availablePlugins: List<PluginItem> = emptyList(),
    val sdkVersion: String = "v3.9.0-SDK",
    val extensionPoints: List<ExtensionPoint> = ExtensionPoint.entries.toList(),
    val selectedCategoryFilter: PluginCategory? = null,
    val marketplaceStub: PluginMarketplaceStub = PluginMarketplaceStub(),
    val activeSandboxMetrics: Map<String, PluginSandboxMetrics> = emptyMap(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
