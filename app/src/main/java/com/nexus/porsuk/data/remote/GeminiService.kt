package com.nexus.porsuk.data.remote

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.nexus.porsuk.data.local.entity.CachedCompanyInfo
import com.nexus.porsuk.data.local.entity.NewsItemEntity
import com.nexus.porsuk.data.local.entity.PriceSnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GeminiService(private val apiKey: String) {

    private val systemInstructionContent = content {
        text("""
            Sen finans dünyasının efsane isimlerinin (Benjamin Graham, Warren Buffett, Peter Lynch) 
            yatırım felsefelerine ve teknik/temel analiz kurallarına tamamen hakim bir "Borsa Profesörü"sün.
            Analizlerini yaparken şu kuralları temel al:
            - Warren Buffett'ın "Değer Yatırımcılığı" (Value Investing) ve Moat (Hendek) analizleri.
            - F/K ve Defter Değeri oranlarında Benjamin Graham'ın güvenlik marjı (Margin of Safety) prensibi.
            - Peter Lynch'in büyüme ve hisse kategorizasyonu teorileri.
            Kullanıcıya sadece kuru rakamlar verme, bu teorik borsa uzmanı kimliğini analizlerine yansıt.
            Analizlerini daima Türkçe, samimi ama son derece profesyonel ve esprili bir üslup ile gerçekleştir.
        """.trimIndent())
    }

    private suspend fun generateContentWithFallback(prompt: String): String {
        return com.nexus.porsuk.ui.common.GeminiModels.generateContentWithFallback(
            apiKey = apiKey,
            prompt = prompt,
            systemInstruction = systemInstructionContent
        )
    }

    suspend fun getStockAnalysis(
        symbol: String,
        companyInfo: CachedCompanyInfo?,
        price: PriceSnapshot?,
        news: List<NewsItemEntity>,
        userCost: Double = 0.0
    ): String = withContext(Dispatchers.IO) {
        val newsTitles = news.take(3).joinToString("; ") { "${it.title} (Duyarlılık: ${it.sentiment ?: "NEUTRAL"})" }
        
        val prompt = """
            Sen Wall Street'in efsanevi borsa simsarı "ORAKUL"sun. Kendine has tescilli formülün olan O-EAGI (Orakul Entropi ve Asimetrik Güç İndeksi) felsefesine göre bu şirketi analiz et.
            
            Şirket: $symbol
            Güncel Fiyat: ${price?.price ?: companyInfo?.week52Low ?: "Bilinmiyor"}
            F/K Oranı: ${companyInfo?.peRatio ?: "Bilinmiyor"}
            Temettü Verimi: ${companyInfo?.dividendYield ?: "Bilinmiyor"}
            52 Hafta Aralığı: ${companyInfo?.week52Low} - ${companyInfo?.week52High}
            Son Haberler ve Duyarlılıklar: $newsTitles
 
            Görevin:
            1. Bu veriler ışığında hissenin O-EAGI skorunu (0 - 100 arası) hesapla.
            2. Formülün 4 alt bileşeni için de puan ver (her biri 0 - 100 arası):
               - Güvenlik Marjı ve İçsel Değer (Graham & Lynch)
               - Haber Duyarlılığı Entropisi
               - Momentum & Akıllı Para İvmesi
               - Sektörel Alfa Gücü
            3. Analiz dökümünü ve detaylı simsar yorumunu aşağıdaki formata göre dön. Formatı bozma.
 
            ÇIKTI FORMATI:
            O-EAGI SKORU: [Skor]
            GÜVENLİK MARJI: [Skor]
            HABER ENTROPİSİ: [Skor]
            MOMENTUM: [Skor]
            SEKTÖR ALFA: [Skor]
            ---
            [Orakul'un tescilli derin ve geniş çaplı analiz yorumu. Türkçe, net, simsar üslubuyla, aşağıdaki başlıkları içeren detaylı ve profesyonel bir rapor:
            - TEMEL GÖSTERGELER & GÜVENLİK MARJI ANALİZİ (Graham ve Buffett yaklaşımıyla F/K, içsel değer ve güvenlik marjı analizi)
            - ENTROPİ & DUYARLILIK ANALİZİ (Son haber akışı, kap haberleri ve pazar algısı analizi)
            - MOMENTUM & TEKNİK GÖRÜNÜM (RSI, Bollinger ve akıllı para hareketlerinin analizi)
            - SEKTÖREL ALFA & STRATEJİK DEĞERLENDİRME (Sektör içindeki gücü ve gelecek projeksiyonları)
            Yorum en az 4-5 paragraftan oluşmalı ve son derece detaylı olmalıdır.]
        """.trimIndent()

        try {
            generateContentWithFallback(prompt)
        } catch (e: Exception) {
            "O-EAGI SKORU: 0\nGÜVENLİK MARJI: 0\nHABER ENTROPİSİ: 0\nMOMENTUM: 0\nSEKTÖR ALFA: 0\n---\n${com.nexus.porsuk.ui.common.GeminiErrorParser.parse(e)}"
        }
    }

    suspend fun getPortfolioHealthCheck(
        holdings: List<com.nexus.porsuk.data.local.entity.BasketItem>,
        companies: List<com.nexus.porsuk.data.local.entity.Company>
    ): String = withContext(Dispatchers.IO) {
        val companyMap = companies.associateBy { it.symbol }
        
        val sectorValues = mutableMapOf<String, Double>()
        var totalValue = 0.0
        holdings.forEach { item ->
            val comp = companyMap[item.symbol]
            val sector = comp?.sector ?: "Diğer"
            val price = comp?.currentPrice ?: item.buyPrice
            val value = item.quantity * price
            sectorValues[sector] = (sectorValues[sector] ?: 0.0) + value
            totalValue += value
        }
        
        val sectorInfo = sectorValues.entries.joinToString { 
            val pct = if (totalValue > 0) (it.value / totalValue * 100) else 0.0
            "${it.key}: %${String.format(java.util.Locale.US, "%.1f", pct)}"
        }
        
        val holdingsInfo = holdings.joinToString { 
            "hisse: ${it.symbol} (${it.quantity} adet)"
        }
        
        val prompt = """
            ${com.nexus.porsuk.data.local.InvestmentKnowledgeBase.getClassicFormulas()}

            Sen son derece profesyonel, veri odaklı ve esprili bir "Borsa Profesörü" finansal danışmanısın. Yukarıdaki klasik yatırım formüllerine ve analiz prensiplerine göre kullanıcının hisse senedi portföyünü analiz et.
            
            Kullanıcının portföyündeki varlıklar: $holdingsInfo
            Sektörel Dağılım: $sectorInfo
            Toplam Değer: $totalValue TRY
            
            Görevin:
            1. Portföyün risk, çeşitlendirme ve genel sağlık durumunu analiz ederek 100 üzerinden bir "Sağlık Puanı" belirle.
            2. Bu puanı ilk satırda kalın olarak yaz: "**Sağlık Puanı: X/100**" formatında.
            3. Altına en fazla 3-4 maddeden oluşan kısa, samimi, esprili ve doğrudan uygulanabilir Türkçe öneriler/check-up maddeleri yaz. Yatırım tavsiyesi olmadığını (YTD) hatırlat ama eğlenceli ve profesyonel bir üslup kullan.
        """.trimIndent()

        try {
            generateContentWithFallback(prompt)
        } catch (e: Exception) {
            com.nexus.porsuk.ui.common.GeminiErrorParser.parse(e)
        }
    }

    suspend fun getPortfolioRebalanceReport(
        holdings: List<com.nexus.porsuk.data.local.entity.BasketItem>,
        companies: List<com.nexus.porsuk.data.local.entity.Company>
    ): String = withContext(Dispatchers.IO) {
        val companyMap = companies.associateBy { it.symbol }
        
        var totalValue = 0.0
        val holdingsInfo = holdings.joinToString("\n") { item ->
            val comp = companyMap[item.symbol]
            val price = comp?.currentPrice ?: item.buyPrice
            val value = item.quantity * price
            totalValue += value
            "  • ${item.symbol}: Adet: ${item.quantity}, Alış: ${item.buyPrice}, Güncel: $price, Toplam Değer: ${String.format(java.util.Locale.US, "%.1f", value)} TRY"
        }

        val prompt = """
            Sen Wall Street'in efsanevi borsa simsarı ve finans üstadı "ORAKUL"sun. Kendine has tescilli formülün olan O-EAGI (Orakul Entropi ve Asimetrik Güç İndeksi) felsefesine göre kullanıcının mevcut portföyünü yeniden dengelemek (rebalance) için kesin, net ve profesyonel bir optimizasyon raporu sun.

            Kullanıcının Mevcut Portföyü:
            $holdingsInfo
            Toplam Değer: ${String.format(java.util.Locale.US, "%.1f", totalValue)} TRY
 
            Görevin:
            1. Portföyün mevcut ağırlık dağılımındaki dengesizlikleri veya aşırı riskli pozisyonları net bir şekilde belirt.
            2. Her hisse senedi için tam olarak ne yapılması gerektiğini (örneğin: "X hissesini azalt, %20 ağırlığa çek" veya "Y hissesini artır, %15 ağırlık ekle" veya "Z hissesini tamamen sat") söyle. Kaçamak cevaplardan kaçın, kesin hedefler ver.
            3. Dengeleme sonrasında portföyün hedef yüzde dağılımını (örneğin: EREGL %20, THYAO %30 vb.) gösteren net bir liste ver.
            4. Orakul simsarı gibi kendinden emin, keskin ve doğrudan uygulanabilir Türkçe bir üslup kullan.
        """.trimIndent()

        try {
            generateContentWithFallback(prompt)
        } catch (e: Exception) {
            com.nexus.porsuk.ui.common.GeminiErrorParser.parse(e)
        }
    }

    /** Genel amaçlı prompt → yanıt wrapper'ı. */
    suspend fun chat(prompt: String): String = withContext(Dispatchers.IO) {
        try {
            generateContentWithFallback(prompt)
        } catch (e: Exception) {
            com.nexus.porsuk.ui.common.GeminiErrorParser.parse(e)
        }
    }
}
