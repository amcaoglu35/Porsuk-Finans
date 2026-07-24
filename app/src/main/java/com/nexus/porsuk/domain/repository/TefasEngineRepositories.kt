package com.nexus.porsuk.domain.repository

import com.nexus.porsuk.data.local.entity.TefasFundEntity
import com.nexus.porsuk.data.sync.TefasSyncReport
import kotlinx.coroutines.flow.Flow

/**
 * Porsuk TEFAS Engine — Fon Deposu Domain Sözleşmesi
 */
interface TefasEngineFundRepository {
    fun getAllActiveFunds(): Flow<List<TefasFundEntity>>
    fun getFundByCode(code: String): Flow<TefasFundEntity?>
    fun getFundsByUmbrella(umbrellaFund: String): Flow<List<TefasFundEntity>>
    fun getFundsByManager(manager: String): Flow<List<TefasFundEntity>>
    fun searchFunds(query: String): Flow<List<TefasFundEntity>>
}

/**
 * Porsuk TEFAS Engine — Senkronizasyon Domain Sözleşmesi
 */
interface TefasEngineSyncRepository {
    suspend fun triggerFundSync(): TefasSyncReport
}
