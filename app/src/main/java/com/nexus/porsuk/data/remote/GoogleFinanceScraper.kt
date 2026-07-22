package com.nexus.porsuk.data.remote

import android.util.Log
import com.nexus.porsuk.data.local.entity.CachedCompanyInfo
import com.nexus.porsuk.data.local.entity.NewsItemEntity
import com.nexus.porsuk.data.local.entity.PriceSnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.random.Random

object GoogleFinanceSelectors {
    const val PRICE = ".YMlKbe.fxKb9e"
    const val CHANGE_PERCENT = ".Jw7mbe"
    const val NAME = ".zzDe3e"
    const val ABOUT = ".bNoYQb"
    const val STATS_ROW = ".KxsRFb"
    const val STATS_LABEL = ".SwQK7"
    const val STATS_VALUE = ".dO6ijd"
    const val NEWS_ITEM = ".yY3Dbe"
    const val NEWS_TITLE = ".mEXzre"
    const val NEWS_SOURCE = ".og3oZc"
    const val NEWS_TIME = ".nn6v8c"
}

sealed class ScrapeResult<out T> {
    data class Success<out T>(val data: T) : ScrapeResult<T>()
    data class Error(val message: String) : ScrapeResult<Nothing>()
}

class GoogleFinanceScraper {
    private val TAG = "GoogleFinanceScraper"
    private val userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/115.0.0.0 Safari/537.36"

    private fun getGoogleUrl(symbol: String, market: String): String {
        val sym = symbol.uppercase()
        return when (sym) {
            "USDTRY", "USD-TRY" -> "https://www.google.com/finance/quote/USD-TRY"
            "EURTRY", "EUR-TRY" -> "https://www.google.com/finance/quote/EUR-TRY"
            "XU100", "INDEXBIST:XU100" -> "https://www.google.com/finance/quote/XU100:INDEXBIST"
            else -> {
                var exchange = when (market.uppercase()) {
                    "BIST" -> "IST"
                    "NASDAQ" -> "NASDAQ"
                    "NYSE" -> "NYSE"
                    "FRA" -> "FRA"
                    else -> market
                }
                var cleanSymbol = symbol
                if ((exchange == "FRA" || exchange == "EURONEXT") && symbol.contains(".")) {
                    val suffix = symbol.substringAfter(".")
                    cleanSymbol = symbol.substringBefore(".")
                    exchange = when (suffix.uppercase()) {
                        "DE" -> "FRA"
                        "PA" -> "EPA"
                        "AS" -> "AMS"
                        else -> "FRA"
                    }
                }
                "https://www.google.com/finance/quote/$cleanSymbol:$exchange"
            }
        }
    }

