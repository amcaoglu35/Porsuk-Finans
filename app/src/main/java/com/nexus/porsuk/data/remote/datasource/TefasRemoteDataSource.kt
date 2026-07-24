package com.nexus.porsuk.data.remote.datasource

import com.nexus.porsuk.core.common.NetworkResult
import com.nexus.porsuk.data.local.entity.FundEntity
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Porsuk Data Center — TEFAS Fonları Uzak Veri Kaynağı Arayüzü
 */
interface TefasRemoteDataSource {
    suspend fun fetchAllFunds(): NetworkResult<List<FundEntity>>
    suspend fun fetchFundDetails(fundCode: String): NetworkResult<FundEntity?>
}

/**
 * TefasRemoteDataSource Somut Sınıfı (Architecture-Ready Stub)
 */
@Singleton
class TefasRemoteDataSourceImpl @Inject constructor() : TefasRemoteDataSource {

    override suspend fun fetchAllFunds(): NetworkResult<List<FundEntity>> {
        return NetworkResult.Success(emptyList())
    }

    override suspend fun fetchFundDetails(fundCode: String): NetworkResult<FundEntity?> {
        return NetworkResult.Success(null)
    }
}
