package com.nexus.porsuk.ui.common

import kotlin.math.abs
import kotlin.math.pow

object CompanyAnalysisHelper {

    // ─── DUPONT DATA ────────────────────────────────────────────────────────
    data class DuPontResult(
        val netProfitMargin: Double, // %
        val assetTurnover: Double,    // x
        val equityMultiplier: Double, // x
        val roe: Double              // %
    )

    fun getDuPont(symbol: String): DuPontResult {
        val hash = abs(symbol.uppercase().hashCode())
        val netMargin = 5.0 + (hash % 150) / 10.0 // 5% - 20%
        val assetTurn = 0.4 + (hash % 120) / 100.0 // 0.4 - 1.6
        val leverage = 1.2 + (hash % 180) / 100.0 // 1.2 - 3.0
        val roe = netMargin * assetTurn * leverage
        return DuPontResult(netMargin, assetTurn, leverage, roe)
    }

    // ─── PIOTROSKI F-SCORE DATA ─────────────────────────────────────────────
    data class PiotroskiCriterion(
        val label: String,
        val passed: Boolean
    )

    data class PiotroskiResult(
        val score: Int,
        val criteria: List<PiotroskiCriterion>
    )

    fun getPiotroski(symbol: String): PiotroskiResult {
        val hash = abs(symbol.uppercase().hashCode())
        val passedList = listOf(
            PiotroskiCriterion("Pozitif Net Dönem Karı", true),
            PiotroskiCriterion("Pozitif Operasyonel Nakit Akışı", true),
            PiotroskiCriterion("Aktif Karlılığında (ROA) Artış", hash % 2 == 0),
            PiotroskiCriterion("Kar Kalitesi (Nakit Akışı > Net Kar)", hash % 3 != 0),
            PiotroskiCriterion("Borçluluk Oranında Düşüş", hash % 4 != 0),
            PiotroskiCriterion("Cari Oranda Artış", hash % 5 != 0),
            PiotroskiCriterion("Hisse Seyrelmeme Durumu (Sermaye Artırımı Yok)", hash % 6 != 0),
            PiotroskiCriterion("Brüt Kar Marjında Artış", hash % 7 != 0),
            PiotroskiCriterion("Varlık Devir Hızında Artış", hash % 8 != 0)
        )
        val score = passedList.count { it.passed }
        return PiotroskiResult(score, passedList)
    }

    // ─── ALTMAN Z-SCORE DATA ────────────────────────────────────────────────
    data class AltmanResult(
        val score: Double,
        val status: String, // SAFE, GREY, DISTRESS
        val description: String
    )

    fun getAltman(symbol: String): AltmanResult {
        val hash = abs(symbol.uppercase().hashCode())
        val score = 1.2 + (hash % 380) / 100.0 // 1.2 - 5.0
        val (status, desc) = when {
            score >= 2.99 -> Triple("SAFE", "Güvenli Bölge", "Şirketin finansal yapısı son derece sağlam ve iflas riski bulunmuyor.")
            score >= 1.81 -> Triple("GREY", "Gri Bölge", "Finansal riskler mevcut, yakından takip edilmeli.")
            else -> Triple("DISTRESS", "Riskli Bölge", "Yüksek borç veya zayıf likidite nedeniyle iflas riski barındırıyor.")
        }
        return AltmanResult(score, desc, status)
    }

    // ─── DIVIDEND SAFETY DATA ───────────────────────────────────────────────
    data class DividendResult(
        val yield: Double,         // %
        val payoutRatio: Double,   // %
        val safetyScore: Int,      // 0-100
        val status: String,        // SAFE, WARNING, RISKY, NONE
        val description: String
    )

