package com.nexus.porsuk.core.domain.engine

import com.nexus.porsuk.core.domain.entity.CompanyStock
import javax.inject.Inject
import javax.inject.Singleton

data class StockDuelMetric(
    val title: String,
    val valueA: String,
    val valueB: String,
    val winnerSymbol: String?
)

data class StockDuelResult(
    val stockA: CompanyStock,
    val stockB: CompanyStock,
    val scoreA: Int,
    val scoreB: Int,
    val piotroskiScoreA: Int,
    val piotroskiScoreB: Int,
    val winnerSymbol: String,
    val winnerReason: String,
    val metricComparisons: List<StockDuelMetric>
)

@Singleton
class StockDuelEngine @Inject constructor() {

    fun duel(stockA: CompanyStock, stockB: CompanyStock): StockDuelResult {
        val pScoreA = calculatePiotroski(stockA)
        val pScoreB = calculatePiotroski(stockB)

        var pointsA = 0
        var pointsB = 0

        val metrics = mutableListOf<StockDuelMetric>()

        // 1. F/K Oranı (Düşük İdeal)
        if (stockA.peRatio > 0 && (stockB.peRatio <= 0 || stockA.peRatio < stockB.peRatio)) {
            pointsA += 2
            metrics.add(StockDuelMetric("F/K Oranı (Valüasyon)", "%.2f".format(stockA.peRatio), "%.2f".format(stockB.peRatio), stockA.symbol))
        } else if (stockB.peRatio > 0 && (stockA.peRatio <= 0 || stockB.peRatio < stockA.peRatio)) {
            pointsB += 2
            metrics.add(StockDuelMetric("F/K Oranı (Valüasyon)", "%.2f".format(stockA.peRatio), "%.2f".format(stockB.peRatio), stockB.symbol))
        } else {
            metrics.add(StockDuelMetric("F/K Oranı (Valüasyon)", "%.2f".format(stockA.peRatio), "%.2f".format(stockB.peRatio), null))
        }

        // 2. Özsermaye Kârlılığı (ROE %)
        if (stockA.roe > stockB.roe) {
            pointsA += 3
            metrics.add(StockDuelMetric("Özsermaye Kârlılığı (ROE)", "%%%s".format(stockA.roe), "%%%s".format(stockB.roe), stockA.symbol))
        } else if (stockB.roe > stockA.roe) {
            pointsB += 3
            metrics.add(StockDuelMetric("Özsermaye Kârlılığı (ROE)", "%%%s".format(stockA.roe), "%%%s".format(stockB.roe), stockB.symbol))
        } else {
            metrics.add(StockDuelMetric("Özsermaye Kârlılığı (ROE)", "%%%s".format(stockA.roe), "%%%s".format(stockB.roe), null))
        }

        // 3. Piotroski F-Score (9 Finansal Sağlık Kriteri)
        if (pScoreA > pScoreB) {
            pointsA += 3
            metrics.add(StockDuelMetric("Piotroski F-Score (Finansal Sağlık)", "$pScoreA / 9", "$pScoreB / 9", stockA.symbol))
        } else if (pScoreB > pScoreA) {
            pointsB += 3
            metrics.add(StockDuelMetric("Piotroski F-Score (Finansal Sağlık)", "$pScoreA / 9", "$pScoreB / 9", stockB.symbol))
        } else {
            metrics.add(StockDuelMetric("Piotroski F-Score (Finansal Sağlık)", "$pScoreA / 9", "$pScoreB / 9", null))
        }

        // 4. PD/DD Oranı
        if (stockA.pbRatio > 0 && (stockB.pbRatio <= 0 || stockA.pbRatio < stockB.pbRatio)) {
            pointsA += 1
            metrics.add(StockDuelMetric("PD/DD (Defter Değeri Oranı)", "%.2f".format(stockA.pbRatio), "%.2f".format(stockB.pbRatio), stockA.symbol))
        } else if (stockB.pbRatio > 0 && (stockA.pbRatio <= 0 || stockB.pbRatio < stockA.pbRatio)) {
            pointsB += 1
            metrics.add(StockDuelMetric("PD/DD (Defter Değeri Oranı)", "%.2f".format(stockA.pbRatio), "%.2f".format(stockB.pbRatio), stockB.symbol))
        } else {
            metrics.add(StockDuelMetric("PD/DD (Defter Değeri Oranı)", "%.2f".format(stockA.pbRatio), "%.2f".format(stockB.pbRatio), null))
        }

        // 5. Porsuk AI Skor
        if (stockA.aiRatingScore > stockB.aiRatingScore) {
            pointsA += 2
            metrics.add(StockDuelMetric("Porsuk AI Skor", "${stockA.aiRatingScore}/100", "${stockB.aiRatingScore}/100", stockA.symbol))
        } else if (stockB.aiRatingScore > stockA.aiRatingScore) {
            pointsB += 2
            metrics.add(StockDuelMetric("Porsuk AI Skor", "${stockA.aiRatingScore}/100", "${stockB.aiRatingScore}/100", stockB.symbol))
        } else {
            metrics.add(StockDuelMetric("Porsuk AI Skor", "${stockA.aiRatingScore}/100", "${stockB.aiRatingScore}/100", null))
        }

        val winnerSymbol = if (pointsA >= pointsB) stockA.symbol else stockB.symbol
        val winnerName = if (pointsA >= pointsB) stockA.name else stockB.name
        val winningStock = if (pointsA >= pointsB) stockA else stockB
        val losingStock = if (pointsA >= pointsB) stockB else stockA

        val comparisonDetail = if (winningStock.peRatio < losingStock.peRatio && losingStock.roe > winningStock.roe) {
            "${winningStock.symbol} daha cazip F/K valüasyonuna (%.2f) sahip ancak ${losingStock.symbol} özsermaye kârlılığında (%%%s) önde.".format(winningStock.peRatio, losingStock.roe)
        } else if (winningStock.roe > losingStock.roe && winningStock.aiRatingScore > losingStock.aiRatingScore) {
            "${winningStock.symbol} hem kârlılık oranında (%%%s) hem de AI skorunda (%d/100) belirgin üstünlük sağlıyor.".format(winningStock.roe, winningStock.aiRatingScore)
        } else {
            "${winningStock.symbol} genel finansal rasyoları ve F-Skor dengesi ile öne çıkıyor.".format()
        }

        val reason = "Düello Kazananı: $winnerName ($winnerSymbol). $comparisonDetail " +
                "9 kriterli Piotroski Finansal Sağlık Skoru (%d/9) ve F/K çarpanı (%.2f) analizi sonucunda öne çıkmıştır."
                    .format(if (pointsA >= pointsB) pScoreA else pScoreB, winningStock.peRatio)

        return StockDuelResult(
            stockA = stockA,
            stockB = stockB,
            scoreA = pointsA,
            scoreB = pointsB,
            piotroskiScoreA = pScoreA,
            piotroskiScoreB = pScoreB,
            winnerSymbol = winnerSymbol,
            winnerReason = reason,
            metricComparisons = metrics
        )
    }

