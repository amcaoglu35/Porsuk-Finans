package com.nexus.porsuk.data.engine

import com.nexus.porsuk.domain.model.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Porsuk Master Score Engine — Merkezi Ağırlıklı Skor Hesaplayıcısı (MasterScoreCalculatorEngine)
 *
 * 8 Alt skor bileşenini dinamik ağırlıklar haritasına (`Map<ScoreComponentType, Double>`) göre
 * 0-100 genel skor ve 7 dereceli `ScoreLevel` seviyesine dönüştürür.
 */
@Singleton
class MasterScoreCalculatorEngine @Inject constructor() {

    private var componentWeights: Map<ScoreComponentType, Double> = mapOf(
        ScoreComponentType.FINANCIAL to 20.0,
        ScoreComponentType.TECHNICAL to 15.0,
        ScoreComponentType.DIVIDEND to 10.0,
        ScoreComponentType.RISK to 15.0,
        ScoreComponentType.MARKET to 10.0,
        ScoreComponentType.GROWTH to 10.0,
        ScoreComponentType.VALUATION to 10.0,
        ScoreComponentType.MOMENTUM to 10.0
    )

    fun calculateMasterScore(symbol: String): MasterScoreResult {
        val subScores = listOf(
            SubScoreDetail(ScoreComponentType.FINANCIAL, 88, componentWeights[ScoreComponentType.FINANCIAL] ?: 20.0, "Büyüme & Karlılık Yüksek"),
            SubScoreDetail(ScoreComponentType.TECHNICAL, 85, componentWeights[ScoreComponentType.TECHNICAL] ?: 15.0, "RSI & MACD Boğa Eğiliminde"),
            SubScoreDetail(ScoreComponentType.DIVIDEND, 80, componentWeights[ScoreComponentType.DIVIDEND] ?: 10.0, "%4.2 Düzenli Verim"),
            SubScoreDetail(ScoreComponentType.RISK, 82, componentWeights[ScoreComponentType.RISK] ?: 15.0, "Beta 1.12 - Düşük/Orta Risk"),
            SubScoreDetail(ScoreComponentType.MARKET, 84, componentWeights[ScoreComponentType.MARKET] ?: 10.0, "Endeks ve Sektör Üstü Performans"),
            SubScoreDetail(ScoreComponentType.GROWTH, 90, componentWeights[ScoreComponentType.GROWTH] ?: 10.0, "Hasılat Artışı Güçlü"),
            SubScoreDetail(ScoreComponentType.VALUATION, 86, componentWeights[ScoreComponentType.VALUATION] ?: 10.0, "F/K 4.85 Uygun Değerleme"),
            SubScoreDetail(ScoreComponentType.MOMENTUM, 88, componentWeights[ScoreComponentType.MOMENTUM] ?: 10.0, "İşlem Hacmi & Fiyat İvmesi Yüksek")
        )

        var totalWeightedScore = 0.0
        var totalWeight = 0.0

        subScores.forEach { item ->
            totalWeightedScore += (item.score * item.weightPct)
            totalWeight += item.weightPct
        }

        val finalScore = if (totalWeight > 0) (totalWeightedScore / totalWeight).toInt().coerceIn(0, 100) else 50
        val level = ScoreLevel.fromScore(finalScore)

        return MasterScoreResult(
            symbol = symbol,
            masterScore = finalScore,
            level = level,
            subScores = subScores
        )
    }

    fun updateWeights(newWeights: Map<ScoreComponentType, Double>) {
        this.componentWeights = newWeights
    }
}
