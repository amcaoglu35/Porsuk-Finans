package com.nexus.porsuk.data.sync

import com.nexus.porsuk.domain.model.AssetCategory
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Porsuk Market Engine — Piyasa Çalışma Saatleri ve Açık/Kapalı Durum Yöneticisi
 *
 * Piyasaların açık veya kapalı olma durumunu kontrol eder.
 * Piyasa kapalıyken gereksiz API isteklerini engelleyerek batarya ve veri kotası tasarrufu sağlar.
 */
@Singleton
class MarketHoursManager @Inject constructor() {

    /**
     * Belirtilen varlık kategorisinin an itibarıyla işlem görüp görmediğini kontrol eder.
     */
    fun isMarketOpen(category: AssetCategory): Boolean {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val hour = calendar.get(Calendar.HOUR_OF_DAY)

        // Hafta sonu (Cumartesi ve Pazar)
        val isWeekend = dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY

        return when (category) {
            AssetCategory.CRYPTO -> true // Kripto piyasaları 7/24 açıktır
            AssetCategory.BIST_STOCK -> !isWeekend && (hour in 7..15) // BIST (UTC 07:00 - 15:00)
            AssetCategory.NASDAQ_STOCK, AssetCategory.NYSE_STOCK -> !isWeekend && (hour in 13..21) // US (UTC 13:30 - 20:00)
            AssetCategory.CURRENCY, AssetCategory.COMMODITY -> !isWeekend // Forex / Emtia hafta içi 24 saat
            else -> !isWeekend
        }
    }
}
