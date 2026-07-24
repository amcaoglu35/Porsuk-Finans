package com.nexus.porsuk.data.plugin

import com.nexus.porsuk.domain.model.StrategyBlockItem
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Porsuk Strategy Builder Pro — Plugin Architecture Arayüzü (StrategyConditionPlugin)
 *
 * Open/Closed Principle & Plugin Architecture: Yeni koşullar ve aksiyonlar var olan kodlar değiştirilmeden bu arayüz uygulanarak sisteme eklenebilir.
 */
interface StrategyConditionPlugin {
    fun getPluginName(): String
    fun validateBlock(block: StrategyBlockItem): Boolean
}

/**
 * RSI Gösterge Plugin'i (RsiConditionPlugin)
 */
@Singleton
class RsiConditionPlugin @Inject constructor() : StrategyConditionPlugin {
    override fun getPluginName() = "RSI Göstergesi"
    override fun validateBlock(block: StrategyBlockItem): Boolean {
        val valNum = block.targetValue.toDoubleOrNull()
        return valNum != null && valNum in 0.0..100.0
    }
}

/**
 * Değerleme F/K Oranı Plugin'i (PeRatioConditionPlugin)
 */
@Singleton
class PeRatioConditionPlugin @Inject constructor() : StrategyConditionPlugin {
    override fun getPluginName() = "F/K Oranı"
    override fun validateBlock(block: StrategyBlockItem): Boolean {
        val valNum = block.targetValue.toDoubleOrNull()
        return valNum != null && valNum > 0
    }
}

/**
 * Altman Z-Score Plugin'i (AltmanZConditionPlugin)
 */
@Singleton
class AltmanZConditionPlugin @Inject constructor() : StrategyConditionPlugin {
    override fun getPluginName() = "Altman Z-Score"
    override fun validateBlock(block: StrategyBlockItem): Boolean {
        val valNum = block.targetValue.toDoubleOrNull()
        return valNum != null
    }
}
