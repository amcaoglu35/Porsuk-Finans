package com.nexus.porsuk.core.domain.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

enum class NewsImpact {
    POSITIVE, NEUTRAL, NEGATIVE
}

data class NewsItem(
    val id: String,
    val title: String,
    val source: String,
    val timeAgo: String,
    val relatedSymbol: String?,
    val aiSummary: String,
    val impact: NewsImpact
)

interface NewsRepository {
    fun getLatestNews(): Flow<List<NewsItem>>
}

@Singleton
class NewsRepositoryImpl @Inject constructor() : NewsRepository {

    private val newsState = MutableStateFlow(
        listOf(
            NewsItem(
                id = "N1",
                title = "Türk Hava Yolları 2026/Q2 Rekor Yolcu Sayısı Açıkladı",
                source = "KAP & Matriks",
                timeAgo = "15 dk önce",
                relatedSymbol = "THYAO",
                aiSummary = "AI Özeti: Yolcu doluluk oranı %86'ya yükseldi. Operasyonel kârlılık uçuş ağındaki genişleme ile beklentilerin üzerine çıktı.",
                impact = NewsImpact.POSITIVE
            ),
            NewsItem(
                id = "N2",
                title = "Aselsan Yeni İhracat Sözleşmesi İmzaladığını Duyurdu",
                source = "KAP Açıklaması",
                timeAgo = "42 dk önce",
                relatedSymbol = "ASELS",
                aiSummary = "AI Özeti: 125 milyon USD tutarındaki savunma sistemi ihracat anlaşması bakiye sipariş miktarını ve ciro büyümesini destekliyor.",
                impact = NewsImpact.POSITIVE
            ),
            NewsItem(
                id = "N3",
                title = "TCMB Para Politikası Kurulu Faiz Kararı Öncesi Piyasa Beklentileri",
                source = "Bloomberg HT",
                timeAgo = "1 saat önce",
                relatedSymbol = "GARAN",
                aiSummary = "AI Özeti: Merkez Bankası'nın faiz oranlarını sabitlemesi bekleniyor. Bankacılık sektörü net faiz marjı pozitif etkilenebilir.",
                impact = NewsImpact.NEUTRAL
            ),
            NewsItem(
                id = "N4",
                title = "Astor Enerji Yeni Üretim Tesisinin Açılışını Gerçekleştirdi",
                source = "Finans Gündem",
                timeAgo = "3 saat önce",
                relatedSymbol = "ASTOR",
                aiSummary = "AI Özeti: Yıllık transformatör üretim kapasitesi %40 artacak. İhracat payının %55'e çıkarılması hedefleniyor.",
                impact = NewsImpact.POSITIVE
            ),
            NewsItem(
                id = "N5",
                title = "Petkim Hammadde Marjlarında Geçici Daralma Yaşandığını Bildirdi",
                source = "KAP Açıklaması",
                timeAgo = "5 saat önce",
                relatedSymbol = "PETKM",
                aiSummary = "AI Özeti: Etilen-Nafta spradindeki küresel daralma çeyreklik faaliyet kâr marjı üzerinde baskı oluşturabilir.",
                impact = NewsImpact.NEGATIVE
            )
        )
    )

    override fun getLatestNews(): Flow<List<NewsItem>> = newsState.asStateFlow()
}
