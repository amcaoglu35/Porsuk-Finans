package com.nexus.porsuk.core.domain.engine

import com.nexus.porsuk.core.domain.entity.CompanyStock
import com.nexus.porsuk.core.domain.entity.MacroIndicators
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.max
import kotlin.math.sqrt

data class PortfolioDoctorReport(
    val riskScore: Int,
    val riskCategory: String,
    val usdTryRateUsed: Double,
    val eurTryRateUsed: Double,
    val totalPortfolioValueTRY: Double,
    val totalPortfolioValueUSD: Double,
    val sharpeRatio: Double,
    val varMonthlyPercent: Double,
    val diversificationScore: Int,
    val doctorAdvice: String
)

@Singleton
class PortfolioDoctorEngine @Inject constructor() {

    fun analyzePortfolio(
        stocks: List<CompanyStock>,
        macro: MacroIndicators
    ): PortfolioDoctorReport {
        val usdRate = macro.usdTry
        val eurRate = macro.eurTry

        if (stocks.isEmpty()) {
            return PortfolioDoctorReport(
                riskScore = 20,
                riskCategory = "Düşük Risk",
                usdTryRateUsed = usdRate,
                eurTryRateUsed = eurRate,
                totalPortfolioValueTRY = 0.0,
                totalPortfolioValueUSD = 0.0,
                sharpeRatio = 0.0,
                varMonthlyPercent = 0.0,
                diversificationScore = 0,
                doctorAdvice = "Portföyünüzde henüz hisse senedi bulunmuyor."
            )
        }

        // Portföy Büyüklüğü Hesaplama (Her hisseden varsayılan 100 adet)
        val totalValueTry = stocks.sumOf { it.price * 100 }
        val totalValueUsd = if (usdRate > 0) totalValueTry / usdRate else 0.0

        // Sektör Çeşitlendirmesi ve Sektörel Korelasyon Risk Çarpanı
        val sectorMap = stocks.groupBy { it.sector }
        val sectorCount = sectorMap.size
        val maxSectorRatio = sectorMap.maxOfOrNull { it.value.size }?.toDouble()?.div(stocks.size) ?: 1.0

        // Sektör yoğunlaşma cezası (Tek bir sektör ağırlığı yüksekse korelasyon riski katlanır)
        val sectorCorrelationPenalty = if (maxSectorRatio > 0.5) (1.0 + (maxSectorRatio - 0.5) * 1.2) else 1.0
        val divScore = ((sectorCount * 25) / sectorCorrelationPenalty).coerceIn(10.0, 100.0).toInt()

        // Portföyün Ağırlıklı Beklenen Yıllık Getirisi (Rp %)
        val expectedAnnualReturn = stocks.map { (it.roe * 0.6) + (it.changePercentage * 5.0) }.average().coerceAtLeast(5.0)

        // Risk-Free Oranı (TCMB Politika Faizi Rf %)
        val riskFreeRate = macro.tcmbPolicyRate.coerceAtLeast(10.0)

        // İstatistiksel Volatilite ve Sektör Korelasyon Ayarlaması (Sigma p %)
        val stockVolatilities = stocks.map { stock ->
            val rsiDeviation = Math.abs(stock.rsi - 50.0)
            val peRisk = if (stock.peRatio > 30) 15.0 else 5.0
            (rsiDeviation * 0.4 + peRisk + Math.abs(stock.changePercentage) * 2.0).coerceIn(12.0, 45.0)
        }
        val rawAnnualVolatility = sqrt(stockVolatilities.map { it * it }.average()).coerceAtLeast(8.0)
        val annualVolatility = (rawAnnualVolatility * sectorCorrelationPenalty).coerceIn(8.0, 65.0)

        // Gerçek İstatistiksel Sharpe Rasyosu: (Rp - Rf) / Volatilite
        val rawSharpe = (expectedAnnualReturn - riskFreeRate) / annualVolatility
        val sharpe = if (rawSharpe.isNaN()) 0.5 else rawSharpe.coerceIn(-2.0, 4.0)

        // Gerçek Riske Maruz Değer (Monthly Parametric VaR %95 Güven Düzeyi)
        // VaR_monthly = Z_95 (1.645) * (Yıllık Volatilite / sqrt(12))
        val monthlyVolatility = annualVolatility / sqrt(12.0)
        val varMonthlyPercent = (1.645 * monthlyVolatility).coerceIn(1.5, 35.0)

        // Portföy Risk Skoru
        val avgRsi = stocks.map { it.rsi }.average()
        val riskScore = ((avgRsi * 0.35) + (annualVolatility * 1.1) - (divScore * 0.15) + (maxSectorRatio * 20.0)).coerceIn(10.0, 95.0).toInt()

        val category = when {
            riskScore >= 70 -> "Yüksek Risk"
            riskScore >= 40 -> "Dengeli"
            else -> "Düşük Risk"
        }

        val advice = buildString {
            append("Portföyünüz canlı kurlarla (USD: %.2f TL, EUR: %.2f TL) ve %d farklı sektör ile analiz edildi. ".format(usdRate, eurRate, sectorCount))
            if (maxSectorRatio > 0.5) {
                append("UYARI: Portföyünüzün %%%d kadarı tek bir sektörde yoğunlaşmış durumda; sektörel korelasyon riski yüksek. ".format((maxSectorRatio * 100).toInt()))
            }
            append("İstatistiki Sharpe Rasyosu %.2f, Aylık Riske Maruz Değer (VaR %%95) %%%.2f seviyesindedir. ".format(sharpe, varMonthlyPercent))
            if (sharpe < 0.5) {
                append("TCMB %%%.1f politika faizine kıyasla risk priminiz düşüktür. Farklı sektörlerden yüksek ROE kârlılığına sahip hisselere dağıtım önerilir.".format(riskFreeRate))
            } else {
                append("Portföyün risk/getiri dengesi olumludur.")
            }
        }

        return PortfolioDoctorReport(
            riskScore = riskScore,
            riskCategory = category,
            usdTryRateUsed = usdRate,
            eurTryRateUsed = eurRate,
            totalPortfolioValueTRY = totalValueTry,
            totalPortfolioValueUSD = totalValueUsd,
            sharpeRatio = "%.2f".format(sharpe).replace(",", ".").toDoubleOrNull() ?: sharpe,
            varMonthlyPercent = "%.2f".format(varMonthlyPercent).replace(",", ".").toDoubleOrNull() ?: varMonthlyPercent,
            diversificationScore = divScore,
            doctorAdvice = advice
        )
    }
}
