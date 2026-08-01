package com.nexus.porsuk.core.domain.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

enum class CorporateEventType {
    MERGER_ACQUISITION, DIVIDEND, CAPITAL_INCREASE, SHARE_BUYBACK
}

data class CorporateEvent(
    val id: String,
    val symbol: String,
    val companyName: String,
    val type: CorporateEventType,
    val title: String,
    val date: String,
    val details: String,
    val valueOrRate: String
)

interface CorporateEventRepository {
    fun getCorporateEvents(): Flow<List<CorporateEvent>>
}

@Singleton
class CorporateEventRepositoryImpl @Inject constructor() : CorporateEventRepository {

    private val eventsState = MutableStateFlow(
        listOf(
            CorporateEvent(
                id = "CE1",
                symbol = "TOASO",
                companyName = "Tofaş Otomobil",
                type = CorporateEventType.MERGER_ACQUISITION,
                title = "Stellantis Otomotiv Satın Alım Anlaşması",
                date = "15 Ağustos 2026",
                details = "Stellantis Türkiye dağıtım haklarının Tofaş tarafından devralınması Rekabet Kurulu nihai onay aşamasındadır.",
                valueOrRate = "400M EUR Devir Bedeli"
            ),
            CorporateEvent(
                id = "CE2",
                symbol = "FROTO",
                companyName = "Ford Otosan",
                type = CorporateEventType.DIVIDEND,
                title = "2026 Nakit Temettü Dağıtımı",
                date = "24 Ekim 2026",
                details = "Hisse başına net ₺43.20 nakit kâr payı dağıtılması Genel Kurul onayına sunulmuştur.",
                valueOrRate = "%3.8 Temettü Verimi"
            ),
            CorporateEvent(
                id = "CE3",
                symbol = "KONTR",
                companyName = "Kontrolmatik Teknoloji",
                type = CorporateEventType.CAPITAL_INCREASE,
                title = "%125 Bedelli & %100 Bedelsiz Sermaye Artırımı",
                date = "02 Eylül 2026",
                details = "ABD Pomega batarya fabrikası kapasite artırımı finansmanı için rüçhan hakkı kullanım süreci başlatılmıştır.",
                valueOrRate = "%225 Toplam Artış"
            ),
            CorporateEvent(
                id = "CE4",
                symbol = "SAHOL",
                companyName = "Sabancı Holding",
                type = CorporateEventType.SHARE_BUYBACK,
                title = "Geri Alınan Payların İptali ve Sermaye Azaltımı",
                date = "10 Eylül 2026",
                details = "Borsa İstanbul'dan geri alınan 10.500.000 adet SAHOL payının sermaye azaltımı yoluyla iptal edilmesi kararlaştırılmıştır.",
                valueOrRate = "10.5M Pay İptali"
            )
        )
    )

    override fun getCorporateEvents(): Flow<List<CorporateEvent>> = eventsState.asStateFlow()
}
