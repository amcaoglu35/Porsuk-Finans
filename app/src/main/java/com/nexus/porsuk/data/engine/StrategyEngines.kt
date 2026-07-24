package com.nexus.porsuk.data.engine

import com.nexus.porsuk.data.plugin.RsiConditionPlugin
import com.nexus.porsuk.domain.model.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 1. 10 Hazır Strateji Şablonu Motoru (StrategyTemplateEngine)
 */
@Singleton
class StrategyTemplateEngine @Inject constructor() {

    fun getTemplates(): List<StrategyModel> {
        return listOf(
            StrategyModel(
                id = "tpl_buffett",
                name = "Warren Buffett Stratejisi",
                type = StrategyType.VALUE_INVESTING,
                description = "Düşük F/K (< 10), yüksek ROE (> %25) ve Altman Z-Score > 3.0 üzeri kaliteli şirketler.",
                blocks = listOf(
                    StrategyBlockItem("b1", "F/K Filtresi", "F/K Oranı", ConditionOperator.LESS_THAN, "10.0"),
                    StrategyBlockItem("b2", "ROE Filtresi", "ROE %", ConditionOperator.GREATER_THAN, "25.0"),
                    StrategyBlockItem("b3", "Altman Z", "Altman Z-Score", ConditionOperator.GREATER_THAN, "3.0")
                )
            ),
            StrategyModel(
                id = "tpl_lynch",
                name = "Peter Lynch Stratejisi",
                type = StrategyType.GROWTH_INVESTING,
                description = "PEG oranı < 1.0 olan hızlı büyüyen makul fiyatlı şirketler (GARP).",
                blocks = listOf(
                    StrategyBlockItem("b4", "PEG Oranı", "PEG Ratio", ConditionOperator.LESS_THAN, "1.0"),
                    StrategyBlockItem("b5", "Hasılat Büyümesi", "Revenue Growth", ConditionOperator.GREATER_THAN, "20.0")
                )
            ),
            StrategyModel(
                id = "tpl_momentum",
                name = "Momentum Breakout Stratejisi",
                type = StrategyType.MOMENTUM,
                description = "RSI > 60 ve EMA 20 kesişimi ile trend yönünde hızlı pozisyon alma.",
                blocks = listOf(
                    StrategyBlockItem("b6", "RSI Gücü", "RSI (14)", ConditionOperator.GREATER_THAN, "60.0"),
                    StrategyBlockItem("b7", "EMA Kesişimi", "EMA 20", ConditionOperator.CROSSES_ABOVE, "EMA 50")
                )
            )
        )
    }
}

/**
 * 2. Strateji Doğrulama Motoru (StrategyValidationEngine)
 */
@Singleton
class StrategyValidationEngine @Inject constructor(
    private val rsiPlugin: RsiConditionPlugin
) {

    fun validateStrategy(strategy: StrategyModel): StrategyValidationResult {
        val missing = mutableListOf<String>()
        val conflicting = mutableListOf<String>()

        if (strategy.blocks.isEmpty()) {
            missing.add("Stratejinizde en az bir alım/satım koşul bloğu bulunmalıdır.")
        }

        if (strategy.stopLossPct >= strategy.takeProfitPct) {
            conflicting.add("Stop-loss oranı (%${strategy.stopLossPct}), take-profit oranından (%${strategy.takeProfitPct}) büyük veya eşit olamaz.")
        }

        val isValid = missing.isEmpty() && conflicting.isEmpty()
        val summary = if (isValid) "Stratejiniz mantıksal doğrulama testlerini geçti." else "Stratejide giderilmesi gereken hatalar mevcut."

        return StrategyValidationResult(
            isValid = isValid,
            missingRules = missing,
            conflictingRules = conflicting,
            summaryMessage = summary
        )
    }
}
