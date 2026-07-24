package com.nexus.porsuk.domain.model

/**
 * Porsuk Portfolio Engine — Portföy Tipleri (Extensible Enum)
 *
 * Gerçek portföyün yanı sıra gelecekte Sanal Portföy (Paper Trading) ve
 * Ortak/Aile Portföyü modellerinin sorunsuz desteklenebilmesi için tasarlanmıştır.
 */
enum class PortfolioType(val displayName: String) {
    REAL("Gerçek Portföy"),
    VIRTUAL_PAPER_TRADING("Sanal / Paper Trading Portföyü"),
    FAMILY_SHARED("Aile / Ortak Portföy")
}

/**
 * Porsuk Portfolio Engine — Desteklenen İşlem Tipleri (9 İşlem Tipi)
 */
enum class TransactionType(val displayName: String, val isCashInflow: Boolean) {
    BUY("Hisse/Varlık Alımı", false),
    SELL("Hisse/Varlık Satışı", true),
    DIVIDEND("Temettü Geliri", true),
    RIGHTS_ISSUE_PAID("Bedelli Sermaye Artırımı", false),
    BONUS_ISSUE_FREE("Bedelsiz Sermaye Artırımı", false),
    COMMISSION("Borsa Komisyonu", false),
    TAX("Stopaj / Vergi", false),
    CASH_DEPOSIT("Nakit Yatırma", true),
    CASH_WITHDRAWAL("Nakit Çekme", false)
}
