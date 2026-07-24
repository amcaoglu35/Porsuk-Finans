package com.nexus.porsuk.feature.api

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
 * Porsuk Enterprise API & Automation Platform — ViewModel Unit Testleri
 */
@OptIn(ExperimentalCoroutinesApi::class)
class EnterpriseApiViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val fakeApiRepository = object : EnterpriseApiRepository {
        override fun getSupportedProtocols() = ApiProtocolType.entries.toList()
        override fun getSupportedAuthMethods() = ApiAuthMethod.entries.toList()
    }

    private val fakeAuthenticationRepository = object : EnterpriseAuthenticationRepository {
        override fun getActiveApiKeys() = flowOf(listOf(ApiKeyItem(name = "Test Key")))
        override suspend fun createApiKey(name: String, scopes: List<String>) = ApiKeyItem(name = name)
        override suspend fun revokeApiKey(keyId: String) = true
    }

    private val fakeWebhookRepository = object : EnterpriseWebhookRepository {
        override fun getWebhookSubscriptions() = flowOf(listOf(WebhookSubscription(targetUrl = "https://test.com/wh")))
        override suspend fun registerWebhook(targetUrl: String, events: List<String>) = WebhookSubscription()
        override suspend fun testWebhookDelivery(webhookId: String) = true
    }

    private val fakeAutomationRepository = object : EnterpriseAutomationRepository {
        override fun getAutomationIntegrations() = flowOf(listOf(AutomationIntegration(providerName = "Zapier")))
    }

    private val fakeUsageRepository = object : EnterpriseUsageRepository {
        override fun getEndpointStatistics() = flowOf(listOf(EndpointStat(endpointPath = "/v1/test")))
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
    fun `loadApiPlatformData updates uiState with API keys, webhooks, and endpoint stats`() = runTest {
        val viewModel = EnterpriseApiViewModel(
            apiRepository = fakeApiRepository,
            authenticationRepository = fakeAuthenticationRepository,
            webhookRepository = fakeWebhookRepository,
            automationRepository = fakeAutomationRepository,
            usageRepository = fakeUsageRepository
        )

        testScheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(ApiProtocolType.REST, state.activeProtocol)
        assertEquals(1, state.apiKeys.size)
        assertEquals("Test Key", state.apiKeys[0].name)
        assertEquals(1, state.webhooks.size)
        assertEquals("https://test.com/wh", state.webhooks[0].targetUrl)
        assertEquals(1, state.automations.size)
        assertEquals(1, state.endpointStats.size)
        assertEquals(false, state.isLoading)
    }

    @Test
    fun `createNewApiKey calls repository and clears input`() = runTest {
        val viewModel = EnterpriseApiViewModel(
            apiRepository = fakeApiRepository,
            authenticationRepository = fakeAuthenticationRepository,
            webhookRepository = fakeWebhookRepository,
            automationRepository = fakeAutomationRepository,
            usageRepository = fakeUsageRepository
        )

        viewModel.onNewKeyNameInputChange("New Bot Key")
        viewModel.createNewApiKey()
        testScheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("", state.newKeyNameInput)
    }
}
