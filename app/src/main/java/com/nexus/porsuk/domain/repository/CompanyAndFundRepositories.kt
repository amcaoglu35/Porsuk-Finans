package com.nexus.porsuk.domain.repository

import com.nexus.porsuk.core.common.NetworkResult
import com.nexus.porsuk.data.local.entity.CompanyEntity
import com.nexus.porsuk.data.local.entity.FundEntity
import kotlinx.coroutines.flow.Flow

/**
 * Porsuk Data Center — Şirketler / Hisse Senetleri Repository Arayüzü (Domain Contract)
 */
interface CompanyRepository {
    fun getActiveCompanies(): Flow<List<CompanyEntity>>
    suspend fun getCompanyBySymbol(symbol: String): CompanyEntity?
    suspend fun syncCompanies(): NetworkResult<Unit>
    suspend fun markDelistedCompanies(delistedSymbols: List<String>)
}

/**
 * Porsuk Data Center — TEFAS Yatırım Fonları Repository Arayüzü (Domain Contract)
 */
interface FundRepository {
    fun getActiveFunds(): Flow<List<FundEntity>>
    suspend fun getFundByCode(code: String): FundEntity?
    suspend fun syncFunds(): NetworkResult<Unit>
    suspend fun markInactiveFunds(inactiveCodes: List<String>)
}
