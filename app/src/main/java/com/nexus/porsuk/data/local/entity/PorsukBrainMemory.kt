package com.nexus.porsuk.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Local Intelligence & Memory Entity for Porsuk Brain.
 * Stores user preferences, top interested stocks, frequented sectors, past AI answers,
 * accuracy rates (Technical %81, News %74, Fundamental %69), and recent indicator snapshots.
 */
@Entity(tableName = "porsuk_brain_memory")
data class PorsukBrainMemory(
    @PrimaryKey val id: Int = 1,
    val userRiskTolerance: String = "MODERATE", // CONSERVATIVE, MODERATE, AGGRESSIVE
    val favoriteInvestmentStyle: String = "Değer Yatırımı",
    val topInterestedSectors: String = "Savunma, Teknoloji, Sanayi",
    val topInterestedSymbols: String = "ASELSAN, THYAO, EREGL",
    val lastTechnicalSummary: String = "RSI pozitif, MACD alım bölgesinde.",
    val lastNewsSummary: String = "Sektörel beklentiler olumlu.",
    val accuracyBreakdownSummary: String = "Teknik: %81, Haber: %74, Bilanço: %69",
    val lastUpdated: Long = System.currentTimeMillis()
)
