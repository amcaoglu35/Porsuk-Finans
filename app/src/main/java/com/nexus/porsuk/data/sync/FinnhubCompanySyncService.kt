package com.nexus.porsuk.data.sync

import com.nexus.porsuk.core.common.NetworkResult
import com.nexus.porsuk.data.local.dao.CompanyDao
import com.nexus.porsuk.data.local.entity.CompanyEntity
import com.nexus.porsuk.data.logging.DataLogger
import com.nexus.porsuk.data.remote.datasource.FinnhubMarketRemoteDataSource
import com.nexus.porsuk.domain.model.ExchangeType
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Porsuk Finnhub Engine — Şirket Senkronizasyon Servisi (Company Sync Service)
 *
 * BIST, NASDAQ ve NYSE borsalarındaki şirket künyelerini indirir, artımlı (incremental)
 * olarak veritabanını günceller, delist olanları pasife çeker ve SyncReport oluşturur.
 */
@Singleton
class FinnhubCompanySyncService @Inject constructor(
    private val remoteDataSource: FinnhubMarketRemoteDataSource,
    private val companyDao: CompanyDao,
    private val logger: DataLogger
) {

    /**
     * Tüm desteklenen borsaların (BIST, NASDAQ, NYSE) şirketlerini indirir ve senkronize eder.
     */
    suspend fun syncAllExchanges(): SyncReport {
        val startTime = System.currentTimeMillis()
        var totalDownloaded = 0
        var totalAdded = 0
        var totalUpdated = 0
        var totalDelisted = 0

        val targetExchanges = listOf(ExchangeType.BIST, ExchangeType.NASDAQ, ExchangeType.NYSE)

        for (exchange in targetExchanges) {
            val report = syncSingleExchange(exchange)
            totalDownloaded += report.totalDownloaded
            totalAdded += report.totalAdded
            totalUpdated += report.totalUpdated
            totalDelisted += report.totalDelisted
        }

        val totalDuration = System.currentTimeMillis() - startTime
        val finalReport = SyncReport(
            totalDownloaded = totalDownloaded,
            totalAdded = totalAdded,
            totalUpdated = totalUpdated,
            totalDelisted = totalDelisted,
            durationMs = totalDuration,
            isSuccess = true
        )

        logger.logSyncEvent("FinnhubCompanySyncService", finalReport.toFormattedLog())
        return finalReport
    }

    /**
     * Belirli bir borsanın şirketlerini artımlı (Incremental) olarak senkronize eder.
     */
    suspend fun syncSingleExchange(exchange: ExchangeType): SyncReport {
        val startTime = System.currentTimeMillis()

        return when (val result = remoteDataSource.getSymbolsForExchange(exchange.code)) {
            is NetworkResult.Success -> {
                val remoteSymbols = result.data
                val existingCompanies = companyDao.getAllCompanies().first().associateBy { it.symbol }

                val newEntities = mutableListOf<CompanyEntity>()
                val updateEntities = mutableListOf<CompanyEntity>()
                val currentRemoteSymbolsSet = remoteSymbols.map { it.symbol }.toSet()

                remoteSymbols.forEach { dto ->
                    val symbolStr = dto.symbol
                    val existing = existingCompanies[symbolStr]

                    val mappedEntity = CompanyEntity(
                        id = existing?.id ?: 0,
                        symbol = symbolStr,
                        isin = dto.isin ?: existing?.isin,
                        companyName = dto.description ?: symbolStr,
                        exchange = exchange.name,
                        country = exchange.defaultCountry,
                        currency = dto.currency ?: exchange.defaultCurrency,
                        sector = "Genel",
                        industry = dto.type ?: "Common Stock",
                        isActive = true,
                        lastUpdated = System.currentTimeMillis()
                    )

                    if (existing == null) {
                        newEntities.add(mappedEntity)
                    } else {
                        updateEntities.add(mappedEntity)
                    }
                }

                // Veritabanına ekle ve güncelle
                if (newEntities.isNotEmpty()) companyDao.insertCompanies(newEntities)
                if (updateEntities.isNotEmpty()) companyDao.insertCompanies(updateEntities)

                // Delist / Silinen şirketleri bul ve pasifleştir
                val delistedEntities = existingCompanies.values
                    .filter { it.exchange == exchange.name && !currentRemoteSymbolsSet.contains(it.symbol) }
                    .map { it.copy(isActive = false) }

                if (delistedEntities.isNotEmpty()) {
                    companyDao.insertCompanies(delistedEntities)
                }

                val duration = System.currentTimeMillis() - startTime
                SyncReport(
                    totalDownloaded = remoteSymbols.size,
                    totalAdded = newEntities.size,
                    totalUpdated = updateEntities.size,
                    totalDelisted = delistedEntities.size,
                    durationMs = duration,
                    isSuccess = true
                )
            }
            is NetworkResult.Error -> {
                SyncReport(isSuccess = false, errorMessage = result.message)
            }
            is NetworkResult.Exception -> {
                SyncReport(isSuccess = false, errorMessage = result.throwable.localizedMessage)
            }
            is NetworkResult.Loading -> SyncReport(isSuccess = false, errorMessage = "Loading state")
        }
    }
}
