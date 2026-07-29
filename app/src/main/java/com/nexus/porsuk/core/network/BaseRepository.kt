package com.nexus.porsuk.core.network

import com.nexus.porsuk.core.common.NetworkResult
import kotlinx.coroutines.flow.*

abstract class BaseRepository {

    protected fun <ResultType, RequestType> networkBoundResource(
        query: () -> Flow<ResultType>,
        fetch: suspend () -> NetworkResult<RequestType>,
        saveFetchResult: suspend (RequestType) -> Unit,
        shouldFetch: (ResultType) -> Boolean = { true }
    ): Flow<NetworkResult<ResultType>> = flow {
        emit(NetworkResult.Loading)
        
        val cachedData = query().first()

        if (shouldFetch(cachedData)) {
            emit(NetworkResult.Loading)
            when (val fetchResult = fetch()) {
                is NetworkResult.Success -> {
                    saveFetchResult(fetchResult.data)
                    emitAll(query().map { NetworkResult.Success(it) })
                }
                is NetworkResult.Error -> {
                    emitAll(query().map { NetworkResult.Error(fetchResult.error, fetchResult.message) })
                }
                is NetworkResult.Exception -> {
                    emitAll(query().map { NetworkResult.Exception(fetchResult.throwable) })
                }
                else -> {
                    emitAll(query().map { NetworkResult.Success(it) })
                }
            }
        } else {
            emitAll(query().map { NetworkResult.Success(it) })
        }
    }
}
