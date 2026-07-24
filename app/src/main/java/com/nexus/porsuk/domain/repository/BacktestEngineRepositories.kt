package com.nexus.porsuk.domain.repository

import com.nexus.porsuk.domain.model.*
import kotlinx.coroutines.flow.Flow

/**
 * 1. Backtest Deposu Sözleşmesi (BacktestRepository)
 */
interface BacktestRepository {
    fun runBacktest(config: BacktestConfig): Flow<BacktestReport>
    fun getSavedReports(): Flow<List<BacktestReport>>
    suspend fun saveReport(report: BacktestReport)
}

/**
 * 2. Simülasyon Yürütme Deposu Sözleşmesi (SimulationRepository)
 */
interface SimulationRepository {
    fun executeSimulationStep(config: BacktestConfig): Flow<EquityPoint>
}

/**
 * 3. Performans Metrikleri Deposu Sözleşmesi (MetricsRepository)
 */
interface MetricsRepository {
    fun calculatePerformanceMetrics(trades: List<BacktestTradeLog>): Flow<BacktestMetrics>
}

/**
 * 4. Benchmark Karşılaştırma Deposu Sözleşmesi (BenchmarkRepository)
 */
interface BenchmarkRepository {
    fun compareWithBuyAndHold(symbol: String): Flow<Double>
}
