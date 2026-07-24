package com.nexus.porsuk.domain.model

/**
 * Strateji Düğüm / Blok Modeli (StrategyBlockItem)
 */
data class StrategyBlockItem(
    val blockId: String,
    val title: String,
    val indicatorName: String,
    val operator: ConditionOperator,
    val targetValue: String,
    val isEntryCondition: Boolean = true
)

/**
 * Strateji Doğrulama Sonucu (StrategyValidationResult)
 */
data class StrategyValidationResult(
    val isValid: Boolean = true,
    val missingRules: List<String> = emptyList(),
    val conflictingRules: List<String> = emptyList(),
    val summaryMessage: String = "Stratejiniz mantıksal olarak eksiksizdir ve simülasyona hazırdır."
)

/**
 * Strateji Ana Modeli (StrategyModel)
 */
data class StrategyModel(
    val id: String = "strat_${System.currentTimeMillis()}",
    val name: String,
    val type: StrategyType,
    val description: String,
    val blocks: List<StrategyBlockItem>,
    val stopLossPct: Double = 5.0,
    val takeProfitPct: Double = 15.0,
    val version: Int = 1,
    val isFavorite: Boolean = false
)

/**
 * Geleceğe Hazır AI Strategy Generator Stub Modeli
 */
data class AiStrategyGeneratorStub(
    val promptInput: String = "Warren Buffett tarzı ucuz ve yüksek kârlı hisseleri bul",
    val generatedStrategyName: String = "Orakul AI Buffett-Style Value Strategy",
    val confidenceScore: Int = 94
)
