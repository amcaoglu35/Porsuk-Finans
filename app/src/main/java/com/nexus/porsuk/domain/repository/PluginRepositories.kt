package com.nexus.porsuk.domain.repository

import com.nexus.porsuk.domain.model.*
import kotlinx.coroutines.flow.Flow

/**
 * 1. Eklenti Yönetim Deposu Sözleşmesi (PluginRepository)
 */
interface PluginRepository {
    fun getInstalledPlugins(): Flow<List<PluginItem>>
    fun getAvailablePlugins(): Flow<List<PluginItem>>
    suspend fun installPlugin(manifest: PluginManifest): Boolean
    suspend fun uninstallPlugin(pluginId: String): Boolean
    suspend fun setPluginState(pluginId: String, state: PluginState): Boolean
    
    // API Sağlık ve Yapılandırma
    fun getPluginHealth(pluginId: String): Flow<ApiHealthMetrics?>
    suspend fun updatePluginHealth(metrics: ApiHealthMetrics)
    suspend fun getApiConfig(pluginId: String): ApiConfig?
    suspend fun saveApiConfig(config: ApiConfig)
}

/**
 * 2. Eklenti SDK Deposu Sözleşmesi (SDKRepository)
 */
interface SDKRepository {
    fun getSdkVersion(): String
    fun getSupportedExtensionPoints(): List<ExtensionPoint>
    fun executePluginInSandbox(pluginId: String, actionName: String): PluginSandboxMetrics
}

/**
 * 3. Eklenti İzin ve Güvenlik Deposu Sözleşmesi (PermissionRepository)
 */
interface PermissionRepository {
    fun getGrantedPermissions(pluginId: String): List<PluginPermission>
    suspend fun validatePluginPermissions(pluginId: String, required: List<PluginPermission>): Boolean
    suspend fun grantPermission(pluginId: String, permission: PluginPermission)
    suspend fun revokePermission(pluginId: String, permission: PluginPermission)
}

/**
 * 4. Eklenti Manifest Deposu Sözleşmesi (ManifestRepository)
 */
interface ManifestRepository {
    suspend fun verifyManifestSignature(manifest: PluginManifest): Boolean
    suspend fun parsePluginManifest(rawManifestJson: String): PluginManifest?
}
