package com.nexus.porsuk.feature.settings

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
 * Porsuk Settings Center — ViewModel Unit Testleri
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SettingsCenterViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val fakeSettingsRepository = object : SettingsRepository {
        override fun getAppVersion() = "v3.8.4 Test"
    }

    private val fakePreferencesRepository = object : PreferencesRepository {
        override fun getMarketAiPreferences() = flowOf(MarketAiPreferences())
        override suspend fun toggleHidePortfolioValue(hidden: Boolean) {}
    }

    private val fakeThemeRepository = object : ThemeRepository {
        override fun getThemeSettings() = flowOf(AppThemeSettings(themeMode = AppThemeMode.DARK))
        override suspend fun setThemeMode(mode: AppThemeMode) {}
    }

    private val fakeRegionRepository = object : RegionRepository {
        override fun getRegionPreferences() = flowOf(RegionPreferences(currency = DefaultCurrency.TRY))
        override suspend fun setLanguage(language: AppLanguage) {}
        override suspend fun setCurrency(currency: DefaultCurrency) {}
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
    fun `loadSettings updates uiState with dark theme and try currency`() = runTest {
        val viewModel = SettingsCenterViewModel(
            settingsRepository = fakeSettingsRepository,
            preferencesRepository = fakePreferencesRepository,
            themeRepository = fakeThemeRepository,
            regionRepository = fakeRegionRepository
        )

        testScheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(AppThemeMode.DARK, state.themeSettings.themeMode)
        assertEquals(DefaultCurrency.TRY, state.regionPreferences.currency)
        assertEquals("v3.8.4 Test", state.appVersion)
        assertEquals(false, state.isLoading)
    }
}
