package com.nexus.porsuk.data.repository

import com.nexus.porsuk.data.engine.FinancialCalculatorFactory
import com.nexus.porsuk.data.local.dao.CalculationHistoryDao
import com.nexus.porsuk.data.local.entity.CalculationHistoryEntity
import com.nexus.porsuk.domain.model.*
import com.nexus.porsuk.domain.repository.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CalculatorRepositoryImpl @Inject constructor(
    private val calculatorFactory: FinancialCalculatorFactory
) : CalculatorRepository {

    override fun calculate(category: CalculatorCategory, inputs: Map<String, Double>): Flow<CalculationResult> = flow {
        val strategy = calculatorFactory.getCalculator(category)
        emit(strategy.calculate(inputs))
    }
}

@Singleton
class ScenarioRepositoryImpl @Inject constructor(
    private val calculatorFactory: FinancialCalculatorFactory
) : ScenarioRepository {
    override fun runMarketScenario(scenarioName: String): Flow<CalculationResult> = flow {
        val res = calculatorFactory.compoundInterest.calculate(mapOf("principal" to 10000.0, "ratePct" to 12.5, "years" to 5.0))
        emit(res.copy(calculatorName = "Piyasa Senaryosu ($scenarioName)"))
    }
}

@Singleton
class ValuationRepositoryImpl @Inject constructor(
    private val calculatorFactory: FinancialCalculatorFactory
) : ValuationRepository {
    override fun calculateGrahamValue(eps: Double, bvps: Double): Flow<CalculationResult> = flow {
        val res = calculatorFactory.graham.calculate(mapOf("eps" to eps, "bvps" to bvps))
        emit(res)
    }
}

@Singleton
class HistoryRepositoryImpl @Inject constructor(
    private val dao: CalculationHistoryDao
) : HistoryRepository {

    override fun getCalculationHistory(): Flow<List<CalculationResult>> {
        return dao.getAllCalculationHistory().map { list ->
            list.map { entity ->
                CalculationResult(
                    calculatorName = entity.calculatorName,
                    category = CalculatorCategory.valueOf(entity.categoryName),
                    primaryResultValue = 0.0,
                    primaryResultText = entity.resultText
                )
            }
        }
    }

    override suspend fun saveCalculationHistory(result: CalculationResult) {
        val entity = CalculationHistoryEntity(
            calculatorName = result.calculatorName,
            categoryName = result.category.name,
            resultText = result.primaryResultText
        )
        dao.insertHistory(entity)
    }
}
