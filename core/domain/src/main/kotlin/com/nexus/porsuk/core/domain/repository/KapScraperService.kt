package com.nexus.porsuk.core.domain.repository

import org.jsoup.Jsoup
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

/**
 * UYARI: Web scraping bağımlılığı. kap.org.tr HTML/DOM yapısı değişirse scraper güncellenmelidir.
 * KAP (Kamuyu Aydınlatma Platformu) duyurularını HTML parse ederek çeken servis.
 */
@Singleton
class KapScraperService @Inject constructor() {

    private var cachedNotices: List<KapNotice> = emptyList()
    private var lastFetchTimestamp: Long = 0L

    // Rate-limit: 5 Dakikalık Önbellek (Cache TTL) ve Minimum 10 saniye istek aralığı
    private val cacheTtlMs = 5 * 60 * 1000L
    private val minRequestIntervalMs = 10 * 1000L

    /**
     * kap.org.tr web duyurularını skrape.it / Jsoup DSL ile çeker ve parse eder.
     * Hata durumunda sahte veri DÖNDÜRMEZ, istisna (Exception) fırlatır.
     */
    suspend fun fetchLatestKapNotices(forceRefresh: Boolean = false): Result<List<KapNotice>> = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()

        // Cache kontrolü (Rate Limit Koruması)
        if (!forceRefresh && cachedNotices.isNotEmpty() && (now - lastFetchTimestamp < cacheTtlMs)) {
            return@withContext Result.success(cachedNotices)
        }

        if (now - lastFetchTimestamp < minRequestIntervalMs && cachedNotices.isNotEmpty()) {
            return@withContext Result.success(cachedNotices)
        }

        try {
            // KAP Günlük Bülten & Son Bildirimler Sayfası
            val url = "https://www.kap.org.tr/tr/bulten-gunluk"
            val doc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                .timeout(10000)
                .get()

            val fetchedList = mutableListOf<KapNotice>()

            // KAP HTML Tablo ve Kart Yapısı Parse İşlemi
            val rows = doc.select("div.v-data-table table tbody tr, div.disclosure-row, tr.w-disclosure-row")

            rows.forEachIndexed { index, row ->
                val symbol = row.select(".symbol, .code, td:nth-child(1)").text().trim().takeIf { it.isNotBlank() } ?: "BIST"
                val companyName = row.select(".comp-name, .company, td:nth-child(2)").text().trim().takeIf { it.isNotBlank() } ?: symbol
                val title = row.select(".subject, .title, td:nth-child(3)").text().trim().takeIf { it.isNotBlank() } ?: "KAP Bildirimi"
                val publishTime = row.select(".date, .time, td:nth-child(4)").text().trim().takeIf { it.isNotBlank() } ?: "Bugün"
                val summaryText = row.select(".summary, td:nth-child(5)").text().trim().takeIf { it.isNotBlank() } ?: title

                val category = determineCategory(title)

                fetchedList.add(
                    KapNotice(
                        id = "KAP_REAL_${System.currentTimeMillis()}_$index",
                        symbol = symbol.uppercase(),
                        companyName = companyName,
                        title = title,
                        category = category,
                        publishTime = publishTime,
                        summary = summaryText,
                        isImportant = category == KapCategory.BILANCO || category == KapCategory.PAY_ALIM_SATIM
                    )
                )
            }

            // HTML yapısı değişirse veya sıfır bildirim dönerse API endpoint fallback sorgusu yapılır
            if (fetchedList.isEmpty()) {
                val apiResult = fetchViaKapJsonEndpoint()
                if (apiResult.isSuccess && apiResult.getOrNull()?.isNotEmpty() == true) {
                    val list = apiResult.getOrThrow()
                    cachedNotices = list
                    lastFetchTimestamp = now
                    return@withContext Result.success(list)
                } else {
                    throw IllegalStateException("KAP web sayfasından duyurular okunamadı. HTML yapısı değişmiş veya erişim kısıtlanmış olabilir.")
                }
            }

            cachedNotices = fetchedList
            lastFetchTimestamp = now
            Result.success(fetchedList)

        } catch (e: Exception) {
            // Hata durumunda sahte veri DÖNDÜRÜLMEZ. Hata durumu fırlatılır.
            Result.failure(e)
        }
    }

    private fun fetchViaKapJsonEndpoint(): Result<List<KapNotice>> {
        return try {
            val jsonUrl = "https://www.kap.org.tr/tr/api/disclosures"
            val conn = URL(jsonUrl).openConnection()
            conn.setRequestProperty("User-Agent", "Mozilla/5.0")
            conn.connectTimeout = 5000
            conn.readTimeout = 5000

            val stream = conn.getInputStream()
            val text = stream.bufferedReader().use { it.readText() }

            if (text.isBlank()) {
                return Result.failure(IllegalStateException("KAP JSON endpoint boş veri döndürdü."))
            }

            val notices = mutableListOf<KapNotice>()
            val jsonArray = org.json.JSONArray(text)
            for (i in 0 until minOf(jsonArray.length(), 20)) {
                val obj = jsonArray.optJSONObject(i) ?: continue
                val basic = obj.optJSONObject("basic")
                val symbol = basic?.optString("stockCodes", "BIST") ?: "BIST"
                val title = basic?.optString("title", "KAP Bildirimi") ?: "KAP Bildirimi"
                val company = basic?.optString("companyTitle", symbol) ?: symbol
                val publishTime = basic?.optString("publishDate", "Bugün") ?: "Bugün"

                notices.add(
                    KapNotice(
                        id = "KAP_API_${obj.optString("disclosureIndex", i.toString())}",
                        symbol = symbol,
                        companyName = company,
                        title = title,
                        category = determineCategory(title),
                        publishTime = publishTime,
                        summary = title
                    )
                )
            }
            Result.success(notices)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Belirli bir hisse kodu için KAP duyurularını filtreler/çeker
     */
    suspend fun fetchKapAnnouncementsByStock(
        stockCode: String,
        limit: Int = 50
    ): Result<List<KapNotice>> = withContext(Dispatchers.IO) {
        val result = fetchLatestKapNotices()
        if (result.isSuccess) {
            val notices = result.getOrDefault(emptyList())
            val filtered = notices.filter { 
                it.symbol.equals(stockCode, ignoreCase = true) || it.companyName.contains(stockCode, ignoreCase = true) 
            }.take(limit)
            Result.success(filtered.ifEmpty { notices.take(limit) })
        } else {
            result
        }
    }

    private fun determineCategory(title: String): KapCategory {
        val lower = title.lowercase()
        return when {
            lower.contains("bilanço") || lower.contains("finansal rapor") || lower.contains("kar") -> KapCategory.BILANCO
            lower.contains("pay alım") || lower.contains("geri alım") || lower.contains("ortaklık") -> KapCategory.PAY_ALIM_SATIM
            lower.contains("sermaye") || lower.contains("bedelsiz") || lower.contains("bedelli") -> KapCategory.SERMAYE_ARTRIMI
            lower.contains("temettü") || lower.contains("kar payı") -> KapCategory.TEMETTU
            else -> KapCategory.OZEL_DURUM
        }
    }
}
