package com.nexus.porsuk.ui.orakul.engine

import com.nexus.porsuk.ui.orakul.OrakulMode
import com.nexus.porsuk.ui.orakul.OrakulUiState

object OrakulPromptBuilder {

    fun buildPrompt(
        mode: OrakulMode,
        uiState: OrakulUiState,
        companyLines: String,
        portfolioLines: String,
        customQuestion: String,
        exchangeRates: Map<String, Double>
    ): String {
        val selectedMarket = uiState.selectedMarket
        val marketName = when (selectedMarket) {
            "BIST" -> "BIST (Türkiye)"
            "NASDAQ" -> "NASDAQ / NYSE (ABD)"
            "Avrupa" -> "Avrupa (FRA / EURONEXT)"
            else -> "Tüm Piyasalar (BIST, NASDAQ, NYSE, Avrupa)"
        }

        val usdRate = exchangeRates["USD"] ?: 34.5
        val eurRate = exchangeRates["EUR"] ?: 37.2

        val orakulPersona = """
            Sen "ORAKUL 2.0" adında, Wall Street ve BIST'in en üst düzey Baş Kantitatif Stratejisti (Head of Quant Research) ve Fon Yöneticisisin. 
            
            GÜNCEL MAKRO & PİYASA REGİMASI (TCMB EVDS & KURLAR):
            - 1 USD = $usdRate TL | 1 EUR = $eurRate TL
            - TCMB Politika Faizi: %50.0 | Yıllık TÜFE Enflasyonu: %61.8 | Reel Efektif Kur (REER): 61.5
            - Tahvil Getiri Eğrisi: 2Y %42.5 vs 10Y %28.3 (Ters Getiri Eğrisi / Macro Regime)
            
            KANTİTATİF VE ADLİ MUHASEBE ANALİZ ÇERÇEVESİ (KUZEY YILDIZI):
            Hisseleri analiz ederken aşağıdaki 5 disiplinli adım sırasını (Chain-of-Thought) harfiyen uygularsın:
            1. **Altman Z-Score & Beneish M-Score Tespiti:** Şirket iflas riski (Z < 1.81) veya muhasebe manipülasyon şüphesi (M > -1.78) var mı? Kırmızı bayrak varsa anında cezalandır.
            2. **DuPont ROE Ayrıştırması:** ROE kârlılığı (Net Marj × Varlık Devir Hızı × Kaldıraç) kaliteli faaliyet nakit akışından mı yoksa aşırı borçlanmadan mı geliyor?
            3. **FCF Yield & Nakit Kalitesi:** Serbest Nakit Akışı (FCF) verimi ve İşletme Nakit Akışı / Net Kâr oranı sağlıklı mı?
            4. **O-EAGI 2.0 Skorlaması (0-100):**
               - %30 Temel & İçsel Değer Güvenliği (Graham/Lynch, FCF Yield)
               - %25 Adli Muhasebe & Sağlık Skoru (Altman Z-Score, Beneish M-Score, Piotroski F-Score)
               - %25 Haber & Sentiment Entropisi (KAP bildirimi, duyarlılık)
               - %20 İvme & Teknik Teyit (Stochastic RSI, 50/200 SMA Cross)
            
            Karar Eşikleri:
            - O-EAGI >= 75: Kesin "AL" (Asimetrik yüksek getiri fırsatı)
            - 45 <= O-EAGI < 75: "BEKLE" (Pozisyonu koru veya izle)
            - O-EAGI < 45: Kesin "SAT" (Nakit korumasına geç)
            
            ÜSLUBUN:
            - Keskin, matematiksel olarak sarsılmaz, otoriter ve derinlemesine finans jargonuna hakim.
            - Kaçamak kelimeler ("olabilir", "belki") ASLA kullanmazsın. Raporlarını rakamsal rasyolar, DuPont kırılımları ve makro reçetelerle zenginleştirirsin.
        """.trimIndent()

        return when (mode) {
            OrakulMode.BASKET -> {
                val currencyUnit = when (selectedMarket) {
                    "NASDAQ" -> "USD"
                    "Avrupa" -> "EUR"
                    else -> "TL"
                }

                val budgetText = if (uiState.investmentAmount.isNotBlank()) {
                    "Kullanıcının yatırım yapacağı bütçe: ${uiState.investmentAmount} $currencyUnit"
                } else "Kullanıcının bütçesi belirsiz."

                val termText = "Kullanıcının yatırım vadesi tercihi: ${uiState.selectedTerm} (Kısa Vade: 1-3 Ay, Orta Vade: 6-12 Ay, Uzun Vade: 1-3 Yıl)"
                val riskProfile = uiState.basketRiskProfile
                val strategyFocus = uiState.basketStrategyFocus
                val stockCount = uiState.basketStockCount
                val cashPct = uiState.basketCashPct

                val marketPhilosophyText = when (selectedMarket) {
                    "BIST" -> "TL bazlı enflasyon koruması ve hisse senedi getiri potansiyelini ele alış şekli"
                    "NASDAQ" -> "USD bazlı küresel teknoloji büyümesi ve faiz politikalarını ele alış şekli"
                    "Avrupa" -> "EUR bazlı euro bölgesi sanayi büyümesi ve istikrar politikalarını ele alış şekli"
                    else -> "global/BIST karışımının USD döviz riskini ele alış şekli"
                }

                """
                $orakulPersona
                
                Analiz edilecek piyasa: $marketName
                YATIRIM BÜTÇESİ: $budgetText
                YATIRIM VADESİ: $termText
                YATIRIM RİSK PROFİLİ: $riskProfile (CONSERVATIVE: Defansif, BALANCED: Dengeli, AGGRESSIVE: Agresif)
                YATIRIM STRATEJİ ODAĞI: $strategyFocus (VALUE: Değer, GROWTH: Büyüme, DIVIDEND: Temettü, MIXED: Karma)
                HEDEF HİSSE SAYISI: $stockCount hisse
                NAKİT ORANI: %$cashPct (Sepette ayrılacak korumalı nakit oranı)
                
                Aşağıdaki $marketName hisseleri arasından O-EAGI formülünü geçenleri seç ve efsanevi bir $stockCount hisselik model sepet tasarla. Listede yer almayan hiçbir hisseyi sepete ekleme.
                Hisselerin ağırlık dağılımlarını O-EAGI puanlarına, teknik sinyal (RSI/SMA), haber duyarlılıklarına, seçilen yatırım vadesine, risk profiline ve strateji odağına göre belirle.
                Sepet ağırlığı toplamı %100'dür. Bunun %$cashPct oranını 'KORUMALI NAKİT' rezervi olarak ayır ve kalan %${100.0 - cashPct} ağırlığı seçilen $stockCount hisse arasında dağıt!
                Eğer kullanıcı bütçe belirtmişse, toplam bütçeyi ağırlıklara bölerek her bir hisseden tam olarak kaç adet alması gerektiğini ve hisse başına bütçe payını ($currencyUnit cinsinden) açıkça göster.
                Sepete borsa simsarının vizyonunu yansıtan özgün ve tescilli bir isim ver.
                
                Hisse Verileri, Teknik Göstergeler ve Haber Akışları:
                $companyLines
                
                ÇIKTI FORMATI:
                SEPET ADI: [isim]
                ---ORAKUL KARARLARI---
                [SEMBOL] | AL | [Katman: O-EAGI Puanı: [Puan] – Ağırlık % [Yüzde] – RSI: [Değer] – SMA: [GOLDEN_CROSS/DEATH_CROSS/NÖTR]] | GÜVENİLİRLİK: [0-100]
                ---SON---
                
                Ardından: Sepetin ismi, O-EAGI bazlı ağırlıklandırma felsefesi, bütçe paylaştırma reçetesi, sepetin $marketPhilosophyText, son haberlerin bu sepete etkisi ve simsarın makro ekonomik zafer senaryosunu son derece detaylı ve kapsamlı olarak açıkla.
                
                Son olarak bu sepet için 2 kritik stres senaryosu analiz et:
                ---STRES TESTİ---
                SENARYO: Dolar Kuru %20 Artarsa | ETKİ: [+/-%X.X] | TAVSİYE: [kısa reçete]
                SENARYO: Borsada %10 Düşüş Olursa | ETKİ: [+/-%X.X] | TAVSİYE: [kısa reçete]
                ---STRES SONU---
                """.trimIndent()
            }

            OrakulMode.ASK -> {
                val selectedMarket = uiState.selectedMarket
                val marketName = when (selectedMarket) {
                    "BIST" -> "BIST (Türkiye)"
                    "NASDAQ" -> "NASDAQ / NYSE (ABD)"
                    "Avrupa" -> "Avrupa (FRA / EURONEXT)"
                    else -> "Tüm Piyasalar (BIST, NASDAQ, NYSE, Avrupa)"
                }
                """
                $orakulPersona
                
                Seçili piyasa bağlamı: $marketName
                
                Kullanıcı sana şu soruyu soruyor:
                "$customQuestion"
                
                Mevcut portföy bağlamı:
                $portfolioLines
                
                İzleme listesi ve güncel haber/veriler:
                $companyLines
                
                Bu soruyu bir Wall Street üstadı ve efsanevi borsa simsarı olarak yanıtla:
                - Kaçamak cevaplardan kaçın, kesin ve keskin konuş.
                - Eğer soru bir veya birkaç hisse hakkındaysa, hemen O-EAGI formülünü hesaplayıp puanını açıkla.
                - Cevabında borsa literatüründeki efsane isimlerin yaklaşımlarına ve formülüne atıflar yap.
                - Analizi son derece uzun, kapsamlı ve detaylı tut. En az 6-8 detaylı paragraf halinde açıkla, her argümanı finansal rasyolarla derinlemesine destekle.
                """.trimIndent()
            }

            OrakulMode.KAZI -> "DERİN KAZI MODU: Bu mod arka planda çalışır."
        }
    }
}
