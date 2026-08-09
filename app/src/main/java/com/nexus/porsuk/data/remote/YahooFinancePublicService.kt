package com.nexus.porsuk.data.remote

import android.util.Log
import com.nexus.porsuk.data.local.entity.CachedCompanyInfo
import com.nexus.porsuk.data.local.entity.PriceSnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.text.DecimalFormat

class YahooFinancePublicService {
    private val client = OkHttpClient()
    private val TAG = "YahooFinancePublicService"
    private val userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/115.0.0.0 Safari/537.36"

    private fun getYahooSymbol(symbol: String, market: String): String {
        val sym = symbol.uppercase()
        return when {
            sym == "USDTRY" || sym == "USD-TRY" -> "USDTRY=X"
            sym == "EURTRY" || sym == "EUR-TRY" -> "EURTRY=X"
            sym == "GBPTRY" || sym == "GBP-TRY" -> "GBPTRY=X"
            sym == "CHFTRY" || sym == "CHF-TRY" -> "CHFTRY=X"
            sym == "JPYTRY" || sym == "JPY-TRY" -> "JPYTRY=X"
            sym == "CADTRY" || sym == "CAD-TRY" -> "CADTRY=X"
            sym == "AUDTRY" || sym == "AUD-TRY" -> "AUDTRY=X"
            sym == "EURUSD" || sym == "EUR-USD" -> "EURUSD=X"
            sym.endsWith("TRY") && sym.length == 6 -> "$sym=X"
            sym == "XU100" || sym == "INDEXBIST:XU100" || sym == "BIST100" -> "XU100.IS"
            sym == "XU030" || sym == "INDEXBIST:XU030" || sym == "BIST30" -> "XU030.IS"
            sym == "DAX" || sym == "DAX40" || sym == "^GDAXI" -> "^GDAXI"
            sym == "FTSE" || sym == "FTSE100" || sym == "^FTSE" -> "^FTSE"
            sym == "N225" || sym == "NIKKEI" || sym == "NIKKEI225" || sym == "^N225" -> "^N225"
            sym == "HSI" || sym == "HANGSENG" || sym == "^HSI" -> "^HSI"
            sym == "SP500" || sym == "^GSPC" -> "^GSPC"
            sym == "NASDAQ" || sym == "^IXIC" -> "^IXIC"
            sym == "DOW" || sym == "DJI" || sym == "^DJI" -> "^DJI"
            market.uppercase() == "BIST" || market.uppercase() == "IST" -> if (sym.contains(".")) sym else "$sym.IS"
            market.uppercase() == "FRA" || market.uppercase() == "EURONEXT" -> if (sym.contains(".")) sym else "$sym.DE"
            else -> sym
        }
    }

    suspend fun fetchPrice(symbol: String, market: String): ScrapeResult<PriceSnapshot> = withContext(Dispatchers.IO) {
        val yahooSymbol = getYahooSymbol(symbol, market)
        val url = "https://query1.finance.yahoo.com/v8/finance/chart/$yahooSymbol?interval=1d&range=1d"
        
        try {
            val request = Request.Builder()
                .url(url)
                .header("User-Agent", userAgent)
                .build()
            
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext ScrapeResult.Error("Yahoo Public API Hatası: ${response.code}")
                }
                
                val body = response.body?.string() ?: return@withContext ScrapeResult.Error("Boş yanıt")
                val json = JSONObject(body)
                val chart = json.optJSONObject("chart")
                val result = chart?.optJSONArray("result")
                
                if (result != null && result.length() > 0) {
                    val data = result.getJSONObject(0)
                    val meta = data.getJSONObject("meta")
                    
                    val currentPrice = meta.optDouble("regularMarketPrice", 0.0)
                    val prevClose = meta.optDouble("chartPreviousClose", 0.0)
                    
                    val changePercent = if (prevClose > 0.0) {
                        ((currentPrice - prevClose) / prevClose) * 100.0
                    } else {
                        0.0
                    }

                    ScrapeResult.Success(
                        PriceSnapshot(symbol = symbol, price = currentPrice, changePercent = changePercent, interval = "DAY")
                    )
                } else {
                    ScrapeResult.Error("Yahoo Public: Sembol bulunamadı: $yahooSymbol")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Yahoo Public API Hatası ($symbol): ${e.localizedMessage}")
            ScrapeResult.Error(e.localizedMessage ?: "Bilinmeyen hata")
        }
    }

