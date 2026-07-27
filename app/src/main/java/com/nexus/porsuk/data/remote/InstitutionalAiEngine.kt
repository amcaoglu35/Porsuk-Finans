package com.nexus.porsuk.data.remote

import com.nexus.porsuk.data.local.SettingsManager
import com.nexus.porsuk.domain.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InstitutionalAiEngine @Inject constructor(
    private val settingsManager: SettingsManager
) {
    suspend fun generateInstitutionalInsights(): List<InstitutionalAiInsight> = withContext(Dispatchers.IO) {
        val apiKey = settingsManager.getGeminiApiKey() ?: return@withContext emptyList()
        val service = GeminiService(apiKey)

        val prompt = """
            Sen bir Bloomberg Terminal baş analistisin. 
            Mevcut piyasa verilerini, sektör performanslarını ve kurumsal para akışlarını analiz ederek 
            profesyonel yatırımcılar için 3 adet kritik 'Institutional Insight' üret.
            
            Format:
            1. Risk Tespiti (Görünmez riskler)
            2. Fırsat Analizi (Sektörel veya şirket bazlı)
            3. Portföy Optimizasyonu (Makro senaryoya göre)
            
            Yanıt dilini Türkçe yap ve kurumsal bir üslup kullan.
        """.trimIndent()

        val response = service.chat(prompt)
        
        // Mock parsing/generation for now
        listOf(
            InstitutionalAiInsight(
                title = "Sektörel Rotasyon Uyarısı",
                type = InsightType.SECTOR_ROTATION,
                description = response.take(150) + "...",
                impactedSectors = listOf("Bankacılık", "Sanayi"),
                opportunityScore = 78,
                riskLevel = "ORTA",
                actionSuggestion = "Finansal hisselerde kar satışı, enerji hisselerinde biriktirme önerilir."
            ),
            InstitutionalAiInsight(
                title = "Likidite ve Volatilite Riski",
                type = InsightType.RISK_DETECTION,
                description = "Küresel merkez bankası kararları sonrası piyasada likidite daralması ve oynaklık artışı bekleniyor.",
                impactedSectors = listOf("Teknoloji", "Havacılık"),
                opportunityScore = 45,
                riskLevel = "YÜKSEK",
                actionSuggestion = "Nakit oranını %25 seviyesine çekerek defansif pozisyona geçilmelidir."
            )
        )
    }
}
