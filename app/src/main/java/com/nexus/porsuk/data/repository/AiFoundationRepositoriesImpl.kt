package com.nexus.porsuk.data.repository

import com.nexus.porsuk.data.ai.*
import com.nexus.porsuk.domain.model.*
import com.nexus.porsuk.domain.repository.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AIRepositoryImpl @Inject constructor(
    private val geminiProvider: GeminiProviderStrategy,
    private val contextEngine: ContextEngine
) : AIRepository {

    override fun generateCompletion(prompt: String, symbol: String?): Flow<String> = flow {
        val ctx = contextEngine.buildCurrentContext(symbol)
        emit(geminiProvider.generateCompletion(prompt, ctx))
    }
}

@Singleton
class PromptRepositoryImpl @Inject constructor(
    private val promptEngine: PromptEngine
) : PromptRepository {
    override fun getPromptTemplates(): Flow<List<AiPromptTemplate>> = flow {
        emit(promptEngine.getDefaultTemplates())
    }
}

@Singleton
class ContextRepositoryImpl @Inject constructor(
    private val contextEngine: ContextEngine
) : ContextRepository {
    override fun getCurrentContext(symbol: String?): Flow<AiContextFrame> = flow {
        emit(contextEngine.buildCurrentContext(symbol))
    }
}

@Singleton
class MemoryRepositoryImpl @Inject constructor(
    private val memoryEngine: MemoryEngine
) : MemoryRepository {
    override fun getSessionMemory(sessionId: String): Flow<AiMemorySession> = flow {
        emit(memoryEngine.getOrCreateSession(sessionId))
    }
}

@Singleton
class ProviderRepositoryImpl @Inject constructor(
    private val geminiProvider: GeminiProviderStrategy,
    private val costEngine: AiCostManagerEngine
) : ProviderRepository {

    override fun getActiveProvider(): Flow<AiProviderType> = flow {
        emit(geminiProvider.getProviderType())
    }

    override fun getCostSummary(): Flow<AiCostSummary> = flow {
        emit(costEngine.getCostSummary())
    }
}
