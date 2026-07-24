package com.nexus.porsuk.data.remote.datasource

import com.nexus.porsuk.core.common.NetworkResult
import com.nexus.porsuk.data.remote.api.TefasApi
import com.nexus.porsuk.data.remote.dto.TefasFundDto
import javax.inject.Inject
import javax.inject.Singleton

/**
 * TEFAS Engine Uzak Veri Kaynağı Sözleşmesi
 */
interface TefasEngineRemoteDataSource {
    suspend fun fetchAllFunds(): NetworkResult<List<TefasFundDto>>
    suspend fun fetchFundDetail(fundCode: String): NetworkResult<TefasFundDto>
}

/**
 * TEFAS Engine Uzak Veri Kaynağı Somut Uygulaması
 */
@Singleton
class TefasEngineRemoteDataSourceImpl @Inject constructor(
    private val tefasApi: TefasApi
) : TefasEngineRemoteDataSource {

    override suspend fun fetchAllFunds(): NetworkResult<List<TefasFundDto>> {
        return try {
            val response = tefasApi.getAllFunds()
            NetworkResult.Success(response)
        } catch (e: Throwable) {
            NetworkResult.Exception(e)
        }
    }

    override suspend fun fetchFundDetail(fundCode: String): NetworkResult<TefasFundDto> {
        return try {
            val detail = tefasApi.getFundDetail(fundCode)
            NetworkResult.Success(detail)
        } catch (e: Throwable) {
            NetworkResult.Exception(e)
        }
    }
}
