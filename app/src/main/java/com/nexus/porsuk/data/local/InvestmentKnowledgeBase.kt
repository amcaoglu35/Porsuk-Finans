package com.nexus.porsuk.data.local

object InvestmentKnowledgeBase {
    /**
     * Finansal yatırımların efsanevi isimlerinin teorik ve matematiksel kurallarını içeren 
     * yerel bilgi tabanını döner. Yapay zekaya bağlam (context) olarak iletilir.
     */
    fun getClassicFormulas(): String {
        return """
            KLASİK YATIRIM FORMÜLLERİ VE ANALİZ PRENSİPLERİ:
            
            1. BENJAMIN GRAHAM (Güvenlik Marjı ve Graham Sayısı):
               - Graham Sayısı Formülü: Karekök(22.5 * Hisse Başına Karlılık (EPS) * Defter Değeri (Book Value)).
               - Bir hissenin güncel fiyatı, Graham Sayısı'nın altındaysa hisse ucuz kabul edilir ve güvenlik marjı (Margin of Safety) yüksektir.
               - F/K (P/E) oranı en fazla 15.0, Fiyat/Defter Değeri (P/B) oranı en fazla 1.5 olmalıdır. (Çarpımları 22.5'i geçmemelidir).

            2. WARREN BUFFETT (Değer Yatırımcılığı ve Moat/Hendek):
               - Özsermaye Karlılığı (ROE) istikrarlı bir şekilde yıllık %15'in üzerinde olmalıdır.
               - Şirketin borçluluk oranı düşük olmalıdır (Borç / Özkaynak oranı < 0.5).
               - Güçlü marka değeri veya piyasayı domine etme gücü (Ekonomik Hendek - Moat) bulunmalıdır.
               - Yatırım yapılacak şirketin anlaşılması kolay bir iş modeline sahip olması gerekir.

            3. PETER LYNCH (Büyüme Yatırımcılığı ve Tenbagger):
               - PEG Oranı (F/K Oranı / Yıllık Büyüme Oranı) kullanılmalıdır.
               - PEG oranı < 1.0 ise şirket ucuz ve büyüme potansiyeline sahiptir. PEG oranı > 2.0 ise aşırı değerlidir.
               - Şirketleri 6 kategoriye ayırır: Yavaş Büyüyenler, Güvenilir Devler, Hızlı Büyüyenler, Döngüseller, Varlık Değeri Yüksek Olanlar ve Dönüşüm Yaşayanlar.
               - Borçsuz veya nakit pozisyonu borcundan fazla olan hızlı büyüyen şirketler (Tenbagger - 10 katına çıkabilecek hisseler) hedeflenmelidir.
        """.trimIndent()
    }

    fun getRandomTip(): String {
        val tips = listOf(
            "Benjamin Graham'a göre, bir hissenin F/K (P/E) oranı en fazla 15, Fiyat/Defter Değeri (P/B) oranı en fazla 1.5 olmalıdır.",
            "Warren Buffett der ki: 'Fiyat ödediğiniz şeydir, değer ise elde ettiğiniz.' ROE oranı %15'ten büyük şirketleri seçin.",
            "Peter Lynch'e göre, PEG oranı (F/K / Büyüme) 1.0'ın altında olan şirketler ucuz ve büyüme potansiyeline sahiptir.",
            "Güvenlik Marjı (Margin of Safety): Bir hisseyi, gerçek içsel değerinin çok altında bir fiyata alarak riski en aza indirmektir.",
            "Warren Buffett'ın altın kuralı: 1. Kural: Asla para kaybetme. 2. Kural: 1. Kuralı asla unutma.",
            "Peter Lynch'in 'Tenbagger' teorisi: Yatırım yaptığınızda 10 katına çıkabilecek, borçsuz ve hızlı büyüyen şirketleri hedefleyin.",
            "Benjamin Graham Sayısı: Karekök(22.5 * EPS * Book Value). Güncel fiyat bu değerin altındaysa hisse ucuz kabul edilir.",
            "Ekonomik Hendek (Moat): Şirketin rakiplerine karşı sahip olduğu, marka değeri veya patent gibi uzun vadeli rekabet avantajıdır.",
            "Warren Buffett: 'Çorap da olsa, hisse senedi de olsa, kaliteli malı fiyatı düşmüşken almayı severim.'",
            "Peter Lynch: 'Bildiğiniz iş kollarına yatırım yapın. Anlamadığınız karmaşık iş modellerinden uzak durun.'"
        )
        return tips.random()
    }
}