    suspend fun fetchHistoricalPrices(symbol: String, market: String, range: String = "1mo", interval: String = "1d"): ScrapeResult<List<Double>> = withContext(Dispatchers.IO) {
        val yahooSymbol = getYahooSymbol(symbol, market)
        val url = "https://query1.finance.yahoo.com/v8/finance/chart/$yahooSymbol?interval=$interval&range=$range"
        
        try {
            val request = Request.Builder()
                .url(url)
                .header("User-Agent", userAgent)
                .build()
            
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@withContext ScrapeResult.Error("Yahoo History API Hatası: ${response.code}")
                
                val body = response.body?.string() ?: return@withContext ScrapeResult.Error("Boş yanıt")
                val json = JSONObject(body)
                val chart = json.optJSONObject("chart")
                val resultList = chart?.optJSONArray("result")
                if (resultList != null && resultList.length() > 0) {
                    val data = resultList.getJSONObject(0)
                    val indicators = data.getJSONObject("indicators")
                    
                    val quote = indicators.optJSONArray("quote")?.optJSONObject(0)
                    val closeArray = quote?.optJSONArray("close")
                    val adjcloseArray = indicators.optJSONArray("adjclose")
                        ?.optJSONObject(0)
                        ?.optJSONArray("adjclose")
                    
                    val targetArray = closeArray ?: adjcloseArray ?: return@withContext ScrapeResult.Error("Kapanış verisi bulunamadı")
                    
                    val prices = mutableListOf<Double>()
                    var lastValidPrice = 0.0
                    for (i in 0 until targetArray.length()) {
                        if (!targetArray.isNull(i)) {
                            val p = targetArray.optDouble(i, Double.NaN)
                            if (!p.isNaN() && p > 0.0) {
                                prices.add(p)
                                lastValidPrice = p
                            } else if (lastValidPrice > 0.0) {
                                prices.add(lastValidPrice)
                            }
                        } else if (lastValidPrice > 0.0) {
                            prices.add(lastValidPrice)
                        }
                    }
                    if (prices.isEmpty()) return@withContext ScrapeResult.Error("Geçerli fiyat noktası bulunamadı")
                    ScrapeResult.Success(prices)
                } else {
                    ScrapeResult.Error("Yahoo History: Veri bulunamadı")
                }
            }
        } catch (e: Exception) {
            ScrapeResult.Error(e.localizedMessage ?: "Geçmiş verisi çekilemedi")
        }
    }

    suspend fun fetchCompanyInfo(symbol: String, market: String): ScrapeResult<CachedCompanyInfo> = withContext(Dispatchers.IO) {
        val yahooSymbol = getYahooSymbol(symbol, market)
        val url = "https://query1.finance.yahoo.com/v8/finance/chart/$yahooSymbol?interval=1d&range=1d"

        try {
            val request = Request.Builder()
                .url(url)
                .header("User-Agent", userAgent)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@withContext ScrapeResult.Error("Yahoo Public Chart API Hatası: ${response.code}")

                val body = response.body?.string() ?: return@withContext ScrapeResult.Error("Boş yanıt")
                val json = JSONObject(body)
                val chart = json.optJSONObject("chart")
                val resultList = chart?.optJSONArray("result")

                if (resultList != null && resultList.length() > 0) {
                    val data = resultList.getJSONObject(0)
                    val meta = data.getJSONObject("meta")
                    
                    val weekHigh = if (meta.has("fiftyTwoWeekHigh") && !meta.isNull("fiftyTwoWeekHigh")) {
                        val v = meta.optDouble("fiftyTwoWeekHigh")
                        if (v.isNaN() || v <= 0.0) null else v
                    } else null
                    
                    val weekLow = if (meta.has("fiftyTwoWeekLow") && !meta.isNull("fiftyTwoWeekLow")) {
                        val v = meta.optDouble("fiftyTwoWeekLow")
                        if (v.isNaN() || v <= 0.0) null else v
                    } else null
                    
                    val regularVolume = if (meta.has("regularMarketVolume") && !meta.isNull("regularMarketVolume")) meta.optLong("regularMarketVolume", 0L) else 0L
                    val vol = if (regularVolume > 0L) formatVolume(regularVolume) else null
                    
                    val longName = if (meta.has("longName") && !meta.isNull("longName")) meta.optString("longName") else ""
                    val shortName = if (meta.has("shortName") && !meta.isNull("shortName")) meta.optString("shortName") else ""
                    val displayName = if (longName.isNotBlank()) longName else if (shortName.isNotBlank()) shortName else ""

                    ScrapeResult.Success(
                        CachedCompanyInfo(
                            symbol = symbol,
                            about = displayName,
                            peRatio = null,
                            marketCap = null,
                            week52High = weekHigh,
                            week52Low = weekLow,
                            dividendYield = null,
                            nextDividendDate = null,
                            volume = vol
                        )
                    )
                } else {
                    ScrapeResult.Error("Yahoo Public Chart: Sembol bulunamadı: $yahooSymbol")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Yahoo Public Chart API Hatası ($symbol): ${e.localizedMessage}")
            ScrapeResult.Error(e.localizedMessage ?: "Yahoo Public Info Hatası")
        }
    }

    private fun formatMarketCap(value: Long, market: String): String {
        if (value <= 0L) return "N/A"
        val df = DecimalFormat("#.##")
        val suffix = when (market.uppercase()) {
            "BIST", "IST" -> " TL"
            "FRA", "EURONEXT" -> " EUR"
            else -> " USD"
        }
        
        return when {
            value >= 1_000_000_000_000L -> df.format(value / 1_000_000_000_000.0) + " Trilyon" + suffix
            value >= 1_000_000_000L -> df.format(value / 1_000_000_000.0) + " Milyar" + suffix
            value >= 1_000_000L -> df.format(value / 1_000_000.0) + " Milyon" + suffix
            else -> value.toString() + suffix
        }
    }

    private fun formatVolume(value: Long): String {
        if (value <= 0L) return "N/A"
        return when {
            value >= 1_000_000L -> DecimalFormat("#.#").format(value / 1_000_000.0) + "M"
            value >= 1_000L -> DecimalFormat("#.#").format(value / 1_000.0) + "K"
            else -> value.toString()
        }
    }
}
