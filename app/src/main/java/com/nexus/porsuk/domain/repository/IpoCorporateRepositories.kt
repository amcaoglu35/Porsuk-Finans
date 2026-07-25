package com.nexus.porsuk.domain.repository

import com.nexus.porsuk.core.common.NetworkResult
import com.nexus.porsuk.domain.model.*
import kotlinx.coroutines.flow.Flow

interface IpoRepository {
    fun getAllIpos(): Flow<List<IpoIntelligence>>
    fun getIposByStatus(status: IpoStatus): Flow<List<IpoIntelligence>>
    fun getIpoDetail(symbol: String): Flow<IpoIntelligence?>
    
    suspend fun syncIpos(): NetworkResult<Unit>
    suspend fun getIpoAiSummary(symbol: String): IpoAiSummary
}

interface CorporateActionRepository {
    fun getActionsForSymbol(symbol: String): Flow<List<CorporateAction>>
    fun getActionsByType(type: CorporateActionType): Flow<List<CorporateAction>>
    
    suspend fun syncCorporateActions(symbol: String): NetworkResult<Unit>
}

interface DividendRepositoryPro {
    fun getDividendHistory(symbol: String): Flow<List<DividendHistoryItem>>
    fun getDividendAnalytics(symbol: String): Flow<DividendAnalytics?>
    
    suspend fun syncDividendData(symbol: String): NetworkResult<Unit>
}

interface CorporateCalendarRepository {
    fun getCorporateEvents(startDate: Long, endDate: Long): Flow<List<CorporateCalendarEvent>>
    fun getUpcomingIpos(): Flow<List<CorporateCalendarEvent>>
    fun getUpcomingDividends(): Flow<List<CorporateCalendarEvent>>
}
