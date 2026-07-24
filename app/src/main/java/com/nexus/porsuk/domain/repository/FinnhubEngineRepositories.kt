package com.nexus.porsuk.domain.repository

import com.nexus.porsuk.core.common.NetworkResult
import com.nexus.porsuk.data.local.entity.CompanyEntity
import com.nexus.porsuk.data.sync.SyncReport
import com.nexus.porsuk.domain.model.ExchangeType
import com.nexus.porsuk.domain.model.MarketStatus
import kotlinx.coroutines.flow.Flow

/**
 * Finnhub Market Data Engine — Şirket Veri Deposu Sözleşmesi
 */
interface FinnhubCompanyRepository {
    fun getCompaniesByExchange(exchange: ExchangeType): Flow<List<CompanyEntity>>
    fun searchCompanies(query: String): Flow<List<CompanyEntity>>
    fun getCompanyBySymbol(symbol: String): Flow<CompanyEntity?>
}

/**
 * Finnhub Market Data Engine — Piyasa Durumu Sözleşmesi
 */
interface FinnhubMarketRepository {
    suspend fun getMarketStatus(exchange: ExchangeType): NetworkResult<MarketStatus>
    fun getSupportedExchanges(): List<ExchangeType>
}

/**
 * Finnhub Market Data Engine — Senkronizasyon ve Raporlama Sözleşmesi
 */
interface FinnhubSyncRepository {
    suspend fun triggerFullSync(): SyncReport
    suspend fun triggerExchangeSync(exchange: ExchangeType): SyncReport
}
