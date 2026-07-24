package com.nexus.porsuk.feature.filings

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
 * Porsuk Regulatory Filings & Disclosure Intelligence Platform — ViewModel Unit Testleri
 */
@OptIn(ExperimentalCoroutinesApi::class)
class RegulatoryFilingViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val fakeFilingRepository = object : FilingRepository {
        override fun getLatestFilings() = flowOf(
            listOf(
                RegulatoryFiling(
                    filingId = "f1",
                    companySymbol = "THYAO.IS",
                    companyName = "THY",
                    title = "Test KAP Bildirimi"
                )
            )
        )
        override fun getFilingsByCompany(symbol: String) = flowOf(emptyList<RegulatoryFiling>())
        override suspend fun markAsDownloaded(filingId: String) {}
    }

    private val fakeDisclosureRepository = object : DisclosureRepository {
        override fun getMaterialEventDisclosures() = flowOf(emptyList<RegulatoryFiling>())
        override suspend fun getFilingAiSummary(filingId: String) = FilingAiSummary(filingId = filingId)
    }

    private val fakeDocumentRepository = object : DocumentRepository {
        override fun getCachedDocuments() = flowOf(emptyList<RegulatoryFiling>())
        override suspend fun downloadFilingPdf(filingId: String) = true
    }

    private val fakeClassificationRepository = object : ClassificationRepository {
        override fun getFilingsByCategory(category: FilingCategory) = flowOf(emptyList<RegulatoryFiling>())
        override fun getAvailableCategories() = FilingCategory.entries.toList()
    }

    private val fakeTimelineRepository = object : TimelineRepository {
        override fun getCompanyTimeline(symbol: String) = flowOf(listOf(CompanyTimelineEvent(companySymbol = symbol, title = "Test Event")))
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
    fun `loadFilingsData updates uiState with latest filings and company timeline`() = runTest {
        val viewModel = RegulatoryFilingViewModel(
            filingRepository = fakeFilingRepository,
            disclosureRepository = fakeDisclosureRepository,
            documentRepository = fakeDocumentRepository,
            classificationRepository = fakeClassificationRepository,
            timelineRepository = fakeTimelineRepository
        )

        testScheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(FilingProviderType.KAP_TURKEY, state.activeProvider)
        assertEquals(1, state.filings.size)
        assertEquals("Test KAP Bildirimi", state.filings[0].title)
        assertEquals(1, state.companyTimeline.size)
        assertEquals(false, state.isLoading)
    }

    @Test
    fun `loadAiSummaryForFiling fetches AI summary and updates selected filing`() = runTest {
        val viewModel = RegulatoryFilingViewModel(
            filingRepository = fakeFilingRepository,
            disclosureRepository = fakeDisclosureRepository,
            documentRepository = fakeDocumentRepository,
            classificationRepository = fakeClassificationRepository,
            timelineRepository = fakeTimelineRepository
        )

        val targetFiling = RegulatoryFiling(filingId = "f1", companySymbol = "THYAO.IS", companyName = "THY", title = "Test KAP")
        viewModel.loadAiSummaryForFiling(targetFiling)
        testScheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNotNull(state.selectedFilingForSummary)
        assertNotNull(state.activeAiSummary)
        assertEquals("f1", state.activeAiSummary?.filingId)
    }
}
