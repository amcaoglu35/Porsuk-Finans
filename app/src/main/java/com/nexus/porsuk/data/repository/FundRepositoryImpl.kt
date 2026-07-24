package com.nexus.porsuk.data.repository

import com.nexus.porsuk.core.common.NetworkResult
import com.nexus.porsuk.data.local.cache.CacheManager
import com.nexus.porsuk.data.local.dao.FundDao
import com.nexus.porsuk.data.local.entity.FundEntity
import com.nexus.porsuk.data.logging.DataLogger
import com.nexus.porsuk.data.remote.datasource.TefasRemoteDataSource
import com.nexus.porsuk.data.validator.DataValidator
import com.nexus.porsuk.domain.repository.FundRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Porsuk Data Center — TEFAS Fonları Repository Somut Sınıfı (FundRepositoryImpl)
 */
@Singleton
class FundRepositoryImpl @Inject constructor(
    private val fundDao: FundDao,
    private val remoteDataSource: TefasRemoteDataSource,
    private val cacheManager: CacheManager,
    private val validator: DataValidator,
    private val logger: DataLogger
) : FundRepository {

    override fun getActiveFunds(): Flow<List<FundEntity>> {
        return fundDao.getAllFunds()
    }

    override suspend fun getFundByCode(code: String): FundEntity? {
        val cacheKey = "fund_$code"
        cacheManager.get<FundEntity>(cacheKey)?.let { return it }

        val fund = fundDao.getFundByCode(code).firstOrNull()
        if (fund != null) {
            cacheManager.put(cacheKey, fund)
        }
        return fund
    }

    override suspend fun syncFunds(): NetworkResult<Unit> {
        logger.logSyncEvent("FundRepository", "TEFAS fon senkronizasyonu başlatıldı...")
        return when (val result = remoteDataSource.fetchAllFunds()) {
            is NetworkResult.Success -> {
                val validatedList = validator.deduplicateFunds(result.data)
                fundDao.insertFunds(validatedList)
                logger.logSyncEvent("FundRepository", "${validatedList.size} TEFAS fonu güncellendi.")
                NetworkResult.Success(Unit)
            }
            is NetworkResult.Error -> {
                logger.logError("FundRepository", "TEFAS fon senkronizasyon hatası: ${result.message}")
                result
            }
            is NetworkResult.Exception -> {
                logger.logError("FundRepository", "TEFAS fon senkronizasyon istisnası", result.throwable)
                result
            }
            is NetworkResult.Loading -> NetworkResult.Loading
        }
    }

    override suspend fun markInactiveFunds(inactiveCodes: List<String>) {
        logger.logSyncEvent("FundRepository", "${inactiveCodes.size} TEFAS fonu pasif/tasfiye edildi.")
    }
}
