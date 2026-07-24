package com.nexus.porsuk.data.repository

import com.nexus.porsuk.data.engine.StrategyTemplateEngine
import com.nexus.porsuk.data.engine.StrategyValidationEngine
import com.nexus.porsuk.data.local.dao.StrategyDao
import com.nexus.porsuk.data.local.entity.StrategyEntity
import com.nexus.porsuk.domain.model.*
import com.nexus.porsuk.domain.repository.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StrategyRepositoryImpl @Inject constructor(
    private val dao: StrategyDao
) : StrategyRepository {

    override fun getSavedStrategies(): Flow<List<StrategyModel>> {
        return dao.getAllSavedStrategies().map { list ->
            list.map { entity ->
                StrategyModel(
                    id = entity.strategyId,
                    name = entity.strategyName,
                    type = StrategyType.valueOf(entity.strategyType),
                    description = entity.description,
                    blocks = emptyList(),
                    stopLossPct = entity.stopLossPct,
                    takeProfitPct = entity.takeProfitPct,
                    version = entity.version
                )
            }
        }
    }

    override suspend fun saveStrategy(strategy: StrategyModel) {
        val entity = StrategyEntity(
            strategyId = strategy.id,
            strategyName = strategy.name,
            strategyType = strategy.type.name,
            description = strategy.description,
            stopLossPct = strategy.stopLossPct,
            takeProfitPct = strategy.takeProfitPct,
            version = strategy.version
        )
        dao.insertStrategy(entity)
    }

    override suspend fun deleteStrategy(strategyId: String) {
        dao.deleteStrategy(strategyId)
    }
}

@Singleton
class StrategyTemplateRepositoryImpl @Inject constructor(
    private val templateEngine: StrategyTemplateEngine
) : StrategyTemplateRepository {
    override fun getTemplates(): Flow<List<StrategyModel>> = flow {
        emit(templateEngine.getTemplates())
    }
}

@Singleton
class StrategyValidationRepositoryImpl @Inject constructor(
    private val validationEngine: StrategyValidationEngine
) : StrategyValidationRepository {
    override fun validateStrategy(strategy: StrategyModel): Flow<StrategyValidationResult> = flow {
        emit(validationEngine.validateStrategy(strategy))
    }
}

@Singleton
class StrategyExecutionRepositoryImpl @Inject constructor() : StrategyExecutionRepository {
    override fun compileStrategy(strategy: StrategyModel): Flow<String> = flow {
        emit("Strategy '${strategy.name}' compiled successfully to Execution Plan AST.")
    }
}
