package com.nexus.porsuk.domain.model

/**
 * 5 Bildirim Öncelik Seviyesi (Notification Priority Levels)
 */
enum class NotificationPriority(val displayName: String, val colorHex: Long) {
    CRITICAL("Kritik 🔴", 0xFFD50000),
    HIGH("Yüksek 🟠", 0xFFFF6D00),
    MEDIUM("Orta 🟡", 0xFFFFB300),
    LOW("Düşük 🟢", 0xFF00C853),
    SILENT("Sessiz ⚪", 0xFF757575);
}
