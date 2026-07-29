package com.nexus.porsuk.data.remote.datasource

import com.nexus.porsuk.core.common.NetworkResult
import com.nexus.porsuk.core.network.BaseRemoteDataSource
import com.nexus.porsuk.core.network.ErrorHandler
import com.nexus.porsuk.data.remote.api.FredApi
import com.nexus.porsuk.data.remote.api.FredResponseDto
import javax.inject.Inject
import javax.inject.Singleton

interface FredRemoteDataSource {
    suspend fun getObservations(seriesId: String): NetworkResult<FredResponseDto>
}

@Singleton
class FredRemoteDataSourceImpl @Inject constructor(
    private val fredApi: FredApi,
    errorHandler: ErrorHandler
) : BaseRemoteDataSource(errorHandler), FredRemoteDataSource {

    override suspend fun getObservations(seriesId: String): NetworkResult<FredResponseDto> {
        return safeApiCall { fredApi.getObservations(seriesId) }
    }
}
