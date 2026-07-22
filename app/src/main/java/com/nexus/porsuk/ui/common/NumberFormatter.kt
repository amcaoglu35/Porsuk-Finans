package com.nexus.porsuk.ui.common

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

object NumberFormatter {
    private val trLocale = Locale("tr", "TR")
    private val usLocale = Locale.US

    /**
     * Kullanıcın ayarına göre (TR veya US) sayıları formatlar.
     * TR -> 1.234,56
     * US -> 1,234.56
     */
    fun format(value: Double, formatType: String = "TR"): String {
        val locale = if (formatType == "TR") trLocale else usLocale
        return try {
            val rounded = BigDecimal(value).setScale(2, RoundingMode.HALF_UP).toDouble()
            String.format(locale, "%,.2f", rounded)
        } catch (e: Exception) {
            String.format(locale, "%,.2f", value)
        }
    }

    /**
     * Yüzde değişimleri için formatlama (+%2,45 veya -%1,20)
     */
    fun formatPercentage(value: Double, formatType: String = "TR"): String {
        val formatted = format(value, formatType)
        return if (value >= 0) "+%$formatted" else "%$formatted"
    }
}
