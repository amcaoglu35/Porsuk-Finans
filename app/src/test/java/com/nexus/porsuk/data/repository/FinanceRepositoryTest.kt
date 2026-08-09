package com.nexus.porsuk.data.repository

import com.google.common.truth.Truth.assertThat
import com.nexus.porsuk.core.common.PorsukEventBus
import com.nexus.porsuk.data.local.SettingsManager
import com.nexus.porsuk.data.local.dao.AssetDao
import com.nexus.porsuk.data.local.dao.TefasFundDao
import com.nexus.porsuk.data.local.entity.PriceSnapshot
import com.nexus.porsuk.data.remote.*
import com.nexus.porsuk.data.remote.api.NewsApi
import com.nexus.porsuk.data.remote.datasource.FredRemoteDataSource
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FinanceRepositoryTest {

    private val assetDao: AssetDao = mockk(relaxed = true)
    private val scraper: GoogleFinanceScraper = mockk()
    private val eventBus: PorsukEventBus = mockk(relaxed = true)
    private val finnhubService: FinnhubService = mockk()
    private val yahooService: YahooFinanceService = mockk()
    private val yahooPublicService: YahooFinancePublicService = mockk()
    private val fmpService: FinancialModelingPrepService = mockk()
    private val settingsManager: SettingsManager = mockk(relaxed = true)
    private val newsApi: NewsApi = mockk()
    private val fredRemoteDataSource: FredRemoteDataSource = mockk()
    private val tefasFundDao: TefasFundDao = mockk(relaxed = true)

    private lateinit var repository: FinanceRepository

    @Before
    fun setUp() {
        coEvery { tefasFundDao.getActiveFundCount() } returns 10 // Prevent init seeding
        repository = FinanceRepository(
            assetDao, scraper, eventBus, finnhubService, yahooService,
            yahooPublicService, fmpService, settingsManager, newsApi,
            fredRemoteDataSource, tefasFundDao
        )
    }

    @Test
    fun `refreshPrice returns success from FMP when available`() = runTest {
        // Arrange
        val symbol = "THYAO"
        val market = "BIST"
        val expectedSnapshot = PriceSnapshot(symbol = symbol, price = 300.0, changePercent = 2.0, interval = "DAY")
        coEvery { fmpService.fetchPrice(symbol, market) } returns ScrapeResult.Success(expectedSnapshot)

        // Act
        val result = repository.refreshPrice(symbol, market)

        // Assert
        assertThat(result).isInstanceOf(ScrapeResult.Success::class.java)
        assertThat((result as ScrapeResult.Success).data.price).isEqualTo(300.0)
        coVerify { fmpService.fetchPrice(symbol, market) }
        confirmVerified(fmpService)
    }

    @Test
    fun `refreshPrice falls back to Yahoo Public when FMP fails`() = runTest {
        // Arrange
        val symbol = "EREGL"
        val market = "BIST"
        val expectedSnapshot = PriceSnapshot(symbol = symbol, price = 50.0, changePercent = -1.0, interval = "DAY")
        coEvery { fmpService.fetchPrice(symbol, market) } returns ScrapeResult.Error("FMP Error")
        coEvery { yahooPublicService.fetchPrice(symbol, market) } returns ScrapeResult.Success(expectedSnapshot)

        // Act
        val result = repository.refreshPrice(symbol, market)

        // Assert
        assertThat(result).isInstanceOf(ScrapeResult.Success::class.java)
        assertThat((result as ScrapeResult.Success).data.price).isEqualTo(50.0)
        coVerify { fmpService.fetchPrice(symbol, market) }
        coVerify { yahooPublicService.fetchPrice(symbol, market) }
    }

    @Test
    fun `refreshPrice returns error when all services fail`() = runTest {
        // Arrange
        val symbol = "ASDF"
        val market = "BIST"
        coEvery { fmpService.fetchPrice(symbol, market) } returns ScrapeResult.Error("FMP Fail")
        coEvery { yahooPublicService.fetchPrice(symbol, market) } returns ScrapeResult.Error("Yahoo Fail")
        coEvery { scraper.fetchPrice(symbol, market) } returns ScrapeResult.Error("Google Fail")

        // Act
        val result = repository.refreshPrice(symbol, market)

        // Assert
        assertThat(result).isInstanceOf(ScrapeResult.Error::class.java)
        assertThat((result as ScrapeResult.Error).message).isEqualTo("Google Fail")
    }
}