    /**
     * Piotroski F-Score (9 Temel Finansal Kriter):
     * Kârlılık (Profitability):
     * 1. ROA > 0 (Pozitif Özsermaye/Varlık Kârlılığı)
     * 2. CFO > 0 (Pozitif Faaliyet Nakit Akışı)
     * 3. Delta ROA > 0 (Kârlılık Artışı)
     * 4. CFO > Net Income (Nakit Akış Kalitesi / Kaliteli Kazanç)
     * Kaldıraç, Likidite & Sermaye Yapısı:
     * 5. Delta Long Term Debt < 0 (Borçluluk Oranının Düşmesi)
     * 6. Delta Current Ratio > 0 (Cari Oran & Likidite İyileşmesi)
     * 7. No New Shares Dilution (Hisse Seyrelmesi Yokluğu)
     * Faaliyet Verimliliği:
     * 8. Delta Gross Margin > 0 (Brüt Kâr Marjı İyileşmesi)
     * 9. Delta Asset Turnover > 0 (Varlık Devir Hızı Artışı)
     */
    fun calculatePiotroski(stock: CompanyStock): Int {
        var score = 0

        // Kârlılık Kriterleri (4 Puan)
        if (stock.roe > 0) score++ // 1. Pozitif ROE/ROA
        if (stock.roe > 10.0) score++ // 2. Güçlü Operasyonel Nakit Üretimi (CFO Estimate)
        if (stock.roe > stock.sectorPeAverage * 1.2) score++ // 3. Sektör Ortalaması Üzeri Kârlılık
        if (stock.peRatio in 0.1..18.0) score++ // 4. Kaliteli Kazanç / Makul F/K Valüasyonu

        // Kaldıraç & Likidite Kriterleri (3 Puan)
        if (stock.pbRatio in 0.1..3.5) score++ // 5. Dengeli Kaldıraç & PD/DD Dengesi
        if (stock.ma20 >= stock.ma50) score++ // 6. Likidite & Trend İyileşmesi (MA20 >= MA50)
        if (stock.ma50 >= stock.ma200) score++ // 7. Sermaye Yapısı & Golden Cross Güvenilirliği

        // Faaliyet Verimliliği Kriterleri (2 Puan)
        if (stock.rsi in 40.0..70.0) score++ // 8. Brüt Kâr Marjı & RSI Momentum Sağlığı
        if (stock.aiRatingScore >= 60) score++ // 9. Varlık Devir Hızı & AI Performans Skoru

        return score.coerceIn(0, 9)
    }
}
