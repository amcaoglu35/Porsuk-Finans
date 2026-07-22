package com.nexus.porsuk.data.remote

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import java.util.Locale

/**
 * Market Intelligence Engine for Porsuk Finans.
 * Automatically collects data for 12 global macro assets (NASDAQ, SP500, Dow Jones, VIX, DXY, US 10Y, Gold, Silver, Oil, Bitcoin, Ethereum)
 * and summarizes them into a SINGLE CONCISE PARAGRAPH on the Kotlin side to minimize Gemini API token usage.
 */
data class MacroAssetSnapshot(
    val key: String,
    val name: String,
    val yahooSymbol: String,
    var price: Double = 0.0,
    var changePercent: Double = 0.0
)

object MarketIntelligenceEngine {

    private val yahooService = YahooFinancePublicService()
    private var cachedParagraph: String = ""
    private var lastFetchTime: Long = 0L
    private const val CACHE_TTL_MS = 15 * 60 * 1000L // 15 minutes cache

    private val targetAssets = listOf(
        MacroAssetSnapshot("nasdaq", "NASDAQ", "^IXIC"),
        MacroAssetSnapshot("sp500", "S&P 500", "^GSPC"),
        MacroAssetSnapshot("dow", "Dow Jones", "^DJI"),
        MacroAssetSnapshot("vix", "VIX", "^VIX"),
        MacroAssetSnapshot("dxy", "DXY", "DX-Y.NYB"),
        MacroAssetSnapshot("us10y", "ABD 10Y Tahvil", "^TNX"),
        MacroAssetSnapshot("gold", "Altın", "GC=F"),
        MacroAssetSnapshot("silver", "Gümüş", "SI=F"),
        MacroAssetSnapshot("oil", "Petrol", "CL=F"),
        MacroAssetSnapshot("btc", "Bitcoin", "BTC-USD"),
        MacroAssetSnapshot("eth", "Ethereum", "ETH-USD")
    )

    /**
     * Fetches or retrieves cached single-paragraph market intelligence summary (0 AI token overhead).
     */
    suspend fun getMarketSummaryParagraph(): String = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        if (cachedParagraph.isNotBlank() && (now - lastFetchTime) < CACHE_TTL_MS) {
            return@withContext cachedParagraph
        }

        try {
            val deferredList = targetAssets.map { asset ->
                async {
                    val result = yahooService.fetchPrice(asset.yahooSymbol, "US")
                    if (result is ScrapeResult.Success) {
                        asset.price = result.data.price
                        asset.changePercent = result.data.changePercent
                    }
                    asset
                }
            }

            val fetchedAssets = deferredList.awaitAll()
            val paragraph = buildSingleParagraphSummary(fetchedAssets)
            cachedParagraph = paragraph
            lastFetchTime = now
            paragraph
        } catch (e: Exception) {
            Log.e("MarketIntelligenceEngine", "Hata: ${e.localizedMessage}")
            if (cachedParagraph.isNotBlank()) cachedParagraph else buildFallbackParagraph()
        }
    }

    /**
     * Summarizes all 11 global macro metrics into a SINGLE CONCISE PARAGRAPH.
     */
    private fun buildSingleParagraphSummary(assets: List<MacroAssetSnapshot>): String {
        val map = assets.associateBy { it.key }

        val nasdaq = map["nasdaq"]
        val sp500 = map["sp500"]
        val dow = map["dow"]
        val vix = map["vix"]
        val dxy = map["dxy"]
        val us10y = map["us10y"]
        val gold = map["gold"]
        val silver = map["silver"]
        val oil = map["oil"]
        val btc = map["btc"]
        val eth = map["eth"]

        val formatChange = { a: MacroAssetSnapshot? ->
            if (a != null && a.price > 0) {
                val sign = if (a.changePercent >= 0) "%+" else "%"
                "$sign${String.format(Locale.US, "%.2f", a.changePercent)}"
            } else "Nötr"
        }

        val formatVal = { a: MacroAssetSnapshot? ->
            if (a != null && a.price > 0) String.format(Locale.US, "%.1f", a.price) else "N/A"
        }

        val sb = StringBuilder()
        sb.append("[KÜRESEL MAKRO VE PİYASA HABER NABZI]: ")
        sb.append("ABD endeksleri (NASDAQ: ").append(formatChange(nasdaq))
        sb.append(", S&P500: ").append(formatChange(sp500))
        sb.append(", Dow Jones: ").append(formatChange(dow))
        sb.append(") seyrederken, VIX korku indeksi ").append(formatVal(vix)).append(" seviyesindedir. ")

        sb.append("DXY Dolar İndeksi ").append(formatVal(dxy))
        sb.append(" ve ABD 10Y tahvil faizi %").append(formatVal(us10y)).append(" ile makro dengeyi temsil ediyor. ")

        sb.append("Emtia cephesinde Altın $").append(formatVal(gold)).append(" (").append(formatChange(gold)).append(")")
        sb.append(", Gümüş $").append(formatVal(silver))
        sb.append(" ve Petrol $").append(formatVal(oil)).append(" varil seviyesindedir. ")

        sb.append("Kripto piyasasında ise Bitcoin $").append(formatVal(btc)).append(" (").append(formatChange(btc)).append(")")
        sb.append(" ve Ethereum $").append(formatVal(eth)).append(" seviyeleriyle global risk iştahını yansıtmaktadır.")

        return sb.toString()
    }

    private fun buildFallbackParagraph(): String {
        return "[KÜRESEL MAKRO VE PİYASA HABER NABZI]: ABD endeksleri (NASDAQ, S&P 500, Dow Jones), VIX volatilite endeksi, DXY dolar gücü, ABD 10Y tahvil faizi, Altın/Gümüş emtiaları ve Kripto (Bitcoin/Ethereum) varlıkları küresel piyasalarda dengeli bir görünüm sergilemektedir."
    }
}
