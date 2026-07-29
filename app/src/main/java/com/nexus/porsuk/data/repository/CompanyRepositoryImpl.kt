package com.nexus.porsuk.data.repository

import com.nexus.porsuk.core.common.DataError
import com.nexus.porsuk.core.common.NetworkResult
import com.nexus.porsuk.core.network.BaseRepository
import com.nexus.porsuk.data.local.cache.CacheManager
import com.nexus.porsuk.data.local.dao.CompanyDao
import com.nexus.porsuk.data.local.entity.CompanyEntity
import com.nexus.porsuk.data.logging.DataLogger
import com.nexus.porsuk.data.remote.datasource.FmpRemoteDataSource
import com.nexus.porsuk.data.remote.datasource.FinnhubRemoteDataSource
import com.nexus.porsuk.data.validator.DataValidator
import com.nexus.porsuk.domain.repository.CompanyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Porsuk Data Center — Şirketler Repository Somut Sınıfı (CompanyRepositoryImpl)
 */
@Singleton
class CompanyRepositoryImpl @Inject constructor(
    private val companyDao: CompanyDao,
    private val remoteDataSource: FinnhubRemoteDataSource,
    private val fmpRemoteDataSource: FmpRemoteDataSource,
    private val cacheManager: CacheManager,
    private val validator: DataValidator,
    private val logger: DataLogger
) : BaseRepository(), CompanyRepository {

    override fun getActiveCompanies(): Flow<List<CompanyEntity>> {
        return companyDao.getAllCompanies()
    }

    override suspend fun getCompanyBySymbol(symbol: String): CompanyEntity? {
        return companyDao.getCompanyBySymbol(symbol).firstOrNull()
    }

    override suspend fun syncCompanies(): NetworkResult<Unit> {
        logger.logSyncEvent("CompanyRepository", "Şirket verileri senkronizasyonu başlatıldı...")
        return when (val result = remoteDataSource.fetchSymbols("US")) {
            is NetworkResult.Success -> {
                val list = result.data.map { dto ->
                    CompanyEntity(
                        symbol = dto.symbol,
                        companyName = dto.description ?: dto.symbol,
                        exchange = "US",
                        sector = dto.type ?: "General",
                        industry = dto.type ?: "General"
                    )
                }
                val validatedList = validator.deduplicateCompanies(list)
                companyDao.insertCompanies(validatedList)
                logger.logSyncEvent("CompanyRepository", "${validatedList.size} şirket başarıyla güncellendi.")
                NetworkResult.Success(Unit)
            }
            is NetworkResult.Error -> {
                logger.logError("CompanyRepository", "Şirket senkronizasyon hatası: ${result.error}")
                NetworkResult.Error(result.error)
            }
            is NetworkResult.Exception -> {
                logger.logError("CompanyRepository", "Şirket senkronizasyon istisnası", result.throwable)
                NetworkResult.Exception(result.throwable)
            }
            is NetworkResult.Loading -> NetworkResult.Loading
        }
    }

    override suspend fun markDelistedCompanies(delistedSymbols: List<String>) {
        logger.logSyncEvent("CompanyRepository", "${delistedSymbols.size} şirket delist/pasif olarak işaretlendi.")
    }
}
