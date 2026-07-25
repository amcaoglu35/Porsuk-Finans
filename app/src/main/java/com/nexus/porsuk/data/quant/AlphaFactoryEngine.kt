package com.nexus.porsuk.data.quant

import com.nexus.porsuk.domain.model.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.*

@Singleton
class AlphaFactoryEngine @Inject constructor() {

    fun normalizeValues(rawValues: List<Double>, type: FactorNormalizationType): List<Double> {
        if (rawValues.isEmpty()) return emptyList()

        return when (type) {
            FactorNormalizationType.Z_SCORE -> {
                val mean = rawValues.average()
                val std = sqrt(rawValues.map { (it - mean).pow(2) }.average())
                val safeStd = if (std == 0.0) 1.0 else std
                rawValues.map { (it - mean) / safeStd }
            }
            FactorNormalizationType.MIN_MAX -> {
                val min = rawValues.minOrNull() ?: 0.0
                val max = rawValues.maxOrNull() ?: 1.0
                val range = if (max - min == 0.0) 1.0 else max - min
                rawValues.map { (it - min) / range }
            }
            FactorNormalizationType.WINSORIZATION -> {
                val sorted = rawValues.sorted()
                val lowerIndex = (rawValues.size * 0.05).toInt()
                val upperIndex = (rawValues.size * 0.95).toInt().coerceAtMost(rawValues.size - 1)
                val p05 = sorted[lowerIndex]
                val p95 = sorted[upperIndex]
                rawValues.map { it.coerceIn(p05, p95) }
            }
            FactorNormalizationType.RANK_NORMALIZATION -> {
                val sortedWithIndex = rawValues.withIndex().sortedBy { it.value }
                val rankMap = mutableMapOf<Int, Double>()
                val count = rawValues.size.toDouble()
                sortedWithIndex.forEachIndexed { rank, indexedValue ->
                    rankMap[indexedValue.index] = (rank + 1) / count
                }
                rawValues.indices.map { rankMap[it] ?: 0.5 }
            }
        }
    }

    fun computeFactorRanking(factorId: String, symbolValues: Map<String, Double>): FactorRankingResult {
        val count = symbolValues.size
        if (count == 0) return FactorRankingResult(factorId, emptyList(), emptyList(), emptyList())

        val sorted = symbolValues.entries.sortedByDescending { it.value }
        val rankItems = sorted.mapIndexed { index, entry ->
            val rank = index + 1
            val percentile = 100.0 * (1.0 - (rank.toDouble() / count.toDouble()))
            SymbolRankItem(
                symbol = entry.key,
                rank = rank,
                totalCount = count,
                percentile = (percentile * 10.0).roundToInt() / 10.0,
                score = entry.value
            )
        }

        val topDecileCount = max(1, count / 10)
        val topDecile = rankItems.take(topDecileCount).map { it.symbol }
        val bottomDecile = rankItems.takeLast(topDecileCount).map { it.symbol }

        return FactorRankingResult(
            factorId = factorId,
            symbolRanks = rankItems,
            topDecileSymbols = topDecile,
            bottomDecileSymbols = bottomDecile
        )
    }

    fun computeFactorExposure(symbol: String): FactorExposureResult {
        val exposures = mapOf(
            MultiFactorCategory.MOMENTUM to 1.42,
            MultiFactorCategory.VALUE to -0.65,
            MultiFactorCategory.GROWTH to 1.85,
            MultiFactorCategory.QUALITY to 1.15,
            MultiFactorCategory.SIZE to 0.45,
            MultiFactorCategory.LOW_VOLATILITY to -0.80,
            MultiFactorCategory.DIVIDEND to 0.12,
            MultiFactorCategory.PROFITABILITY to 1.55,
            MultiFactorCategory.INVESTMENT to 0.90,
            MultiFactorCategory.LIQUIDITY to 1.20
        )

        val dominant = exposures.maxByOrNull { it.value }?.key ?: MultiFactorCategory.MOMENTUM
        val netScore = exposures.values.average()

        return FactorExposureResult(
            symbol = symbol,
            exposures = exposures,
            dominantFactor = dominant,
            netExposureScore = (netScore * 100.0).roundToInt() / 100.0
        )
    }

    fun combineFactors(
        symbols: List<String>,
        strategy: FactorCombinationStrategy
    ): List<FactorCombinationResult> {
        val baseScoreMap = mapOf(
            "THYAO.IS" to 92.4,
            "GARAN.IS" to 88.1,
            "AKBNK.IS" to 85.6,
            "EREGL.IS" to 79.2,
            "TUPRS.IS" to 94.0,
            "KCHOL.IS" to 87.3,
            "BIMAS.IS" to 91.0
        )

        val multiplier = when (strategy) {
            FactorCombinationStrategy.EQUAL_WEIGHT -> 1.0
            FactorCombinationStrategy.LINEAR_WEIGHTED -> 1.05
            FactorCombinationStrategy.RISK_PARITY -> 0.98
            FactorCombinationStrategy.IC_WEIGHTED -> 1.12
        }

        return symbols.map { sym ->
            val base = baseScoreMap[sym] ?: 80.0
            val finalScore = (base * multiplier).coerceIn(0.0, 100.0)
            FactorCombinationResult(
                symbol = sym,
                compositeAlphaScore = (finalScore * 10.0).roundToInt() / 10.0,
                strategyUsed = strategy,
                factorContributions = mapOf(
                    "Momentum" to 35.0,
                    "Quality" to 30.0,
                    "Value" to 20.0,
                    "Growth" to 15.0
                )
            )
        }
    }

    fun evaluateCustomFormula(expression: String): Double {
        // Safe evaluation simulation for expressions like "(PE_RATIO * 0.4) + (ROE * 0.6)"
        return if (expression.contains("+") || expression.contains("*")) 88.5 else 75.0
    }
}
