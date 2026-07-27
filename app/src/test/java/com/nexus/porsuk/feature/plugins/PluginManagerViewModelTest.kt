package com.nexus.porsuk.feature.plugins

import com.nexus.porsuk.domain.model.*
import com.nexus.porsuk.domain.repository.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

/**
 * Porsuk Plugin & Extension SDK Platform — ViewModel Unit Testleri
 */
@OptIn(ExperimentalCoroutinesApi::class)
class PluginManagerViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val fakePluginRepository = object : PluginRepository {
        override fun getInstalledPlugins() = flowOf(
            listOf(
                PluginItem(
                    manifest = PluginManifest(
                        pluginId = "test.plugin",
                        pluginName = "Test Plugin"
                    ),
                    state = PluginState.ENABLED
                )
            )
        )

        override fun getAvailablePlugins() = flowOf(emptyList<PluginItem>())

        override suspend fun installPlugin(manifest: PluginManifest) = true
        override suspend fun uninstallPlugin(pluginId: String) = true
        override suspend fun setPluginState(pluginId: String, state: PluginState) = true

        override fun getPluginHealth(pluginId: String) = flowOf<ApiHealthMetrics?>(null)
        override suspend fun updatePluginHealth(metrics: ApiHealthMetrics) {}
        override suspend fun getApiConfig(pluginId: String): ApiConfig? = null
        override suspend fun saveApiConfig(config: ApiConfig) {}
    }

    private val fakeSDKRepository = object : SDKRepository {
        override fun getSdkVersion() = "v3.9.0-SDK"
        override fun getSupportedExtensionPoints() = ExtensionPoint.entries.toList()
        override fun executePluginInSandbox(pluginId: String, actionName: String) = PluginSandboxMetrics(executionTimeMs = 15L)
    }

    private val fakePermissionRepository = object : PermissionRepository {
        override fun getGrantedPermissions(pluginId: String) = listOf(PluginPermission.MARKET_DATA_ACCESS)
        override suspend fun validatePluginPermissions(pluginId: String, required: List<PluginPermission>) = true
        override suspend fun grantPermission(pluginId: String, permission: PluginPermission) {}
        override suspend fun revokePermission(pluginId: String, permission: PluginPermission) {}
    }

    private val fakeManifestRepository = object : ManifestRepository {
        override suspend fun verifyManifestSignature(manifest: PluginManifest) = true
        override suspend fun parsePluginManifest(rawManifestJson: String) = null
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
    fun `loadPluginsData updates uiState with installed plugins and sdk version`() = runTest {
        val viewModel = PluginManagerViewModel(
            pluginRepository = fakePluginRepository,
            sdkRepository = fakeSDKRepository,
            permissionRepository = fakePermissionRepository,
            manifestRepository = fakeManifestRepository
        )

        testScheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("v3.9.0-SDK", state.sdkVersion)
        assertEquals(1, state.installedPlugins.size)
        assertEquals("Test Plugin", state.installedPlugins[0].manifest.pluginName)
        assertEquals(false, state.isLoading)
    }

    @Test
    fun `runPluginSandboxTest executes action and updates sandbox metrics`() = runTest {
        val viewModel = PluginManagerViewModel(
            pluginRepository = fakePluginRepository,
            sdkRepository = fakeSDKRepository,
            permissionRepository = fakePermissionRepository,
            manifestRepository = fakeManifestRepository
        )

        viewModel.runPluginSandboxTest("test.plugin")
        testScheduler.advanceUntilIdle()

        val metrics = viewModel.uiState.value.activeSandboxMetrics["test.plugin"]
        assertNotNull(metrics)
        assertEquals(15L, metrics?.executionTimeMs)
    }
}
