package com.nexus.porsuk.domain.usecase.transcript

import com.nexus.porsuk.domain.model.*
import com.nexus.porsuk.domain.repository.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * 1. Earnings Call Toplantıları UseCase
 */
class GetEarningsCallsUseCase @Inject constructor(
    private val earningsCallRepository: EarningsCallRepository
) {
    fun getCalls(symbol: String): Flow<List<EarningsCallTranscript>> {
        return earningsCallRepository.getRecentEarningsCalls(symbol)
    }

    suspend fun getQna(callId: String): List<QnaExchange> {
        return earningsCallRepository.getQnaExchanges(callId)
    }
}

/**
 * 2. Transkript Detayı & Konuşmacı Akışı UseCase
 */
class GetTranscriptDetailUseCase @Inject constructor(
    private val transcriptRepository: TranscriptRepository,
    private val speakerRepository: SpeakerRepository
) {
    fun getUtterances(callId: String): Flow<List<TranscriptUtterance>> {
        return transcriptRepository.getTranscriptUtterances(callId)
    }

    suspend fun getVisuals(callId: String): TranscriptVisuals {
        return transcriptRepository.getTranscriptVisuals(callId)
    }
}

/**
 * 3. Yönetici Açıklamaları & Öngörüler UseCase
 */
class AnalyzeManagementStatementsUseCase @Inject constructor(
    private val transcriptRepository: TranscriptRepository
) {
    suspend fun execute(callId: String): ManagementAnalysis {
        return transcriptRepository.getManagementAnalysis(callId)
    }
}

/**
 * 4. Transkript İçi Arama Engine UseCase
 */
class SearchTranscriptsUseCase @Inject constructor(
    private val searchRepository: TranscriptSearchRepository
) {
    suspend fun search(query: String, symbol: String? = null): List<TranscriptSearchResult> {
        return searchRepository.searchTranscripts(query, symbol)
    }
}

/**
 * 5. AI Transkript Özeti & Sinyaller UseCase
 */
class GenerateTranscriptAiIntelligenceUseCase @Inject constructor(
    private val transcriptRepository: TranscriptRepository
) {
    suspend fun execute(callId: String): TranscriptAiSummary {
        return transcriptRepository.getTranscriptAiSummary(callId)
    }
}
