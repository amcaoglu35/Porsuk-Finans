package com.nexus.porsuk.data.repository

import com.nexus.porsuk.data.engine.EventDrivenBacktestEngine
import com.nexus.porsuk.data.engine.MetricsCalculatorEngine
import com.nexus.porsuk.data.local.dao.BacktestReportDao
import com.nexus.porsuk.data.local.entity.BacktestReportEntity
import com.nexus.porsuk.domain.model.*
import com.nexus.porsuk.domain.repository.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BacktestRepositoryImpl @Inject constructor(
    private val backtestEngine: EventDrivenBacktestEngine,
    private val dao: BacktestReportDao
) : BacktestRepository {

    override fun runBacktest(config: BacktestConfig): Flow<BacktestReport> = flow {
        emit(backtestEngine.runBacktest(config))
    }

    override fun getSavedReports(): Flow<List<BacktestReport>> {
        return dao.getAllSavedReports().map { list ->
            list.map { entity ->
                BacktestReport(
                    reportId = entity.reportId,
                    config = BacktestConfig(strategyName = entity.strategyName),
                    metrics = BacktestMetrics(
                        totalReturnPct = entity.totalReturnPct,
                        sharpeRatio = entity.sharpeRatio,
                        maxDrawdownPct = entity.maxDrawdownPct,
                        winRatePct = entity.winRatePct
                    ),
                    equityCurve = emptyList(),
                    tradeLogs = emptyList()
                )
            }
        }
    }

    override suspend fun saveReport(report: BacktestReport) {
        val entity = BacktestReportEntity(
            reportId = report.reportId,
            strategyName = report.config.strategyName,
            totalReturnPct = report.metrics.totalReturnPct,
            sharpeRatio = report.metrics.sharpeRatio,
            maxDrawdownPct = report.metrics.maxDrawdownPct,
            winRatePct = report.metrics.winRatePct
        )
        dao.insertReport(entity)
    }
}

@Singleton
class SimulationRepositoryImpl @Inject constructor(
    private val backtestEngine: EventDrivenBacktestEngine
) : SimulationRepository {
    override fun executeSimulationStep(config: BacktestConfig): Flow<EquityPoint> = flow {
        val report = backtestEngine.runBacktest(config)
        report.equityCurve.forEach { emit(it) }
    }
}

@Singleton
class MetricsRepositoryImpl @Inject constructor(
    private val metricsEngine: MetricsCalculatorEngine
) : MetricsRepository {
    override fun calculatePerformanceMetrics(trades: List<BacktestTradeLog>): Flow<BacktestMetrics> = flow {
        emit(metricsEngine.calculateMetrics(trades, 10000.0))
    }
}

@Singleton
class BenchmarkRepositoryImpl @Inject constructor() : BenchmarkRepository {
    override fun compareWithBuyAndHold(symbol: String): Flow<Double> = flow {
        emit(22.0)
    }
}
