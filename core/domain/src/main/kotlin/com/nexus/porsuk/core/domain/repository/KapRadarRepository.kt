package com.nexus.porsuk.core.domain.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

enum class KapCategory {
    BILANCO, OZEL_DURUM, PAY_ALIM_SATIM, SERMAYE_ARTRIMI, TEMETTU
}

data class KapNotice(
    val id: String,
    val symbol: String,
    val companyName: String,
    val title: String,
    val category: KapCategory,
    val publishTime: String,
    val summary: String,
    val isImportant: Boolean = true
)

interface KapRadarRepository {
    fun getKapNotices(): Flow<List<KapNotice>>
    fun getFilteredKapNotices(categoryFilter: String, portfolioSymbols: Set<String>): Flow<List<KapNotice>>
}

@Singleton
class KapRadarRepositoryImpl @Inject constructor() : KapRadarRepository {

    private val kapState = MutableStateFlow(
        listOf(
            KapNotice(
                id = "KAP1",
                symbol = "THYAO",
                companyName = "Türk Hava Yolları",
                title = "2026 2. Çeyrek Finansal Raporu ve Bilanço Açıklaması",
                category = KapCategory.BILANCO,
                publishTime = "Bugün 18:45",
                summary = "Şirketimiz 2. çeyrekte 18.4 Milyar TL net kâr açıklamıştır. Satış gelirleri geçen yılın aynı dönemine göre %42 artmıştır."
            ),
            KapNotice(
                id = "KAP2",
                symbol = "ASELS",
                companyName = "Aselsan Elektronik",
                title = "Yeni İş İlişkisi ve İhracat Sözleşmesi Bildirimi",
                category = KapCategory.OZEL_DURUM,
                publishTime = "Bugün 17:15",
                summary = "Aselsan ile yurt dışındaki bir müşteri arasında 98 Milyon ABD Doları tutarında savunma sistemleri tedarik sözleşmesi imzalanmıştır."
            ),
            KapNotice(
                id = "KAP3",
                symbol = "KCHOL",
                companyName = "Koç Holding",
                title = "Şirket Ortakları Tarafından Pay Alım Bildirimi",
                category = KapCategory.PAY_ALIM_SATIM,
                publishTime = "Bugün 16:30",
                summary = "Nitelikli yatırımcılar tarafından borsa fiyatından 250.000 adet pay alımı gerçekleştirilmiştir."
            ),
            KapNotice(
                id = "KAP4",
                symbol = "ASTOR",
                companyName = "Astor Enerji",
                title = "%100 Bedelsiz Sermaye Artırımı SPK Onayı",
                category = KapCategory.SERMAYE_ARTRIMI,
                publishTime = "Dün 19:10",
                summary = "Sermaye Piyasası Kurulu (SPK), şirketimizin iç kaynaklardan %100 oranında bedelsiz sermaye artırımı başvurusunu onaylamıştır."
            ),
            KapNotice(
                id = "KAP5",
                symbol = "TUPRS",
                companyName = "Tüpraş",
                title = "2026 Yılı 2. Taksit Temettü Dağıtım Tarihi Kesinleşti",
                category = KapCategory.TEMETTU,
                publishTime = "Dün 14:20",
                summary = "Hisse başına net ₺6.85 nakit temettü ödemesi 28 Eylül 2026 tarihinde yatırımcı hesaplarına yatırılacaktır."
            )
        )
    )

    override fun getKapNotices(): Flow<List<KapNotice>> = kapState.asStateFlow()

    override fun getFilteredKapNotices(
        categoryFilter: String,
        portfolioSymbols: Set<String>
    ): Flow<List<KapNotice>> {
        return kapState.map { list ->
            list.filter { item ->
                val matchesCategory = when (categoryFilter) {
                    "Portföyüm" -> portfolioSymbols.contains(item.symbol)
                    "Temettü" -> item.category == KapCategory.TEMETTU
                    "Bilanço" -> item.category == KapCategory.BILANCO
                    "Sermaye" -> item.category == KapCategory.SERMAYE_ARTRIMI
                    else -> true
                }
                matchesCategory
            }
        }
    }
}
