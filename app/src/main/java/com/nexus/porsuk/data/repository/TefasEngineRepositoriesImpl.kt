package com.nexus.porsuk.data.repository

import com.nexus.porsuk.data.local.cache.CacheManager
import com.nexus.porsuk.data.local.dao.TefasFundDao
import com.nexus.porsuk.data.local.entity.TefasFundEntity
import com.nexus.porsuk.data.sync.TefasSyncReport
import com.nexus.porsuk.data.sync.TefasSyncService
import com.nexus.porsuk.domain.repository.TefasEngineFundRepository
import com.nexus.porsuk.domain.repository.TefasEngineSyncRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TefasEngineFundRepositoryImpl @Inject constructor(
    private val tefasFundDao: TefasFundDao,
    private val cacheManager: CacheManager
) : TefasEngineFundRepository {

    override fun getAllActiveFunds(): Flow<List<TefasFundEntity>> {
        return tefasFundDao.getAllActiveFunds()
    }

    override fun getFundByCode(code: String): Flow<TefasFundEntity?> {
        val cacheKey = "tefas_fund_$code"
        return tefasFundDao.getFundByCode(code).onEach { fund ->
            if (fund != null) {
                cacheManager.put(cacheKey, fund)
            }
        }
    }

    override fun getFundsByUmbrella(umbrellaFund: String): Flow<List<TefasFundEntity>> {
        return tefasFundDao.getFundsByUmbrella(umbrellaFund)
    }

    override fun getFundsByManager(manager: String): Flow<List<TefasFundEntity>> {
        return tefasFundDao.getFundsByManager(manager)
    }

    override fun searchFunds(query: String): Flow<List<TefasFundEntity>> {
        return tefasFundDao.searchFunds(query)
    }
}

@Singleton
class TefasEngineSyncRepositoryImpl @Inject constructor(
    private val syncService: TefasSyncService
) : TefasEngineSyncRepository {

    override suspend fun triggerFundSync(): TefasSyncReport {
        return syncService.syncAllFunds()
    }
}