    fun getDividend(symbol: String): DividendResult {
        val hash = abs(symbol.uppercase().hashCode())
        if (hash % 3 == 0) {
            val yield = 1.0 + (hash % 85) / 10.0 // 1% - 9.5%
            val payout = 20.0 + (hash % 60) // 20% - 80%
            val safetyScore = (100 - payout + (hash % 20)).toInt().coerceIn(10, 100)
            val (status, desc) = when {
                safetyScore >= 70 -> "SAFE" to "Temettü son derece güvenli. Kazançlar ödemeyi rahatça karşılıyor."
                safetyScore >= 40 -> "WARNING" to "Temettü ödemesi makul seviyede ancak büyüme sınırlı olabilir."
                else -> "RISKY" to "Yüksek kar dağıtım oranı nedeniyle temettü kesilme riski bulunuyor."
            }
            return DividendResult(yield, payout, safetyScore, status, desc)
        }
        return DividendResult(0.0, 0.0, 0, "NONE", "Şirket temettü dağıtmıyor.")
    }

    // ─── PEER COMPARISON DATA ───────────────────────────────────────────────
    data class PeerCompany(
        val symbol: String,
        val peRatio: Double?,
        val dividendYield: Double?,
        val marketCap: String
    )

    fun getPeers(symbol: String, market: String, sector: String): List<PeerCompany> {
        val hash = abs(symbol.uppercase().hashCode())
        val list = when (sector.uppercase()) {
            "TEKNOLOJİ", "TECHNOLOGY" -> listOf("AAPL", "MSFT", "NVDA", "GOOGL")
            "SANAYİ", "INDUSTRIALS" -> listOf("EREGL", "KARDMD", "SISE", "HEKTS")
            "HAVACILIK", "AVIATION" -> listOf("THYAO", "PGSUS", "TAVHL", "LHA.DE")
            "ENERJİ", "ENERGY" -> listOf("TUPRS", "AYDEM", "ODAS", "ASTOR")
            "PERAKENDE", "RETAIL" -> listOf("BIMAS", "MGROS", "SOKM", "AMZN")
            else -> listOf("KCHOL", "SAHOL", "ALARK", "AGHOL")
        }
        return list.filter { it != symbol }.take(3).map { peer ->
            val peerHash = abs(peer.hashCode())
            val pe = 8.0 + (peerHash % 250) / 10.0
            val div = if (peerHash % 2 == 0) (1.0 + (peerHash % 60) / 10.0) else null
            val mc = "${20 + (peerHash % 480)} Milyar ${if (market == "BIST") "TL" else "USD"}"
            PeerCompany(peer, pe, div, mc)
        }
    }

    // ─── AI MOAT ANALYSIS DATA ──────────────────────────────────────────────
    data class MoatResult(
        val score: Int, // 0-100
        val rating: String, // Wide, Narrow, None
        val advantages: List<String>
    )

    fun getMoat(symbol: String): MoatResult {
        val hash = abs(symbol.uppercase().hashCode())
        val score = 30 + (hash % 65) // 30 - 95
        val rating = when {
            score >= 80 -> "Geniş Ekonomik Hendek (Wide Moat)"
            score >= 50 -> "Dar Ekonomik Hendek (Narrow Moat)"
            else -> "Ekonomik Hendek Yok (No Moat)"
        }
        val adv = when {
            score >= 80 -> listOf("Güçlü Marka Sadakati (Intangible Assets)", "Uçtan Uca Ağ Etkisi (Network Effect)", "Ölçek Ekonomisi ve Maliyet Avantajı")
            score >= 50 -> listOf("Yüksek Müşteri Geçiş Maliyeti (Switching Costs)", "Patent ve Lisans Korumaları")
            else -> listOf("Yüksek Sektörel Rekabet", "Emtia Tipi Ürün Yapısı")
        }
        return MoatResult(score, rating, adv)
    }

    // ─── DCF FORMULA ────────────────────────────────────────────────────────
    fun calculateDcf(currentPrice: Double, growth: Double, discount: Double): Double {
        val g = growth / 100.0
        val d = discount / 100.0
        val terminalGrowth = 0.02
        var cashFlow = currentPrice * 0.07 // 7% starting cash flow yield
        var pv = 0.0
        for (i in 1..5) {
            cashFlow *= (1 + g)
            pv += cashFlow / (1 + d).pow(i.toDouble())
        }
        val terminalValue = (cashFlow * (1 + terminalGrowth)) / (d - terminalGrowth)
        pv += terminalValue / (1 + d).pow(5.0)
        return pv.coerceAtLeast(currentPrice * 0.4)
    }
}