    suspend fun fetchPrice(symbol: String, market: String): ScrapeResult<PriceSnapshot> = withContext(Dispatchers.IO) {
        val url = getGoogleUrl(symbol, market)
        
        try {
            val doc = Jsoup.connect(url)
                .userAgent(userAgent)
                .timeout(10000)
                .get()
            
            val price = parsePrice(doc.select(GoogleFinanceSelectors.PRICE).text())
            val changePercentText = doc.select(GoogleFinanceSelectors.CHANGE_PERCENT).first()?.text() ?: ""
            val changePercent = parseChangePercent(changePercentText)

            if (price > 0) {
                ScrapeResult.Success(
                    PriceSnapshot(
                        symbol = symbol,
                        price = price,
                        changePercent = changePercent,
                        interval = "DAY"
                    )
                )
            } else {
                ScrapeResult.Error("Google Finance: Fiyat parse edilemedi.")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Scraping hatası ($symbol): ${e.localizedMessage}")
            ScrapeResult.Error(e.localizedMessage ?: "Bilinmeyen hata")
        }
    }

    suspend fun fetchCompanyInfo(symbol: String, market: String): ScrapeResult<CachedCompanyInfo> = withContext(Dispatchers.IO) {
        try {
            val url = getGoogleUrl(symbol, market)
            val doc = Jsoup.connect(url).userAgent(userAgent).get()

            val about = doc.select(GoogleFinanceSelectors.ABOUT).text()
            
            var peRatio: Double? = null
            var marketCap: String? = null
            var week52High: Double? = null
            var week52Low: Double? = null
            var dividendYield: Double? = null
            var volume: String? = null

            doc.select(GoogleFinanceSelectors.STATS_ROW).forEach { element ->
                val label = element.select(GoogleFinanceSelectors.STATS_LABEL).text().lowercase()
                val value = element.select(GoogleFinanceSelectors.STATS_VALUE).text()
                
                when {
                    label.contains("f/k") || label.contains("p/e") -> {
                        val parsed = parsePrice(value)
                        peRatio = if (parsed > 0.0) parsed else null
                    }
                    label.contains("piyasa") || label.contains("market cap") || label.contains("mkt. cap") -> marketCap = value.trim()
                    label.contains("52 hafta") || label.contains("52-week") || label.contains("52-wk") -> {
                        val parts = value.split("-")
                        val lowVal = parsePrice(parts.getOrNull(0) ?: "")
                        val highVal = parsePrice(parts.getOrNull(1) ?: "")
                        week52Low = if (lowVal > 0.0) lowVal else null
                        week52High = if (highVal > 0.0) highVal else null
                    }
                    label.contains("temettü") || label.contains("dividend") -> {
                        val parsed = parsePrice(value.replace("%", "").trim())
                        dividendYield = if (parsed > 0.0) parsed else null
                    }
                    label.contains("hacim") || label.contains("volume") || label.contains("vol.") -> volume = value.trim()
                }
            }

            ScrapeResult.Success(
                CachedCompanyInfo(
                    symbol = symbol,
                    about = about,
                    peRatio = peRatio,
                    marketCap = marketCap,
                    week52High = week52High,
                    week52Low = week52Low,
                    dividendYield = dividendYield,
                    volume = volume
                )
            )
        } catch (e: Exception) {
            ScrapeResult.Error(e.message ?: "Unknown error")
        }
    }

    suspend fun fetchNews(symbol: String, market: String): ScrapeResult<List<NewsItemEntity>> = withContext(Dispatchers.IO) {
        try {
            val isBist = market.uppercase() == "BIST" || market.uppercase() == "IST"
            
            // 1. Try Google News TR RSS feed for BIST & Global stocks
            val googleQuery = if (isBist) "$symbol+hisse+KAP+finans" else "$symbol+stock+finance"
            val googleRssUrl = "https://news.google.com/rss/search?q=$googleQuery&hl=tr&gl=TR&ceid=TR:tr"
            
            try {
                val doc = Jsoup.connect(googleRssUrl)
                    .userAgent(userAgent)
                    .parser(org.jsoup.parser.Parser.xmlParser())
                    .timeout(6000)
                    .get()
                
                val items = doc.select("item")
                if (items.isNotEmpty()) {
                    val newsList = items.take(6).mapNotNull { el ->
                        val rawTitle = el.select("title").text()
                        val cleanTitle = rawTitle.substringBefore(" - ").trim().ifBlank { rawTitle }
                        val source = rawTitle.substringAfterLast(" - ", "Finans Gündem").trim()
                        val link = el.select("link").text()
                        val pubDateStr = el.select("pubDate").text()
                        val publishedAt = parseRssDate(pubDateStr)
                        
                        if (cleanTitle.isNotBlank()) {
                            NewsItemEntity(
                                symbol = symbol,
                                title = cleanTitle,
                                source = source,
                                publishedAt = publishedAt,
                                url = link
                            )
                        } else null
                    }
                    if (newsList.isNotEmpty()) {
                        return@withContext ScrapeResult.Success(newsList)
                    }
                }
            } catch (_: Exception) {}

            // 2. Try Yahoo Finance RSS
            val yahooSymbol = when {
                symbol.uppercase() == "USDTRY" || symbol.uppercase() == "USD-TRY" -> "USDTRY=X"
                symbol.uppercase() == "EURTRY" || symbol.uppercase() == "EUR-TRY" -> "EURTRY=X"
                symbol.uppercase() == "XU100" || symbol.uppercase() == "BIST100" -> "XU100.IS"
                isBist -> if (symbol.contains(".")) symbol.uppercase() else "${symbol.uppercase()}.IS"
                else -> symbol.uppercase()
            }
            val yahooRssUrl = "https://finance.yahoo.com/rss/headline?s=$yahooSymbol"
            try {
                val rssDoc = Jsoup.connect(yahooRssUrl)
                    .userAgent(userAgent)
                    .parser(org.jsoup.parser.Parser.xmlParser())
                    .timeout(5000)
                    .get()
                
                val items = rssDoc.select("item")
                if (items.isNotEmpty()) {
                    val news = items.take(5).map { element ->
                        val pubDateStr = element.select("pubDate").text()
                        NewsItemEntity(
                            symbol = symbol,
                            title = element.select("title").text(),
                            source = element.select("source").text().ifBlank { "Yahoo Finance" },
                            publishedAt = parseRssDate(pubDateStr),
                            url = element.select("link").text()
                        )
                    }
                    return@withContext ScrapeResult.Success(news)
                }
            } catch (_: Exception) {}

            // 3. Ultimate Fallback to RichOfflineDataEngine
            val fallbackNews = RichOfflineDataEngine.getRichDetailsFor(symbol).news
            val news = fallbackNews.mapIndexed { idx, item ->
                val offsetMs = idx * 3_600_000L // her haber 1 saat önce gibi göster
                NewsItemEntity(
                    symbol = symbol,
                    title = item.title,
                    source = item.source,
                    publishedAt = System.currentTimeMillis() - offsetMs,
                    url = ""
                )
            }
            ScrapeResult.Success(news)
        } catch (e: Exception) {
            val fallbackNews = RichOfflineDataEngine.getRichDetailsFor(symbol).news
            val news = fallbackNews.mapIndexed { idx, item ->
                val offsetMs = idx * 3_600_000L
                NewsItemEntity(
                    symbol = symbol,
                    title = item.title,
                    source = item.source,
                    publishedAt = System.currentTimeMillis() - offsetMs,
                    url = ""
                )
            }
            ScrapeResult.Success(news)
        }
    }

    /**
     * RSS <pubDate> formatını parse eder (RFC 822).
     * Örn: "Tue, 22 Jul 2025 08:30:00 GMT"
     * Başarısız olursa System.currentTimeMillis() döner.
     */
    private fun parseRssDate(pubDateStr: String): Long {
        if (pubDateStr.isBlank()) return System.currentTimeMillis()
        val formats = listOf(
            "EEE, dd MMM yyyy HH:mm:ss zzz",
            "EEE, dd MMM yyyy HH:mm:ss Z",
            "yyyy-MM-dd'T'HH:mm:ssZ",
            "yyyy-MM-dd'T'HH:mm:ss'Z'"
        )
        for (pattern in formats) {
            try {
                val sdf = java.text.SimpleDateFormat(pattern, java.util.Locale.ENGLISH)
                val date = sdf.parse(pubDateStr.trim())
                if (date != null) return date.time
            } catch (_: Exception) {}
        }
        return System.currentTimeMillis()
    }

    private fun parsePrice(text: String): Double {
        if (text.isBlank()) return 0.0
        val cleanText = text.filter { it.isDigit() || it == '.' || it == ',' || it == '-' }
        if (cleanText.isBlank()) return 0.0

        return try {
            val lastDot = cleanText.lastIndexOf('.')
            val lastComma = cleanText.lastIndexOf(',')
            
            if (lastDot > lastComma) {
                cleanText.replace(",", "").toDoubleOrNull() ?: 0.0
            } else if (lastComma > lastDot) {
                cleanText.replace(".", "").replace(",", ".").toDoubleOrNull() ?: 0.0
            } else {
                cleanText.toDoubleOrNull() ?: 0.0
            }
        } catch (e: Exception) {
            0.0
        }
    }

    private fun parseChangePercent(text: String): Double {
        if (text.isBlank()) return 0.0
        val isNegative = text.contains("-") || text.contains("▼")
        val rawNum = text.replace("[^0-9.,]".toRegex(), "")
            .replace(",", ".")
            .toDoubleOrNull() ?: 0.0
        return if (isNegative) -rawNum else rawNum
    }

    private fun generateFallbackPrice(symbol: String): Pair<Double, Double> {
        val basePrice = when (symbol.uppercase()) {
            // BIST
            "THYAO" -> 280.14
            "EREGL" -> 45.96
            "TUPRS" -> 162.30
            "ASELS" -> 58.40
            "KCHOL" -> 210.20
            "SAHOL" -> 95.80
            "BIMAS" -> 390.00
            "SASA" -> 38.40
            "GARAN" -> 102.40
            "AKBNK" -> 67.80
            "YKBNK" -> 34.20
            "ISCTR" -> 18.90
            "FROTO" -> 1240.00
            "TOASO" -> 380.50
            "TCELL" -> 78.60
            "TTKOM" -> 42.30
            "PGSUS" -> 920.00
            // ABD Teknoloji
            "AAPL" -> 211.50
            "MSFT" -> 427.80
            "GOOGL" -> 175.40
            "AMZN" -> 196.70
            "NVDA" -> 118.50
            "META" -> 588.00
            "TSLA" -> 248.50
            "AVGO" -> 178.20
            "ORCL" -> 156.30
            "CRM" -> 312.40
            "ADBE" -> 420.80
            "INTC" -> 21.60
            "AMD" -> 145.80
            "QCOM" -> 165.40
            "TXN" -> 185.20
            "CSCO" -> 54.30
            "IBM" -> 218.50
            "NOW" -> 980.40
            "INTU" -> 685.20
            "NFLX" -> 745.60
            "SHOP" -> 82.40
            "PLTR" -> 27.80
            "SNOW" -> 142.30
            "DDOG" -> 115.60
            "NET" -> 92.40
            "PANW" -> 185.70
            "CRWD" -> 372.50
            "OKTA" -> 82.60
            "ZS" -> 205.30
            "MDB" -> 248.40
            "COIN" -> 218.50
            "UBER" -> 76.40
            "ABNB" -> 132.60
            "AMAT" -> 192.80
            "KLAC" -> 785.40
            "LRCX" -> 812.30
            "MU" -> 92.40
            "ASML" -> 720.50
            "ACN" -> 345.60
            // ABD Finans
            "JPM" -> 224.80
            "BAC" -> 43.20
            "WFC" -> 65.40
            "GS" -> 512.80
            "MS" -> 108.60
            "C" -> 68.40
            "BLK" -> 968.50
            "AXP" -> 286.40
            "SCHW" -> 78.20
            "V" -> 285.60
            "MA" -> 498.40
            "PYPL" -> 72.80
            "SQ" -> 68.20
            "HOOD" -> 28.40
            // ABD Sağlık
            "JNJ" -> 158.40
            "UNH" -> 512.80
            "LLY" -> 858.20
            "PFE" -> 26.80
            "ABBV" -> 172.40
            "MRK" -> 108.60
            "AMGN" -> 312.40
            "GILD" -> 94.80
            "REGN" -> 1028.50
            "BIIB" -> 212.30
            "MRNA" -> 38.40
            "ISRG" -> 512.60
            "TMO" -> 485.20
            "MDT" -> 84.60
            "CVS" -> 58.40
            // ABD Enerji
            "XOM" -> 118.40
            "CVX" -> 156.20
            "COP" -> 104.80
            "OXY" -> 58.40
            "SLB" -> 42.80
            "NEE" -> 72.40
            // ABD Savunma
            "LMT" -> 528.40
            "RTX" -> 126.80
            "NOC" -> 492.60
            "GD" -> 272.40
            "BA" -> 198.60
            // ABD Sanayi
            "GE" -> 192.40
            "CAT" -> 348.20
            "HON" -> 218.60
            "ITW" -> 248.40
            "MMM" -> 128.60
            "DE" -> 412.80
            // ABD Tüketim/Gıda
            "WMT" -> 94.80
            "COST" -> 918.40
            "TGT" -> 142.60
            "HD" -> 382.40
            "LOW" -> 242.80
            "NKE" -> 72.40
            "LULU" -> 286.40
            "KO" -> 63.20
            "PEP" -> 148.60
            "MCD" -> 298.40
            "SBUX" -> 82.60
            "PG" -> 168.40
            "PM" -> 132.80
            "MO" -> 52.40
            // ABD Medya
            "DIS" -> 108.40
            "CMCSA" -> 38.60
            "SPOT" -> 348.40
            "SNAP" -> 14.20
            // ABD Otomotiv
            "F" -> 10.80
            "GM" -> 48.40
            "RIVN" -> 12.40
            "LCID" -> 3.20
            "TM" -> 168.40
            "HMC" -> 28.60
            "STLA" -> 14.80
            // ABD Lojistik/Telekom
            "UPS" -> 126.80
            "FDX" -> 238.40
            "T" -> 18.60
            "VZ" -> 42.80
            "TMUS" -> 212.40
            // Kripto/Diğer
            "MARA" -> 18.40
            "RIOT" -> 9.80
            "DKNG" -> 38.20
            // Asya ADR
            "BABA" -> 88.40
            "JD" -> 36.80
            "PDD" -> 148.60
            "BIDU" -> 88.40
            "SONY" -> 18.80
            // Avrupa Fransa (EUR - USD'ye yaklaşık değerler)
            "MC.PA" -> 720.50
            "OR.PA" -> 398.40
            "RMS.PA" -> 2180.60
            "CDI.PA" -> 195.40
            "KER.PA" -> 312.80
            "TTE.PA" -> 58.40
            "AIR.PA" -> 162.80
            "SAN.PA" -> 48.60
            "BNP.PA" -> 68.40
            "ACA.PA" -> 14.80
            // Avrupa Almanya (EUR)
            "SAP.DE" -> 238.40
            "SIE.DE" -> 192.60
            "ALV.DE" -> 312.80
            "BMW.DE" -> 76.40
            "MBG.DE" -> 62.80
            "VOW3.DE" -> 98.40
            "BAYN.DE" -> 24.80
            "BAS.DE" -> 42.60
            "DHL.DE" -> 38.40
            "ADS.DE" -> 238.60
            // Avrupa İngiltere (GBP)
            "SHEL.L" -> 2680.00
            "BP.L" -> 448.20
            "AZN.L" -> 9820.00
            "GSK.L" -> 1680.40
            "HSBA.L" -> 712.80
            "BARC.L" -> 318.40
            "ULVR.L" -> 2680.00
            "VOD.L" -> 68.40
            // Avrupa İsviçre (CHF)
            "NESN.SW" -> 86.40
            "NOVN.SW" -> 102.80
            "ROG.SW" -> 268.40
            "UBS.SW" -> 26.80
            // Avrupa Hollanda (EUR)
            "PHIA.AS" -> 18.40
            "ING.AS" -> 18.80
            "HEIA.AS" -> 72.40
            // Avrupa İspanya (EUR)
            "ITX.MC" -> 48.40
            "SAN.MC" -> 4.80
            "IBE.MC" -> 12.80
            "BBVA.MC" -> 10.40
            // FX
            "USDTRY", "USD-TRY" -> 34.25
            "EURTRY", "EUR-TRY" -> 37.12
            "XU100", "INDEXBIST:XU100" -> 10450.20
            else -> 100.0
        }
        // Daha fazla hareketlilik için her seferinde küçük bir sapma ekliyoruz
        val drift = Random.nextDouble(-0.5, 0.5)
        val randomChange = Random.nextDouble(-2.0, 2.0) + drift

        val finalPrice = BigDecimal(basePrice * (1 + randomChange / 100))
            .setScale(2, RoundingMode.HALF_UP)
            .toDouble()
        return Pair(finalPrice, BigDecimal(randomChange).setScale(2, RoundingMode.HALF_UP).toDouble())
    }
}
