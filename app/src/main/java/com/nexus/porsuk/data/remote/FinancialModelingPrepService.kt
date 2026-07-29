package com.nexus.porsuk.data.remote

import android.util.Log
import com.nexus.porsuk.data.local.entity.PriceSnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import com.google.gson.Gson
import com.nexus.porsuk.data.remote.dto.*

class FinancialModelingPrepService(private val apiKey: String) {
    private val client = OkHttpClient()
    private val gson = Gson()
    private val TAG = "FMP_Service"

    private suspend fun <T> fetchList(url: String, clazz: Class<Array<T>>): ScrapeResult<List<T>> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder().url(url).build()
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@withContext ScrapeResult.Error("API Error: ${response.code}")
                val body = response.body?.string() ?: return@withContext ScrapeResult.Error("Empty Body")
                val array = gson.fromJson(body, clazz)
                ScrapeResult.Success(array.toList())
            }
        } catch (e: Exception) {
            ScrapeResult.Error(e.localizedMessage ?: "Unknown Error")
        }
    }

    suspend fun fetchIncomeStatement(symbol: String, limit: Int = 4): ScrapeResult<List<IncomeStatementDto>> {
        val url = "https://financialmodelingprep.com/api/v3/income-statement/$symbol?limit=$limit&apikey=$apiKey"
        return fetchList(url, Array<IncomeStatementDto>::class.java)
    }

    suspend fun fetchBalanceSheet(symbol: String, limit: Int = 4): ScrapeResult<List<BalanceSheetDto>> {
        val url = "https://financialmodelingprep.com/api/v3/balance-sheet-statement/$symbol?limit=$limit&apikey=$apiKey"
        return fetchList(url, Array<BalanceSheetDto>::class.java)
    }

    suspend fun fetchCashFlow(symbol: String, limit: Int = 4): ScrapeResult<List<CashFlowDto>> {
        val url = "https://financialmodelingprep.com/api/v3/cash-flow-statement/$symbol?limit=$limit&apikey=$apiKey"
        return fetchList(url, Array<CashFlowDto>::class.java)
    }

    suspend fun fetchRatios(symbol: String, limit: Int = 4): ScrapeResult<List<CompanyRatioDto>> {
        val url = "https://financialmodelingprep.com/api/v3/ratios/$symbol?limit=$limit&apikey=$apiKey"
        return fetchList(url, Array<CompanyRatioDto>::class.java)
    }

    suspend fun fetchKeyMetrics(symbol: String, limit: Int = 4): ScrapeResult<List<KeyMetricsDto>> {
        val url = "https://financialmodelingprep.com/api/v3/key-metrics/$symbol?limit=$limit&apikey=$apiKey"
        return fetchList(url, Array<KeyMetricsDto>::class.java)
    }

    suspend fun fetchFullProfile(symbol: String): ScrapeResult<CompanyProfileDto> {
        val url = "https://financialmodelingprep.com/api/v3/profile/$symbol?apikey=$apiKey"
        val res = fetchList(url, Array<CompanyProfileDto>::class.java)
        return when (res) {
            is ScrapeResult.Success -> {
                if (res.data.isNotEmpty()) ScrapeResult.Success(res.data.first())
                else ScrapeResult.Error("No profile found")
            }
            is ScrapeResult.Error -> ScrapeResult.Error(res.message)
        }
    }

    suspend fun fetchPrice(symbol: String, market: String): ScrapeResult<PriceSnapshot> = withContext(Dispatchers.IO) {
        val fmpSymbol = when (market.uppercase()) {
            "BIST", "IST" -> {
                if (symbol.contains(".")) symbol else "$symbol.IS"
            }
            else -> symbol
        }

        val url = "https://financialmodelingprep.com/api/v3/quote/$fmpSymbol?apikey=$apiKey"
        
        try {
            val request = Request.Builder()
                .url(url)
                .build()
            
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext ScrapeResult.Error("API hatası: ${response.code}")
                }
                
                val body = response.body?.string() ?: return@withContext ScrapeResult.Error("Boş yanıt")
                val jsonArray = JSONArray(body)
                
                if (jsonArray.length() > 0) {
                    val obj = jsonArray.getJSONObject(0)
                    val price = obj.optDouble("price", 0.0)
                    val changesPercentage = obj.optDouble("changesPercentage", 0.0)
                    
                    if (price > 0) {
                        ScrapeResult.Success(
                            PriceSnapshot(
                                symbol = symbol,
                                price = price,
                                changePercent = changesPercentage,
                                interval = "DAY"
                            )
                        )
                    } else {
                        ScrapeResult.Error("Sembol verisi geçersiz: $symbol")
                    }
                } else {
                    ScrapeResult.Error("Sembol bulunamadı: $symbol")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "FMP hatası ($symbol): ${e.localizedMessage}")
            ScrapeResult.Error(e.localizedMessage ?: "Bilinmeyen hata")
        }
    }
    
    suspend fun fetchPrices(symbols: List<String>): ScrapeResult<Map<String, PriceSnapshot>> = withContext(Dispatchers.IO) {
        if (symbols.isEmpty()) return@withContext ScrapeResult.Success(emptyMap())
        
        val symbolList = symbols.joinToString(",")
        val url = "https://financialmodelingprep.com/api/v3/quote/$symbolList?apikey=$apiKey"
        
        try {
            val request = Request.Builder().url(url).build()
            client.newCall(request).execute().use { response ->
                val body = response.body?.string() ?: return@withContext ScrapeResult.Error("Boş yanıt")
                val jsonArray = JSONArray(body)
                val results = mutableMapOf<String, PriceSnapshot>()
                
                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    val sym = obj.getString("symbol")
                    val price = obj.getDouble("price")
                    val change = obj.getDouble("changesPercentage")
                    
                    val cleanSym = sym.replace(".IS", "")
                    results[cleanSym] = PriceSnapshot(
                        symbol = cleanSym,
                        price = price,
                        changePercent = change,
                        interval = "DAY"
                    )
                }
                ScrapeResult.Success(results)
            }
        } catch (e: Exception) {
            ScrapeResult.Error(e.localizedMessage ?: "Bilinmeyen hata")
        }
    }

    suspend fun fetchCompanyProfiles(symbols: List<String>): ScrapeResult<List<com.nexus.porsuk.data.local.entity.CachedCompanyInfo>> = withContext(Dispatchers.IO) {
        if (symbols.isEmpty()) return@withContext ScrapeResult.Success(emptyList())
        
        val symbolList = symbols.joinToString(",")
        val url = "https://financialmodelingprep.com/api/v3/profile/$symbolList?apikey=$apiKey"
        
        try {
            val request = Request.Builder().url(url).build()
            client.newCall(request).execute().use { response ->
                val body = response.body?.string() ?: return@withContext ScrapeResult.Error("Boş yanıt")
                val jsonArray = JSONArray(body)
                val results = mutableListOf<com.nexus.porsuk.data.local.entity.CachedCompanyInfo>()
                
                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    val sym = obj.getString("symbol").replace(".IS", "")
                    
                    results.add(
                        com.nexus.porsuk.data.local.entity.CachedCompanyInfo(
                            symbol = sym,
                            about = obj.optString("description", ""),
                            peRatio = null, // Profile endpoint doesn't have it
                            marketCap = obj.optDouble("mktCap", 0.0).toString(),
                            week52High = null,
                            week52Low = null,
                            dividendYield = obj.optDouble("lastDiv", 0.0),
                            volume = obj.optLong("volAvg", 0).toString(),
                            lastUpdated = System.currentTimeMillis()
                        )
                    )
                }
                ScrapeResult.Success(results)
            }
        } catch (e: Exception) {
            ScrapeResult.Error(e.localizedMessage ?: "Bilinmeyen hata")
        }
    }
}
