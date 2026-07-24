package com.nexus.porsuk.data.sync

/**
 * Porsuk TEFAS Engine — Senkronizasyon Raporu
 *
 * Kaç fon indirildi, kaç yeni fon eklendi, kaçı güncellendi, kaçı pasife çekildi
 * ve işlem süresini detaylı raporlar.
 */
data class TefasSyncReport(
    val totalDownloaded: Int = 0,
    val totalAdded: Int = 0,
    val totalUpdated: Int = 0,
    val totalInactivated: Int = 0,
    val durationMs: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val isSuccess: Boolean = true,
    val errorMessage: String? = null
) {
    fun toFormattedLog(): String {
        return """
            ==================== TEFAS SYNC RAPORU ====================
            Tarih            : ${java.util.Date(timestamp)}
            Durum            : ${if (isSuccess) "BAŞARILI" else "BAŞARISIZ ($errorMessage)"}
            İndirilen Fon    : $totalDownloaded
            Yeni Eklendi     : $totalAdded
            Güncellendi      : $totalUpdated
            Pasife Çekildi   : $totalInactivated
            İşlem Süresi     : ${durationMs}ms
            ===========================================================
        """.trimIndent()
    }
}
