package com.nexus.porsuk.data.sync

/**
 * Porsuk Finnhub Engine — Senkronizasyon Detay Raporu (Sync Audit Report)
 *
 * Her senkronizasyon çalıştığında kaç şirket indirildi, kaçı güncellendi,
 * kaç yeni şirket eklendi, kaçı pasife çekildi ve toplam süre verilerini toplar.
 */
data class SyncReport(
    val totalDownloaded: Int = 0,
    val totalAdded: Int = 0,
    val totalUpdated: Int = 0,
    val totalDelisted: Int = 0,
    val durationMs: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val isSuccess: Boolean = true,
    val errorMessage: String? = null
) {
    fun toFormattedLog(): String {
        return """
            =================== FINNHUB SYNC RAPORU ===================
            Tarih          : ${java.util.Date(timestamp)}
            Durum          : ${if (isSuccess) "BAŞARILI" else "BAŞARISIZ ($errorMessage)"}
            Toplam İndirilen: $totalDownloaded
            Yeni Eklendi   : $totalAdded
            Güncellendi    : $totalUpdated
            Pasife Alındı  : $totalDelisted
            İşlem Süresi   : ${durationMs}ms
            ===========================================================
        """.trimIndent()
    }
}
