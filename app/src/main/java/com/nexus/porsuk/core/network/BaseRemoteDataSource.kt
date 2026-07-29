package com.nexus.porsuk.core.network

import com.nexus.porsuk.core.common.NetworkResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class BaseRemoteDataSource @Inject constructor(
    private val errorHandler: ErrorHandler
) {
    protected suspend fun <T> safeApiCall(apiCall: suspend () -> T): NetworkResult<T> {
        return try {
            NetworkResult.Success(apiCall())
        } catch (throwable: Throwable) {
            errorHandler.handleError(throwable)
        }
    }
}
