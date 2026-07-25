package com.nexus.porsuk.domain.usecase.ipo

import com.nexus.porsuk.domain.model.IpoAiSummary
import com.nexus.porsuk.domain.model.IpoIntelligence
import com.nexus.porsuk.domain.repository.IpoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetFullIpoIntelligenceUseCase @Inject constructor(
    private val repository: IpoRepository
) {
    operator fun invoke(symbol: String): Flow<FullIpoIntelligence> {
        return combine(
            repository.getIpoDetail(symbol),
            kotlinx.coroutines.flow.flow { emit(repository.getIpoAiSummary(symbol)) }
        ) { info, aiSummary ->
            FullIpoIntelligence(info, aiSummary)
        }
    }
}

data class FullIpoIntelligence(
    val info: IpoIntelligence?,
    val aiSummary: IpoAiSummary
)
