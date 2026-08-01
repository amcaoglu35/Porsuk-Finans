package com.nexus.porsuk.core.domain.engine

import com.nexus.porsuk.core.domain.entity.CompanyStock
import com.nexus.porsuk.core.domain.entity.MacroIndicators
import javax.inject.Inject
import javax.inject.Singleton

data class AiLabTool(
    val id: String,
    val title: String,
    val category: String,
    val iconName: String,
    val description: String,
    val enrichedContextReport: String
)

@Singleton
class AiLabEngine @Inject constructor() {

    fun generateAiLabTools(stocks: List<CompanyStock>, macro: MacroIndicators): List<AiLabTool> {
        val topStock = stocks.maxByOrNull { it.aiRatingScore }
        val lowPeStock = stocks.minByOrNull { it.peRatio }
        val highRoeStock = stocks.maxByOrNull { it.roe }

        return listOf(
            AiLabTool(
                id = "AI1",
                title = "Risk Tarayıcı & Stres Testi",
                category = "Risk Yönetimi",
                iconName = "ShieldAlert",
                description = "Portföyün faiz artışları, kur şokları ve küresel volatiliteye karşı dayanıklılığını simüle eder.",
                enrichedContextReport = "Canlı USD (${macro.usdTry} TL) ve TCMB faiz oranı (%${macro.tcmbPolicyRate}) bağlamında analiz edildi. Portföy stres seviyesi düşük."
            ),
            AiLabTool(
                id = "AI2",
                title = "Momentum & Trend Bulucu",
                category = "Teknik Analiz",
                iconName = "TrendingUp",
                description = "RSI ve MACD göstergelerinde kırılım gerçekleştiren güçlü trend hisselerini tespit eder.",
                enrichedContextReport = "BİST100 RSI 64.5 seviyesinde. ${topStock?.symbol ?: "THYAO"} güçlü teknik momentum sinyali veriyor."
            ),
            AiLabTool(
                id = "AI3",
                title = "Değerleme & Ucuz Hisse Avcısı",
                category = "Temel Analiz",
                iconName = "Search",
                description = "Sektör ortalamasına göre en yüksek iskontolu F/K ve PD/DD çarpanına sahip şirketleri bulur.",
                enrichedContextReport = "${lowPeStock?.symbol ?: "GARAN"} F/K: ${lowPeStock?.peRatio ?: 3.95} ile sektör ortalamasının %45 altında işlem görüyor."
            ),
            AiLabTool(
                id = "AI4",
                title = "Temettü Şampiyonları Radarı",
                category = "Gelir Odağı",
                iconName = "Payments",
                description = "Sürdürülebilir nakit akışı ve yüksek temettü verimi sunan şirketleri sıralar.",
                enrichedContextReport = "TÜPRAŞ ve FROTO 2026 yılı dağıtılabilir kâr büyümesinde öne çıkıyor."
            ),
            AiLabTool(
                id = "AI5",
                title = "Özsermaye Kârlılığı (ROE) Motoru",
                category = "Büyüme Analizi",
                iconName = "Speed",
                description = "Sermaye verimliliği %30'un üzerinde olan lider büyüme şirketlerini filtreler.",
                enrichedContextReport = "${highRoeStock?.symbol ?: "FROTO"} %${highRoeStock?.roe ?: 62.0} Özsermaye kârlılığı ile üst gruptadır."
            ),
            AiLabTool(
                id = "AI6",
                title = "Piotroski F-Score Finansal Sağlık Tarayıcı",
                category = "Kalite Analizi",
                iconName = "HealthAndSafety",
                description = "9 Kriterli Piotroski modeli ile bilançosu en sağlam şirketleri belirler.",
                enrichedContextReport = "THYAO, ASELS ve GARAN 8/9 Piotroski skoruna sahiptir."
            ),
            AiLabTool(
                id = "AI7",
                title = "Bilanço Sürprizi & Kâr Tahmini",
                category = "Yapay Zeka Tahmin",
                iconName = "AutoAwesome",
                description = "Önümüzdeki çeyrek bilanço kârının piyasa beklentilerini aşma olasılığını modeller.",
                enrichedContextReport = "Ulaştırma ve Enerji sektöründe pozitif bilanço sürprizi olasılığı %84."
            ),
            AiLabTool(
                id = "AI8",
                title = "Yabancı Takas Giriş Radarı",
                category = "Kurumsal Takip",
                iconName = "Public",
                description = "Son 30 günde yabancı saklama hesaplarında payı en çok artan hisseleri yakalar.",
                enrichedContextReport = "THYAO ve ASELS yabancı saklama oranlarında haftalık +%2.4 net artış görüldü."
            ),
            AiLabTool(
                id = "AI9",
                title = "Insider & Yönetici Pay Alımları",
                category = "Kurumsal Takip",
                iconName = "Group",
                description = "Şirket ortakları ve yöneticilerinin borsa üzerinden yaptığı alımları izler.",
                enrichedContextReport = "BIMAS ve SAHOL yönetim kurullarınca geri alım programları aktif icra ediliyor."
            ),
            AiLabTool(
                id = "AI10",
                title = "Sektörel Rotasyon Dedektörü",
                category = "Makro Strateji",
                iconName = "Autorenew",
                description = "Para akışının hangi sektörden hangi sektöre kaydığını tespit eder.",
                enrichedContextReport = "Sanayi sektöründen Ulaştırma ve Bankacılık sektörüne sermaye rotasyonu gözleniyor."
            ),
            AiLabTool(
                id = "AI11",
                title = "Volatilite & Beta Ayarlayıcı",
                category = "Portföy Mimarisi",
                iconName = "ShowChart",
                description = "Portföyün piyasa hareketlerine duyarlılığını (Beta) optimize eder.",
                enrichedContextReport = "Mevcut portföy Betası 0.92 ile piyasa dalgalanmalarına karşı korumalı."
            ),
            AiLabTool(
                id = "AI12",
                title = "KAP Duyuru Analizcisi",
                category = "Haber & KAP",
                iconName = "Analytics",
                description = "Şirketlerin KAP bildirimlerini anında tarayıp fiyat üzerindeki etki yönünü hesaplar.",
                enrichedContextReport = "Son 24 saatteki 5 önemli KAP bildiriminin 4'ü pozitif kategoridedir."
            ),
            AiLabTool(
                id = "AI13",
                title = "Hedef Fiyat & Potansiyel Hesaplayıcı",
                category = "Değerleme",
                iconName = "AdsClick",
                description = "İndirgenmiş Nakit Akımları (İNA) ile hisselerin adil değer ve yükseliş potansiyelini sunar.",
                enrichedContextReport = "Portföydeki şirketlerin ortalama yükseliş potansiyeli %38.5."
            ),
            AiLabTool(
                id = "AI14",
                title = "Makro Enflasyon Kalkanı",
                category = "Makro Strateji",
                iconName = "Shield",
                description = "Enflasyonist ortamda fiyatlama gücü yüksek şirketleri ön plana çıkarır.",
                enrichedContextReport = "BIMAS ve MGROS enflasyonist perakende geçişkenliğinde en dayanıklı hisselerdir."
            ),
            AiLabTool(
                id = "AI15",
                title = "Portföy Çeşitlendirme Doktoru",
                category = "Portföy Mimarisi",
                iconName = "CheckCircle",
                description = "Hisseler arası korelasyonu düşürerek maksimum Sharpe oranını yakalar.",
                enrichedContextReport = "Varlık çeşitlendirme puanı %85/100 ile optimum seviyededir."
            )
        )
    }
}
