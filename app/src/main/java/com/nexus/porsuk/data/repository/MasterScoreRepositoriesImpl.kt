package com.nexus.porsuk.data.repository

import com.nexus.porsuk.data.engine.MasterScoreCalculatorEngine
import com.nexus.porsuk.data.local.dao.MasterScoreDao
import com.nexus.porsuk.data.local.entity.MasterScoreHistoryEntity
import com.nexus.porsuk.domain.model.*
import com.nexus.porsuk.domain.repository.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MasterScoreRepositoryImpl @Inject constructor(
    private val calculatorEngine: MasterScoreCalculatorEngine
) : MasterScoreRepository {
    override fun getMasterScore(symbol: String): Flow<MasterScoreResult> = flow {
        emit(calculatorEngine.calculateMasterScore(symbol))
    }
}

@Singleton
class ScoreHistoryRepositoryImpl @Inject constructor(
    private val dao: MasterScoreDao
) : ScoreHistoryRepository {

    override fun getScoreHistory(symbol: String): Flow<List<MasterScoreHistoryItem>> {
        return dao.getScoreHistoryForSymbol(symbol).map { list ->
            list.map {
                MasterScoreHistoryItem(
                    scoreId = it.scoreId,
                    symbol = it.symbol,
                    masterScore = it.masterScore,
                    level = try { ScoreLevel.valueOf(it.scoreLevel) } catch (e: Exception) { ScoreLevel.NEUTRAL },
                    timestamp = it.timestamp
                )
            }
        }
    }

    override suspend fun saveScoreHistory(symbol: String, masterScore: Int, level: ScoreLevel) {
        val entity = MasterScoreHistoryEntity(
            symbol = symbol,
            masterScore = masterScore,
            scoreLevel = level.name
        )
        dao.insertScoreHistory(entity)
    }
}

@Singleton
class ScoreCalculationRepositoryImpl @Inject constructor(
    private val calculatorEngine: MasterScoreCalculatorEngine
) : ScoreCalculationRepository {

    override suspend fun calculateScore(symbol: String): MasterScoreResult {
        return calculatorEngine.calculateMasterScore(symbol)
    }

    override suspend fun updateComponentWeights(weights: Map<ScoreComponentType, Double>) {
        calculatorEngine.updateWeights(weights)
    }
}
