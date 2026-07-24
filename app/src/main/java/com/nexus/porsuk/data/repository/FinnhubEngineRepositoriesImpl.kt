package com.nexus.porsuk.data.repository

import com.nexus.porsuk.core.common.NetworkResult
import com.nexus.porsuk.data.local.dao.CompanyDao
import com.nexus.porsuk.data.local.entity.CompanyEntity
import com.nexus.porsuk.data.remote.datasource.FinnhubMarketRemoteDataSource
import com.nexus.porsuk.data.sync.FinnhubCompanySyncService
import com.nexus.porsuk.data.sync.SyncReport
import com.nexus.porsuk.domain.model.ExchangeType
import com.nexus.porsuk.domain.model.MarketStatus
import com.nexus.porsuk.domain.repository.FinnhubCompanyRepository
import com.nexus.porsuk.domain.repository.FinnhubMarketRepository
import com.nexus.porsuk.domain.repository.FinnhubSyncRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FinnhubCompanyRepositoryImpl @Inject constructor(
    private val companyDao: CompanyDao
) : FinnhubCompanyRepository {

    override fun getCompaniesByExchange(exchange: ExchangeType): Flow<List<CompanyEntity>> {
        return companyDao.getAllCompanies().map { list ->
            list.filter { it.exchange.equals(exchange.name, ignoreCase = true) }
        }
    }

    override fun searchCompanies(query: String): Flow<List<CompanyEntity>> {
        return companyDao.searchCompanies(query)
    }

    override fun getCompanyBySymbol(symbol: String): Flow<CompanyEntity?> {
        return companyDao.getCompanyBySymbol(symbol)
    }
}

@Singleton
class FinnhubMarketRepositoryImpl @Inject constructor(
    private val remoteDataSource: FinnhubMarketRemoteDataSource
) : FinnhubMarketRepository {

    override suspend fun getMarketStatus(exchange: ExchangeType): NetworkResult<MarketStatus> {
        return when (val result = remoteDataSource.getMarketStatus(exchange.code)) {
            is NetworkResult.Success -> {
                val dto = result.data
                NetworkResult.Success(
                    MarketStatus(
                        exchange = exchange,
                        isOpen = dto.isOpen,
                        session = dto.session ?: "REGULAR",
                        timezone = dto.timezone ?: "UTC",
                        holiday = dto.holiday
                    )
                )
            }
            is NetworkResult.Error -> result
            is NetworkResult.Exception -> result
            is NetworkResult.Loading -> NetworkResult.Loading
        }
    }

    override fun getSupportedExchanges(): List<ExchangeType> {
        return listOf(
            ExchangeType.BIST,
            ExchangeType.NASDAQ,
            ExchangeType.NYSE,
            ExchangeType.LSE,
            ExchangeType.EURONEXT,
            ExchangeType.ETF
        )
    }
}

@Singleton
class FinnhubSyncRepositoryImpl @Inject constructor(
    private val syncService: FinnhubCompanySyncService
) : FinnhubSyncRepository {

    override suspend fun triggerFullSync(): SyncReport {
        return syncService.syncAllExchanges()
    }

    override suspend fun triggerExchangeSync(exchange: ExchangeType): SyncReport {
        return syncService.syncSingleExchange(exchange)
    }
}
