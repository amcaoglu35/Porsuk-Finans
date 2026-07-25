package com.nexus.porsuk.domain.repository

import com.nexus.porsuk.domain.model.*
import kotlinx.coroutines.flow.Flow

/**
 * 1. Transkript Deposu Sözleşmesi (TranscriptRepository)
 */
interface TranscriptRepository {
    fun getTranscript(callId: String): Flow<EarningsCallTranscript?>
    fun getTranscriptUtterances(callId: String): Flow<List<TranscriptUtterance>>
    suspend fun getManagementAnalysis(callId: String): ManagementAnalysis
    suspend fun getTranscriptAiSummary(callId: String): TranscriptAiSummary
    suspend fun getTranscriptVisuals(callId: String): TranscriptVisuals
}

/**
 * 2. Earnings Call Toplantı Deposu Sözleşmesi (EarningsCallRepository)
 */
interface EarningsCallRepository {
    fun getRecentEarningsCalls(symbol: String): Flow<List<EarningsCallTranscript>>
    fun getCallsByType(symbol: String, type: EarningsCallType): Flow<List<EarningsCallTranscript>>
    suspend fun getQnaExchanges(callId: String): List<QnaExchange>
}

/**
 * 3. Konuşmacı Deposu Sözleşmesi (SpeakerRepository)
 */
interface SpeakerRepository {
    fun getSpeakersForCall(callId: String): Flow<List<SpeakerInfo>>
    suspend fun getSpeakerTalkTimePct(callId: String): Map<String, Double>
}

/**
 * 4. Transkript Arama Deposu Sözleşmesi (TranscriptSearchRepository)
 */
interface TranscriptSearchRepository {
    suspend fun searchTranscripts(query: String, symbol: String? = null): List<TranscriptSearchResult>
    suspend fun searchByTopic(topic: String): List<TranscriptSearchResult>
    fun getFutureStubs(): Flow<TranscriptFutureStubs>
}
