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

class YahooFinanceService(private val apiKey: String) {
    private val client = OkHttpClient()
    private val TAG = "YahooFinanceService"
    private val host = "apidojo-yahoo-finance-v1.p.rapidapi.com"

    suspend fun fetchPrice(symbol: String, market: String): ScrapeResult<PriceSnapshot> = withContext(Dispatchers.IO) {
        val yahooSymbol = getYahooSymbol(symbol, market)
        val url = "https://$host/market/v2/get-quotes?region=TR&symbols=$yahooSymbol"
        
        try {
            val request = Request.Builder()
                .url(url)
                .addHeader("x-rapidapi-key", apiKey)
                .addHeader("x-rapidapi-host", host)
                .build()
            
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@withContext ScrapeResult.Error("Yahoo API hatası: ${response.code}")
                
                val body = response.body?.string() ?: return@withContext ScrapeResult.Error("Boş yanıt")
                val json = JSONObject(body)
                val quoteResponse = json.optJSONObject("quoteResponse")
                val result = quoteResponse?.optJSONArray("result")
                
                if (result != null && result.length() > 0) {
                    val stockData = result.getJSONObject(0)
                    val currentPrice = stockData.optDouble("regularMarketPrice", 0.0)
                    val changePercent = stockData.optDouble("regularMarketChangePercent", 0.0)

                    ScrapeResult.Success(
                        PriceSnapshot(symbol = symbol, price = currentPrice, changePercent = changePercent, interval = "DAY")
                    )
                } else {
                    ScrapeResult.Error("Yahoo: Sembol bulunamadı: $yahooSymbol")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Yahoo Finance hatası ($symbol): ${e.localizedMessage}")
            ScrapeResult.Error(e.localizedMessage ?: "Bilinmeyen hata")
        }
    }

    suspend fun fetchCompanyInfo(symbol: String, market: String): ScrapeResult<CachedCompanyInfo> = withContext(Dispatchers.IO) {
        val yahooSymbol = getYahooSymbol(symbol, market)
        val url = "https://$host/market/v2/get-quotes?region=TR&symbols=$yahooSymbol"

        try {
            val request = Request.Builder()
                .url(url)
                .addHeader("x-rapidapi-key", apiKey)
                .addHeader("x-rapidapi-host", host)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@withContext ScrapeResult.Error("Yahoo API hatası")

                val body = response.body?.string() ?: return@withContext ScrapeResult.Error("Boş yanıt")
                val json = JSONObject(body)
                val quoteResponse = json.optJSONObject("quoteResponse")
                val resultList = quoteResponse?.optJSONArray("result")

                if (resultList != null && resultList.length() > 0) {
                    val data = resultList.getJSONObject(0)
                    
                    val pe = if (data.has("trailingPE") && !data.isNull("trailingPE")) {
                        val v = data.optDouble("trailingPE")
                        if (v.isNaN() || v <= 0.0) null else v
                    } else null
                    
                    val weekHigh = if (data.has("fiftyTwoWeekHigh") && !data.isNull("fiftyTwoWeekHigh")) {
                        val v = data.optDouble("fiftyTwoWeekHigh")
                        if (v.isNaN() || v <= 0.0) null else v
                    } else null
                    
                    val weekLow = if (data.has("fiftyTwoWeekLow") && !data.isNull("fiftyTwoWeekLow")) {
                        val v = data.optDouble("fiftyTwoWeekLow")
                        if (v.isNaN() || v <= 0.0) null else v
                    } else null
                    
                    val divYield = if (data.has("dividendYield") && !data.isNull("dividendYield")) {
                        val v = data.optDouble("dividendYield")
                        if (v.isNaN() || v < 0.0) null else v * 100.0
                    } else null
                    
                    val marketCapRaw = if (data.has("marketCap") && !data.isNull("marketCap")) data.optLong("marketCap", 0L) else 0L
                    val marketCapFormatted = if (marketCapRaw > 0L) formatMarketCap(marketCapRaw, market) else null
                    
                    val regularVolume = if (data.has("regularMarketVolume") && !data.isNull("regularMarketVolume")) data.optLong("regularMarketVolume", 0L) else 0L
                    val vol = if (regularVolume > 0L) formatVolume(regularVolume) else null
                    
                    val longName = if (data.has("longName") && !data.isNull("longName")) data.optString("longName") else ""

                    ScrapeResult.Success(
                        CachedCompanyInfo(
                            symbol = symbol,
                            about = longName,
                            peRatio = pe,
                            marketCap = marketCapFormatted,
                            week52High = weekHigh,
                            week52Low = weekLow,
                            dividendYield = divYield,
                            volume = vol
                        )
                    )
                } else {
                    ScrapeResult.Error("Yahoo Veri Bulunamadı")
                }
            }
        } catch (e: Exception) {
            ScrapeResult.Error(e.message ?: "Yahoo Info Error")
        }
    }

    private fun getYahooSymbol(symbol: String, market: String): String {
        return when (market.uppercase()) {
            "BIST", "IST" -> if (symbol.contains(".")) symbol else "$symbol.IS"
            "FRA", "EURONEXT" -> if (symbol.contains(".")) symbol else "$symbol.DE"
            "CURRENCY" -> if (symbol.contains("=")) symbol else "${symbol}=X"
            else -> symbol
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
