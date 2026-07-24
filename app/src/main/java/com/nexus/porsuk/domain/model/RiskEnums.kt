package com.nexus.porsuk.domain.model

/**
 * Porsuk Risk Intelligence Engine — 6 Dereceli Risk Seviyesi (Risk Levels)
 */
enum class RiskLevel(val displayName: String, val colorHex: Long) {
    VERY_LOW("Çok Düşük Risk 🟢", 0xFF00C853),
    LOW("Düşük Risk 🍏", 0xFF2E7D32),
    MODERATE("Orta Risk 🟡", 0xFFFFB300),
    HIGH("Yüksek Risk 🟠", 0xFFFF6D00),
    VERY_HIGH("Çok Yüksek Risk 🔴", 0xFFD50000),
    EXTREME("Aşırı / Kritik Risk ⚠️", 0xFFDD2C00);
}

/**
 * 6 Risk Kategorisi (Risk Categories)
 */
enum class RiskCategory(val displayName: String) {
    MARKET("Piyasa Riski (Market)"),
    LIQUIDITY("Likidite Riski (Liquidity)"),
    FINANCIAL("Finansal Risk (Financial)"),
    BUSINESS("İş & Sektör Riski (Business)"),
    PRICE("Fiyat & Volatilite Riski (Price)"),
    PORTFOLIO("Portföy Riski (Portfolio)");
}
