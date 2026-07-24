package com.nexus.porsuk.domain.model

/**
 * Porsuk Finans — Desteklenen Borsa ve Enstrüman Tipleri (Extensible Enum)
 *
 * BIST, NASDAQ ve NYSE borsalarının yanı sıra gelecekte LSE, EURONEXT borsaları
 * ve ETF desteğinin sorunsuz eklenebilmesi için esnek şekilde tasarlanmıştır.
 */
enum class ExchangeType(
    val code: String,
    val displayName: String,
    val defaultCurrency: String,
    val defaultCountry: String
) {
    BIST("IS", "Borsa İstanbul", "TRY", "TR"),
    NASDAQ("US", "NASDAQ", "USD", "US"),
    NYSE("US", "New York Stock Exchange", "USD", "US"),
    
    // Gelecekte Eklenecek Borsalar & ETF Desteği
    LSE("LSE", "London Stock Exchange", "GBP", "UK"),
    EURONEXT("PA", "Euronext Paris", "EUR", "FR"),
    XETRA("DE", "Deutsche Börse Xetra", "EUR", "DE"),
    ETF("ETF", "Exchange Traded Funds", "USD", "GLOBAL");

    companion object {
        fun fromCode(code: String): ExchangeType {
            return entries.firstOrNull { it.code.equals(code, ignoreCase = true) } ?: BIST
        }
    }
}
