package com.nexus.porsuk.core.domain.repository

import com.nexus.porsuk.core.domain.entity.MacroIndicators
import com.nexus.porsuk.core.domain.entity.MarketSentiment
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

interface MarketRepository {
    fun getMarketSentiment(): Flow<MarketSentiment>
    fun getMacroIndicators(): Flow<MacroIndicators>
}

@Singleton
class MarketRepositoryImpl @Inject constructor() : MarketRepository {

    private val macroState = MutableStateFlow(
        MacroIndicators(
            tcmbPolicyRate = 45.0,
            tcmbInflation = 68.5,
            fedInterestRate = 5.25,
            fedInflation = 3.2,
            usdTry = 33.85,
            eurTry = 36.90
        )
    )

    private val sentimentState = MutableStateFlow(calculateDynamicSentiment(vix = 14.8, bistChange = 1.65))

    override fun getMarketSentiment(): Flow<MarketSentiment> = sentimentState.asStateFlow()

    override fun getMacroIndicators(): Flow<MacroIndicators> = macroState.asStateFlow()

    private fun calculateDynamicSentiment(vix: Double, bistChange: Double): MarketSentiment {
        val baseScore = 50 + (bistChange * 8) - ((vix - 15.0) * 2)
        val fearGreed = baseScore.coerceIn(5.0, 95.0).toInt()

        val bullRatio = (fearGreed * 0.95).toInt().coerceIn(10, 90)
        val bearRatio = 100 - bullRatio

        val label = when {
            fearGreed >= 75 -> "Aşırı Hırs"
            fearGreed >= 60 -> "Hırs"
            fearGreed >= 40 -> "Nötr"
            fearGreed >= 25 -> "Korku"
            else -> "Aşırı Korku"
        }

        return MarketSentiment(
            bullRatio = bullRatio,
            bearRatio = bearRatio,
            fearAndGreedIndex = fearGreed,
            fearAndGreedLabel = label,
            vixIndex = vix,
            bist100Change = bistChange,
            aiAccuracyRate = 87.6
        )
    }
}
