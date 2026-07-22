package com.nexus.porsuk.data.remote

import android.util.Log
import com.nexus.porsuk.data.local.entity.PriceSnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

class FinnhubService(private val apiKey: String) {
    private val client = OkHttpClient()
    private val TAG = "FinnhubService"

    suspend fun fetchPrice(symbol: String, market: String): ScrapeResult<PriceSnapshot> = withContext(Dispatchers.IO) {
        // Finnhub expects symbols like THYAO.IS for BIST
        val finnhubSymbol = when (market.uppercase()) {
            "BIST", "IST" -> {
                if (symbol.contains(".")) symbol else "$symbol.IS"
            }
            "CURRENCY" -> {
                // Finnhub currency symbols can be different, e.g., OANDA:USD_TRY
                // For now, let's keep it simple or fallback
                symbol
            }
            else -> symbol
        }

        val url = "https://finnhub.io/api/v1/quote?symbol=$finnhubSymbol&token=$apiKey"
        
        try {
            val request = Request.Builder()
                .url(url)
                .build()
            
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext ScrapeResult.Error("API hatası: ${response.code}")
                }
                
                val body = response.body?.string() ?: return@withContext ScrapeResult.Error("Boş yanıt")
                val json = JSONObject(body)
                
                // c: Current price, dp: Percent change
                val currentPrice = json.optDouble("c", 0.0)
                val changePercent = json.optDouble("dp", 0.0)

                if (currentPrice > 0) {
                    ScrapeResult.Success(
                        PriceSnapshot(
                            symbol = symbol,
                            price = currentPrice,
                            changePercent = changePercent,
                            interval = "DAY"
                        )
                    )
                } else {
                    ScrapeResult.Error("Sembol bulunamadı veya veri yok: $symbol")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Finnhub hatası ($symbol): ${e.localizedMessage}")
            ScrapeResult.Error(e.localizedMessage ?: "Bilinmeyen hata")
        }
    }
}
