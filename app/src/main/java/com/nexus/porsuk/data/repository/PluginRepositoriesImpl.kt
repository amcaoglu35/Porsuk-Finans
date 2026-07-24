package com.nexus.porsuk.data.repository

import com.nexus.porsuk.domain.model.*
import com.nexus.porsuk.domain.repository.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PluginRepositoryImpl @Inject constructor() : PluginRepository {

    private val defaultInstalled = listOf(
        PluginItem(
            manifest = PluginManifest(
                pluginId = "com.nexus.porsuk.plugin.rsi_macd_super",
                pluginName = "RSI & MACD Super Indicator",
                version = "2.1.0",
                developerName = "Quantum Quant Lab",
                category = PluginCategory.TECHNICAL_INDICATOR,
                extensionPoint = ExtensionPoint.CHARTS,
                requiredPermissions = listOf(PluginPermission.MARKET_DATA_ACCESS)
            ),
            state = PluginState.ENABLED,
            isBuiltIn = true
        ),
        PluginItem(
            manifest = PluginManifest(
                pluginId = "com.nexus.porsuk.plugin.finnhub_live",
                pluginName = "Finnhub Institutional Streamer",
                version = "1.4.2",
                developerName = "Finnhub Dev Team",
                category = PluginCategory.MARKET_DATA_PROVIDER,
                extensionPoint = ExtensionPoint.MARKETS,
                requiredPermissions = listOf(PluginPermission.MARKET_DATA_ACCESS, PluginPermission.NETWORK_ACCESS)
            ),
            state = PluginState.ENABLED,
            isBuiltIn = true
        ),
        PluginItem(
            manifest = PluginManifest(
                pluginId = "com.nexus.porsuk.plugin.orakul_gpt4_financial",
                pluginName = "Orakul GPT-4 Financial Brain",
                version = "3.0.1",
                developerName = "OpenAI Ecosystem",
                category = PluginCategory.AI_PROVIDER,
                extensionPoint = ExtensionPoint.AI_WORKSPACE,
                requiredPermissions = listOf(PluginPermission.AI_ACCESS, PluginPermission.NETWORK_ACCESS)
            ),
            state = PluginState.ENABLED,
            isBuiltIn = true
        )
    )

    private val defaultAvailable = listOf(
        PluginItem(
            manifest = PluginManifest(
                pluginId = "com.nexus.porsuk.plugin.interactive_brokers",
                pluginName = "Interactive Brokers Direct Direct Connect",
                version = "1.0.0",
                developerName = "IBKR Official",
                category = PluginCategory.BROKER_PROVIDER,
                extensionPoint = ExtensionPoint.PORTFOLIO,
                requiredPermissions = listOf(PluginPermission.BROKER_ACCESS, PluginPermission.NETWORK_ACCESS)
            ),
            state = PluginState.INSTALLED
        ),
        PluginItem(
            manifest = PluginManifest(
                pluginId = "com.nexus.porsuk.plugin.kap_radar_plus",
                pluginName = "KAP Smart Money Tracker",
                version = "1.2.0",
                developerName = "BIST Alpha Group",
                category = PluginCategory.SCANNER_PLUGIN,
                extensionPoint = ExtensionPoint.DASHBOARD,
                requiredPermissions = listOf(PluginPermission.MARKET_DATA_ACCESS, PluginPermission.NOTIFICATION_ACCESS)
            ),
            state = PluginState.INSTALLED
        )
    )

    private val installedPluginsState = MutableStateFlow(defaultInstalled)
    private val availablePluginsState = MutableStateFlow(defaultAvailable)

    override fun getInstalledPlugins(): Flow<List<PluginItem>> = installedPluginsState.asStateFlow()

    override fun getAvailablePlugins(): Flow<List<PluginItem>> = availablePluginsState.asStateFlow()

    override suspend fun installPlugin(manifest: PluginManifest): Boolean {
        val newItem = PluginItem(manifest = manifest, state = PluginState.ENABLED)
        installedPluginsState.update { current -> current + newItem }
        availablePluginsState.update { current -> current.filter { it.manifest.pluginId != manifest.pluginId } }
        return true
    }

    override suspend fun uninstallPlugin(pluginId: String): Boolean {
        val removed = installedPluginsState.value.find { it.manifest.pluginId == pluginId } ?: return false
        installedPluginsState.update { current -> current.filter { it.manifest.pluginId != pluginId } }
        availablePluginsState.update { current -> current + removed.copy(state = PluginState.INSTALLED) }
        return true
    }

    override suspend fun setPluginState(pluginId: String, state: PluginState): Boolean {
        installedPluginsState.update { current ->
            current.map { item ->
                if (item.manifest.pluginId == pluginId) item.copy(state = state) else item
            }
        }
        return true
    }
}

@Singleton
class SDKRepositoryImpl @Inject constructor() : SDKRepository {
    override fun getSdkVersion(): String = "v3.9.0-SDK"

    override fun getSupportedExtensionPoints(): List<ExtensionPoint> = ExtensionPoint.entries.toList()

    override fun executePluginInSandbox(pluginId: String, actionName: String): PluginSandboxMetrics {
        return PluginSandboxMetrics(
            executionTimeMs = (8..25).random().toLong(),
            memoryFootprintMb = 1.6,
            isCrashIsolated = true
        )
    }
}

@Singleton
class PermissionRepositoryImpl @Inject constructor() : PermissionRepository {
    private val permissionsMap = mutableMapOf<String, MutableSet<PluginPermission>>()

    override fun getGrantedPermissions(pluginId: String): List<PluginPermission> {
        return permissionsMap[pluginId]?.toList() ?: listOf(PluginPermission.MARKET_DATA_ACCESS)
    }

    override suspend fun validatePluginPermissions(pluginId: String, required: List<PluginPermission>): Boolean {
        val granted = getGrantedPermissions(pluginId)
        return granted.containsAll(required)
    }

    override suspend fun grantPermission(pluginId: String, permission: PluginPermission) {
        val set = permissionsMap.getOrPut(pluginId) { mutableSetOf(PluginPermission.MARKET_DATA_ACCESS) }
        set.add(permission)
    }

    override suspend fun revokePermission(pluginId: String, permission: PluginPermission) {
        permissionsMap[pluginId]?.remove(permission)
    }
}

@Singleton
class ManifestRepositoryImpl @Inject constructor() : ManifestRepository {
    override suspend fun verifyManifestSignature(manifest: PluginManifest): Boolean {
        return manifest.signatureHash.contains("PORSUK_SIGNED")
    }

    override suspend fun parsePluginManifest(rawManifestJson: String): PluginManifest? {
        return PluginManifest(
            pluginId = "com.nexus.porsuk.plugin.parsed",
            pluginName = "Parsed Custom Extension"
        )
    }
}
