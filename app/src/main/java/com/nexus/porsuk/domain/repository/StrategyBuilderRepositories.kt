package com.nexus.porsuk.domain.repository

import com.nexus.porsuk.domain.model.*
import kotlinx.coroutines.flow.Flow

/**
 * 1. Strateji Deposu Sözleşmesi (StrategyRepository)
 */
interface StrategyRepository {
    fun getSavedStrategies(): Flow<List<StrategyModel>>
    suspend fun saveStrategy(strategy: StrategyModel)
    suspend fun deleteStrategy(strategyId: String)
}

/**
 * 2. Strateji Şablonları Deposu Sözleşmesi (StrategyTemplateRepository)
 */
interface StrategyTemplateRepository {
    fun getTemplates(): Flow<List<StrategyModel>>
}

/**
 * 3. Strateji Doğrulama Deposu Sözleşmesi (StrategyValidationRepository)
 */
interface StrategyValidationRepository {
    fun validateStrategy(strategy: StrategyModel): Flow<StrategyValidationResult>
}

/**
 * 4. Strateji Yürütme ve Derleme Deposu Sözleşmesi (StrategyExecutionRepository)
 */
interface StrategyExecutionRepository {
    fun compileStrategy(strategy: StrategyModel): Flow<String>
}
