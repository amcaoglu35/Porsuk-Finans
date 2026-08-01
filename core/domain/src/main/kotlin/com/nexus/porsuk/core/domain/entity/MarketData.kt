package com.nexus.porsuk.core.domain.entity

data class MacroIndicators(
    val tcmbPolicyRate: Double = 45.0,
    val tcmbInflation: Double = 68.5,
    val fedInterestRate: Double = 5.25,
    val fedInflation: Double = 3.2,
    val usdTry: Double = 33.85,
    val eurTry: Double = 36.90
)

data class MarketSentiment(
    val bullRatio: Int,
    val bearRatio: Int,
    val fearAndGreedIndex: Int,
    val fearAndGreedLabel: String,
    val vixIndex: Double,
    val bist100Change: Double,
    val aiAccuracyRate: Double
)
