package com.nexus.porsuk.domain.model

/**
 * Backtest Simülasyon Parametreleri (BacktestConfig)
 */
data class BacktestConfig(
    val strategyName: String = "RSI + EMA Crossover Stratejisi",
    val mode: BacktestMode = BacktestMode.SINGLE_ASSET,
    val initialCapitalUsd: Double = 10000.0,
    val tradeSizePct: Double = 10.0, // Pozisyon büyüklüğü %
    val commissionPct: Double = 0.1, // Komisyon oranı %
    val slippagePct: Double = 0.05,
    val startDateTimestamp: Long = System.currentTimeMillis() - (365L * 86400000L), // 1 Yıl Önce
    val endDateTimestamp: Long = System.currentTimeMillis()
)

/**
 * 16 Finansal Performans Metriği (BacktestMetrics)
 */
data class BacktestMetrics(
    val totalReturnPct: Double = 34.5,
    val cagrPct: Double = 34.5, // Yıllık Bileşik Büyüme
    val alpha: Double = 8.2,
    val beta: Double = 0.92,
    val sharpeRatio: Double = 1.85,
    val sortinoRatio: Double = 2.40,
    val calmarRatio: Double = 2.15,
    val informationRatio: Double = 1.45,
    val annualVolatilityPct: Double = 18.5,
    val maxDrawdownPct: Double = -16.0,
    val recoveryFactor: Double = 2.15,
    val profitFactor: Double = 1.95,
    val winRatePct: Double = 64.0,
    val avgWinUsd: Double = 420.0,
    val avgLossUsd: Double = -180.0,
    val expectancyUsd: Double = 204.0
)

/**
 * İşlem Günlüğü Ögesi (BacktestTradeLog)
 */
data class BacktestTradeLog(
    val tradeId: String,
    val symbol: String,
    val orderType: TradeOrderType,
    val entryPrice: Double,
    val exitPrice: Double,
    val entryTimestamp: Long,
    val exitTimestamp: Long,
    val netProfitUsd: Double,
    val returnPct: Double,
    val durationDays: Int
)

/**
 * Sermaye ve Drawdown Noktası (EquityPoint)
 */
data class EquityPoint(
    val timestamp: Long,
    val equityValue: Double,
    val drawdownPct: Double
)

/**
 * Standart Backtest Rapor Modeli (BacktestReport)
 */
data class BacktestReport(
    val reportId: String = "report_${System.currentTimeMillis()}",
    val config: BacktestConfig,
    val metrics: BacktestMetrics,
    val equityCurve: List<EquityPoint>,
    val tradeLogs: List<BacktestTradeLog>,
    val buyAndHoldReturnPct: Double = 22.0,
    val benchmarkSymbol: String = "BIST100",
    val executiveSummaryText: String = "Stratejiniz %34.5 toplam getiri ve 1.85 Sharpe Oranı ile %22.0 Buy & Hold getirisini belirgin şekilde geride bırakmıştır.",
    val generatedAt: Long = System.currentTimeMillis()
)

/**
 * Geleceğe Hazır AI Strategy Optimization & Monte Carlo Stub Modeli
 */
data class AiMonteCarloSimulationStub(
    val totalRuns: Int = 1000,
    val probabilityOfProfitPct: Double = 92.4,
    val worstCaseDrawdownPct: Double = -22.5,
    val aiOptimizationRecommendation: String = "Orakul AI: RSI 14 periyodu yerine 12 periyot kullanımı win rate'i %4.2 artırmaktadır."
)
