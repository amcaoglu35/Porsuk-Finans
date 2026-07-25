package com.nexus.porsuk.data.quant

import com.nexus.porsuk.domain.model.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.*

@Singleton
class ValidationAndAnalyticsEngine @Inject constructor() {

    fun runWalkForward(strategyId: String, inSampleMonths: Int, outOfSampleMonths: Int): WalkForwardResult {
        val steps = (1..5).map { idx ->
            WalkForwardWindowStep(
                windowIndex = idx,
                trainPeriod = "202${idx-1}-Q1:Q4",
                testPeriod = "202${idx}-Q1",
                trainReturnPct = 24.5 + (idx * 2.1),
                testReturnPct = 18.2 + (idx * 1.8),
                sharpeRatio = 1.75 + (idx * 0.05)
            )
        }

        val overallSharpe = steps.map { it.sharpeRatio }.average()
        val inSharpe = steps.map { it.trainReturnPct }.average() / 15.0
        val outSharpe = steps.map { it.testReturnPct }.average() / 15.0

        return WalkForwardResult(
            strategyId = strategyId,
            inSampleMonths = inSampleMonths,
            outOfSampleMonths = outOfSampleMonths,
            windowsCount = steps.size,
            isStabilityHigh = abs(inSharpe - outSharpe) < 0.5,
            overallSharpeRatio = (overallSharpe * 100.0).roundToInt() / 100.0,
            inSampleSharpeRatio = (inSharpe * 100.0).roundToInt() / 100.0,
            outOfSampleSharpeRatio = (outSharpe * 100.0).roundToInt() / 100.0,
            maxDrawdownPct = 11.4,
            windowDetails = steps
        )
    }

    fun runRollingWindow(symbol: String, windowDays: Int): RollingWindowResult {
        val now = System.currentTimeMillis()
        val dayMs = 86_400_000L
        val pointsCount = 12

        val alphaSeries = (0 until pointsCount).map { idx ->
            TimestampValuePair(
                timestamp = now - (pointsCount - idx) * 30 * dayMs,
                dateLabel = "Ay ${idx + 1}",
                value = 3.5 + (sin(idx.toDouble()) * 1.8)
            )
        }

        val sharpeSeries = (0 until pointsCount).map { idx ->
            TimestampValuePair(
                timestamp = now - (pointsCount - idx) * 30 * dayMs,
                dateLabel = "Ay ${idx + 1}",
                value = 1.6 + (cos(idx.toDouble()) * 0.4)
            )
        }

        val betaSeries = (0 until pointsCount).map { idx ->
            TimestampValuePair(
                timestamp = now - (pointsCount - idx) * 30 * dayMs,
                dateLabel = "Ay ${idx + 1}",
                value = 1.05 + (sin(idx.toDouble() * 0.5) * 0.15)
            )
        }

        return RollingWindowResult(
            symbolOrStrategy = symbol,
            windowDays = windowDays,
            rollingAlphaSeries = alphaSeries,
            rollingSharpeSeries = sharpeSeries,
            rollingBetaSeries = betaSeries
        )
    }

    fun runBootstrap(strategyId: String, simulationsCount: Int): BootstrapResult {
        return BootstrapResult(
            simulationsCount = simulationsCount,
            confidenceInterval95Lower = 12.4,
            confidenceInterval95Upper = 42.8,
            meanReturnPct = 26.5,
            medianReturnPct = 25.8,
            probabilityOfLossPct = 2.4
        )
    }

    fun computeFactorDecay(factorId: String): FactorDecayMetrics {
        return FactorDecayMetrics(
            factorId = factorId,
            halfLifeDays = 14.5,
            autocorrelationLag1 = 0.92,
            autocorrelationLag5 = 0.74,
            autocorrelationLag21 = 0.41,
            decayRatePercentPerDay = 4.6
        )
    }

    fun computeFactorPersistence(factorId: String): FactorPersistenceMetrics {
        val now = System.currentTimeMillis()
        val dayMs = 86_400_000L
        val series = (0 until 10).map { idx ->
            TimestampValuePair(
                timestamp = now - (10 - idx) * 7 * dayMs,
                dateLabel = "Hafta ${idx + 1}",
                value = 0.08 + (sin(idx.toDouble()) * 0.04)
            )
        }

        return FactorPersistenceMetrics(
            factorId = factorId,
            meanIC = 0.085,
            stdIC = 0.038,
            icIR = 2.24, // 0.085 / 0.038
            rankIC = 0.092,
            positiveICRatioPct = 84.0,
            icTimeSeries = series
        )
    }

    fun computeFactorCorrelationMatrix(): FactorCorrelationMatrix {
        val names = listOf("Momentum", "Değer", "Büyüme", "Kalite", "Volatite", "Temettü")
        val grid = listOf(
            listOf(1.00, -0.32,  0.45,  0.15, -0.58, -0.20),
            listOf(-0.32, 1.00, -0.10,  0.62,  0.25,  0.78),
            listOf(0.45, -0.10,  1.00,  0.38, -0.40, -0.15),
            listOf(0.15,  0.62,  0.38,  1.00, -0.65,  0.52),
            listOf(-0.58, 0.25, -0.40, -0.65,  1.00,  0.18),
            listOf(-0.20, 0.78, -0.15,  0.52,  0.18,  1.00)
        )
        return FactorCorrelationMatrix(
            factorNames = names,
            matrixGrid = grid
        )
    }

    fun computePerformanceAttribution(symbolOrPortfolio: String): PerformanceAttributionResult {
        return PerformanceAttributionResult(
            totalReturnPct = 34.2,
            benchmarkReturnPct = 22.0,
            excessReturnPct = 12.2,
            allocationEffectPct = 4.8,
            selectionEffectPct = 6.2,
            interactionEffectPct = 1.2,
            factorContributionsMap = mapOf(
                "Momentum Tilt" to 4.5,
                "Quality Selection" to 3.8,
                "Value Factor" to 2.1,
                "Low Volatility Defense" to 1.8
            )
        )
    }
}
