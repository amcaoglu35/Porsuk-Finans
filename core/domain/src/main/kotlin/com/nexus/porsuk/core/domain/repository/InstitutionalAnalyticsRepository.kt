package com.nexus.porsuk.core.domain.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

data class InstitutionalHolding(
    val symbol: String,
    val companyName: String,
    val foreignShareRatio: Double,
    val foreignRatioWeeklyChange: Double,
    val pensionFundRatio: Double,
    val insiderTransactionSummary: String,
    val institutionalSentiment: String
)

interface InstitutionalAnalyticsRepository {
    fun getInstitutionalData(): Flow<List<InstitutionalHolding>>
}

@Singleton
class InstitutionalAnalyticsRepositoryImpl @Inject constructor() : InstitutionalAnalyticsRepository {

    private val institutionalState = MutableStateFlow(
        listOf(
            InstitutionalHolding(
                symbol = "THYAO",
                companyName = "Türk Hava Yolları",
                foreignShareRatio = 54.8,
                foreignRatioWeeklyChange = 2.45,
                pensionFundRatio = 12.6,
                insiderTransactionSummary = "Son 30 günde Citibank ve Deutsche Bank takaslarında net 14.2M lot artış kaydedildi.",
                institutionalSentiment = "GÜÇLÜ YABANCI GİRİŞİ"
            ),
            InstitutionalHolding(
                symbol = "GARAN",
                companyName = "Garanti BBVA",
                foreignShareRatio = 48.2,
                foreignRatioWeeklyChange = 1.60,
                pensionFundRatio = 15.4,
                insiderTransactionSummary = "Ana ortak BBVA tarafından kurumsal takas oranı korunuyor. Yerli fonlarda alım hakim.",
                institutionalSentiment = "GÜÇLÜ YABANCI GİRİŞİ"
            ),
            InstitutionalHolding(
                symbol = "ASELS",
                companyName = "Aselsan Elektronik",
                foreignShareRatio = 38.5,
                foreignRatioWeeklyChange = 3.10,
                pensionFundRatio = 18.2,
                insiderTransactionSummary = "Emeklilik fonları ve yatırım fonları tarafından portföy ağırlığı %2.5 artırıldı.",
                institutionalSentiment = "GÜÇLÜ YABANCI GİRİŞİ"
            ),
            InstitutionalHolding(
                symbol = "KCHOL",
                companyName = "Koç Holding",
                foreignShareRatio = 62.1,
                foreignRatioWeeklyChange = 0.85,
                pensionFundRatio = 11.0,
                insiderTransactionSummary = "Koç Ailesi bireysel alımları ve yabancı saklama hesaplarında pozitif giriş sürüyor.",
                institutionalSentiment = "NÖTR / POZİTİF"
            ),
            InstitutionalHolding(
                symbol = "BIMAS",
                companyName = "BİM Birleşik Mağazalar",
                foreignShareRatio = 58.4,
                foreignRatioWeeklyChange = 1.90,
                pensionFundRatio = 14.8,
                insiderTransactionSummary = "Geri alım programı kapsamında şirket yönetimince 150.000 lot hisse geri alındı.",
                institutionalSentiment = "KURUMSAL ALIM"
            )
        )
    )

    override fun getInstitutionalData(): Flow<List<InstitutionalHolding>> = institutionalState.asStateFlow()
}
