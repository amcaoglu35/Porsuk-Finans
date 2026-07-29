# AI Oracle & AI Lab Production Upgrade - Walkthrough

Porsuk Finans uygulamasındaki AI yetenekleri artık gerçek finansal verilerle beslenen, profesyonel analitik araçlarla donatılmış prodüksiyon seviyesine yükseltildi.

## Temel Özellikler ve Değişiklikler

### 1. AI Oracle: Derinlemesine Hisse Kehaneti
- **Hisse Arama ve Analiz:** Kullanıcılar artık herhangi bir hisseyi aratarak Orakul'un o hisse özelindeki derin analizini alabiliyor.
- **10 Kritik Metrik Kartı:** AI Skoru, Risk, Büyüme, Temettü, Sağlık, Momentum, Volatilite, Likidite, Kalite ve Güven metrikleri gerçek rasyolarla hesaplanıyor.
- **Dinamik SWOT ve Görünüm:** Gemini AI, FMP'den gelen finansal tabloları analiz ederek şirketin güçlü/zayıf yönlerini ve kısa/uzun vadeli beklentilerini raporluyor.
- **İnteraktif Yatırım Tezi:** Markdown formatında sunulan, finansal argümanlarla desteklenmiş profesyonel yatırım tezleri üretiliyor.

### 2. AI Lab: 15 Yeni Uzman Araç
- **Uzman Araç Izgarası:** 15 farklı uzman modül (Portfolio Health Check, Dividend Finder, Risk Scanner, Economic Impact Analyzer vb.) eklendi.
- **Bağlamsal Analiz:** "Portfolio Health Check" kullanıcının Room'daki gerçek portföyünü baz alırken, "Economic Impact Analyzer" makro verileri (FRED) analiz ediyor.
- **Anlık Raporlama:** Her araç için özel prompt şablonları oluşturuldu ve GeminiService üzerinden yüksek doğruluklu Markdown raporları üretilmesi sağlandı.
- **Hata ve Yükleme Yönetimi:** Her araç için bağımsız Loading, Error ve Retry durumları eklendi.

### 3. Teknik Altyapı ve Veri Entegrasyonu
- **Hibrit Veri Bağlamı:** AI artık Financial Modeling Prep (Financials), Finnhub (Price) ve NewsAPI (News) verilerini tek bir bağlamda birleştirerek yorum yapıyor.
- **MVVM & Flow Mimari:** Tüm AI akışları StateFlow ile yönetiliyor ve veritabanı önbelleği sayesinde offline mod destekleniyor.
- **Optimize Edilmiş Promptlar:** `GeminiPromptBuilder` yapılandırılmış JSON çıktıları ve uzman persona tanımlarıyla güncellendi.

## Ekran Görüntüleri ve UI Detayları

> [!TIP]
> AI Lab modülündeki **Portfolio Health Check** aracını kullanarak portföyünüzün Gemini tarafından klinik bir teşhisini alabilirsiniz.

> [!IMPORTANT]
> AI Oracle analizleri için **Ayarlar** kısmından geçerli bir Gemini API anahtarı girildiğinden emin olun.

### Yapılan Teknik İyileştirmeler
- **FinanceRepository:** AI bağlamı oluşturmak için finansal verileri toplayan yeni metodlarla güçlendirildi.
- **ViewModel Sadeleştirmesi:** `OrakulViewModel` ve `AiLabViewModel` sadece UI state ve tetikleyici logic'lerle ilgilenecek şekilde optimize edildi.
- **Modern UI:** Material 3 bileşenleri, animasyonlu Gauge göstergeleri ve Markdown metin desteği ile kullanıcı deneyimi artırıldı.
