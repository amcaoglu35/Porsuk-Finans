package com.nexus.porsuk.domain.repository

import com.nexus.porsuk.domain.model.*
import kotlinx.coroutines.flow.Flow

/**
 * 1. Hesaplayıcı Deposu Sözleşmesi (CalculatorRepository)
 */
interface CalculatorRepository {
    fun calculate(category: CalculatorCategory, inputs: Map<String, Double>): Flow<CalculationResult>
}

/**
 * 2. Senaryo Simülatörü Deposu Sözleşmesi (ScenarioRepository)
 */
interface ScenarioRepository {
    fun runMarketScenario(scenarioName: String): Flow<CalculationResult>
}

/**
 * 3. Değerleme Deposu Sözleşmesi (ValuationRepository)
 */
interface ValuationRepository {
    fun calculateGrahamValue(eps: Double, bvps: Double): Flow<CalculationResult>
}

/**
 * 4. Hesaplama Geçmişi Deposu Sözleşmesi (HistoryRepository)
 */
interface HistoryRepository {
    fun getCalculationHistory(): Flow<List<CalculationResult>>
    suspend fun saveCalculationHistory(result: CalculationResult)
}
