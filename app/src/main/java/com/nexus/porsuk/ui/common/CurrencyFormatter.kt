package com.nexus.porsuk.ui.common

import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Locale

object CurrencyFormatter {

    /**
     * Ham decimal sayıları borsa standardında formatlar.
     */
    fun formatWithSymbol(value: Double, symbol: String, formatType: String = "TR"): String {
        val formatted = NumberFormatter.format(value, formatType)
        return "$symbol$formatted"
    }

    /**
     * TL para birimini formatlar.
     */
    fun formatTRY(value: Double, formatType: String = "TR"): String {
        return formatWithSymbol(value, "₺", formatType)
    }

    fun getCurrencySymbol(market: String): String {
        return when (market.uppercase()) {
            "NASDAQ", "NYSE" -> "$"
            "FRA", "EURONEXT" -> "€"
            else -> "₺"
        }
    }
}
