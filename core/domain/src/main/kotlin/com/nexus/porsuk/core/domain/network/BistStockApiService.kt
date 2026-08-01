package com.nexus.porsuk.core.domain.network

import com.nexus.porsuk.core.domain.entity.CompanyStock
import com.nexus.porsuk.core.domain.entity.TechnicalSignal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BistStockApiService @Inject constructor() {

    private val client = OkHttpClient.Builder()
        .connectTimeout(8, TimeUnit.SECONDS)
        .readTimeout(8, TimeUnit.SECONDS)
        .build()

    suspend fun fetchLiveBistQuotes(targetSymbols: List<String> = emptyList()): Result<Map<String, Double>> = withContext(Dispatchers.IO) {
        val quotes = mutableMapOf<String, Double>()
        val defaultSymbols = listOf(
            "THYAO", "GARAN", "ASELS", "EREGL", "KCHOL", "AKBNK",
            "SISE", "TUPRS", "SAHOL", "BIMAS", "YKBNK", "TCELL",
            "FROTO", "TOASO", "KOZAL", "MGROS", "ENKAI", "PETKM",
            "ASTOR", "KONTR", "SASA", "HEKTAS", "PGSUS", "EKGYO",
            "ALARK", "ISCTR"
        )
        val symbolsToFetch = (if (targetSymbols.isNotEmpty()) targetSymbols else defaultSymbols).map {
            if (it.endsWith(".IS")) it else "$it.IS"
        }
        val url = "https://query1.finance.yahoo.com/v7/finance/quote?symbols=${symbolsToFetch.joinToString(",")}"

        try {
            val request = Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val bodyString = response.body?.string() ?: ""
                    val json = JSONObject(bodyString)
                    val result = json.getJSONObject("quoteResponse").getJSONArray("result")

                    for (i in 0 until result.length()) {
                        val item = result.getJSONObject(i)
                        val fullSymbol = item.optString("symbol", "")
                        val price = item.optDouble("regularMarketPrice", 0.0)

                        val cleanSymbol = fullSymbol.replace(".IS", "")
                        if (cleanSymbol.isNotBlank() && price > 0) {
                            quotes[cleanSymbol] = price
                        }
                    }
                    Result.success(quotes)
                } else {
                    Result.failure(Exception("Ağ isteği başarısız oldu: HTTP ${response.code}"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
