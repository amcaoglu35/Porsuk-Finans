package com.nexus.porsuk.feature.security

import com.nexus.porsuk.domain.model.*
import com.nexus.porsuk.domain.repository.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * Porsuk Security Center — ViewModel Unit Testleri
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SecurityCenterViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val fakeSecurityRepository = object : SecurityRepository {
        override fun getSecurityMetrics() = flowOf(SecurityScoreMetrics(score = 98))
    }

    private val fakePrivacyRepository = object : PrivacyRepository {
        override fun getPrivacyConsents() = flowOf(PrivacyConsentModel())
        override suspend fun updateConsent(consent: PrivacyConsentModel) {}
    }

    private val fakeAuthenticationRepository = object : AuthenticationRepository {
        override fun isBiometricsEnabled() = flowOf(true)
        override suspend fun setBiometricsEnabled(enabled: Boolean) {}
    }

    private val fakeAuditRepository = object : AuditRepository {
        override fun getAuditLogs() = flowOf(listOf(SecurityAuditLog(title = "Test Log")))
        override suspend fun logSecurityEvent(title: String, description: String, category: AuditCategory) {}
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
    fun `loadData updates uiState with security score and audit logs`() = runTest {
        val viewModel = SecurityCenterViewModel(
            securityRepository = fakeSecurityRepository,
            privacyRepository = fakePrivacyRepository,
            authenticationRepository = fakeAuthenticationRepository,
            auditRepository = fakeAuditRepository
        )

        testScheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(98, state.metrics.score)
        assertEquals(1, state.auditLogs.size)
        assertEquals("Test Log", state.auditLogs[0].title)
        assertEquals(false, state.isLoading)
    }
}
