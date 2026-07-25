package com.nexus.porsuk.data.ma

import com.nexus.porsuk.domain.model.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToInt

@Singleton
class CorporateEventEngine @Inject constructor() {

    fun calculatePremiumPaid(offerPrice: Double, marketPrice: Double): Double {
        if (marketPrice <= 0.0) return 25.0
        val prem = ((offerPrice - marketPrice) / marketPrice) * 100.0
        return (prem * 10.0).roundToInt() / 10.0
    }

    fun computeDealImpact(dealId: String): DealImpactAnalysis {
        return DealImpactAnalysis(
            dealId = dealId,
            revenueImpactPct = 28.5,
            marketCapImpactPct = 18.2,
            industryImpactSummary = "İşlem sonrasında birleşen yapı Türkiye ve Doğu Avrupa havacılık ve lojistik pazarının %38'ini kontrol edecektir.",
            competitiveImpactSummary = "Ölçek ekonomileri sayesinde birim koltuk maliyetlerinde (CASK) %8.5 düşüş sağlanacaktır.",
            riskAnalysisSummary = "Rekabet Kurumu onay süreci ve filo entegrasyonu operasyonel gecikme riski taşımaktadır."
        )
    }

    fun generateAiIntelligence(dealId: String): DealAiIntelligence {
        return DealAiIntelligence(
            dealId = dealId,
            dealSummaryText = "Stratejik satın alım teklifi kapsamında $1.8 Milyar USD işlem hacmi ile $2.4 Milyar USD EV değerlemesi hedeflenmiştir.",
            strategicAnalysisText = "Birleşme, şirketin uluslararası kargo ve lojistik ağını 45 yeni noktaya genişleterek Pazar Payını %32'den %41'e çıkaracaktır.",
            costSynergyUsd = 120_000_000.0,
            revenueSynergyUsd = 185_000_000.0,
            riskSummaryText = "Regülasyon onaylarında gecikme ve borçla finansman maliyetlerinin yüksek faiz ortamındaki etkisi.",
            opportunitySummaryText = "Çapraz bilet satışı ve filo optimizasyonu sayesinde yıllık $305M toplam sinerji."
        )
    }

    fun computeDealVisuals(dealId: String): DealVisuals {
        val milestones = listOf(
            DealMilestoneStep(1, "Yönetim Kurulu Bağlayıcı Niyet Mektubu (LOI)", "15 Mayıs 2026", true),
            DealMilestoneStep(2, "Kamuoyu ve KAP Duyurusu (M&A Announcement)", "22 Haziran 2026", true),
            DealMilestoneStep(3, "Finansal & Hukuki Due Diligence", "10 Temmuz 2026", true),
            DealMilestoneStep(4, "Rekabet Kurumu & SEC Regülasyon Onayı", "15 Ağustos 2026", false),
            DealMilestoneStep(5, "Genel Kurul Oylaması & İşlem Kapanışı (Closing)", "30 Eylül 2026", false)
        )

        val multiples = mapOf(
            "THYAO.IS (Hedef)" to 6.8,
            "PGSUS.IS (Sektör)" to 7.4,
            "Lufthansa (Küresel)" to 8.2,
            "Air France-KLM" to 7.9,
            "İşlem Çarpanı (Deal EV/EBITDA)" to 8.5
        )

        val stats = mapOf(
            "Toplam İşlem Hacmi (\$M)" to 1800.0,
            "Ödenen Prim (%)" to 28.5,
            "Tahmini Sinerji (\$M)" to 305.0,
            "Kapanışa Kalan Gün" to 67.0
        )

        return DealVisuals(
            dealTimelineMilestones = milestones,
            industryComparisonMultiples = multiples,
            dealStatisticsMap = stats
        )
    }
}
