# Production Level News & Macro Economics Module Upgrade

Bu plan, Porsuk Finans uygulamasındaki Haberler ve Makro Ekonomi modüllerini gerçek verilere dayalı, profesyonel analitik araçlarla donatılmış prodüksiyon seviyesine çıkarmayı hedeflemektedir.

## User Review Required

> [!IMPORTANT]
> - **Veri Kaynakları:** Haberler için NewsAPI, makro ekonomik göstergeler için FRED (Federal Reserve Economic Data), ekonomik takvim için Finnhub kullanılacaktır.
> - **AI Sentiment:** Her haber Gemini AI tarafından özetlenecek ve duyarlılık (Pozitif/Negatif/Nötr) analizi yapılacaktır.
> - **Bildirimler:** Yüksek etkili ekonomik olaylar için kullanıcıya anlık bildirim desteği eklenecektir.

## Open Questions
- Haberler NewsAPI üzerinden çekilirken sadece İngilizce mi yoksa Türkçe kaynaklar mı önceliklendirilmeli?
- FRED verileri için başlangıçta kaç yıllık geçmiş veri gösterilmeli? (Öneri: 5 veya 10 yıl).

## Proposed Changes

### [Component Name] Data & Domain Layer

#### [NEW] [MacroDataEntity](file:///C:/Users/amcao/AndroidStudioProjects/Porsuk/app/src/main/java/com/nexus/porsuk/data/local/entity/Entities.kt)
- FRED'den gelen (CPI, GDP, Unemployment vb.) serileri saklamak için yeni tablo.

#### [MODIFY] [AssetDao.kt](file:///C:/Users/amcao/AndroidStudioProjects/Porsuk/app/src/main/java/com/nexus/porsuk/data/local/dao/AssetDao.kt)
- Makro verileri için `insertMacroData` ve `getMacroData(seriesId)` metodları.
- Haberler için favori ve okundu işaretleme metodları.

#### [MODIFY] [FinanceRepository.kt](file:///C:/Users/amcao/AndroidStudioProjects/Porsuk/app/src/main/java/com/nexus/porsuk/data/repository/FinanceRepository.kt)
- `refreshNewsByCategory(category)`: NewsAPI entegrasyonu ile kategorize edilmiş haber çekme.
- `refreshMacroIndicators()`: FRED API ile tüm makro serileri güncelleme.
- `refreshEconomicCalendar()`: Finnhub üzerinden haftalık takvimi çekme.

### [Component Name] ViewModel Layer

#### [MODIFY] [NewsViewModel.kt](file:///C:/Users/amcao/AndroidStudioProjects/Porsuk/app/src/main/java/com/nexus/porsuk/feature/news/NewsViewModel.kt)
- Kategori yönetimi (Latest, Company, Sector, Economy, Global, Tech, AI).
- Gemini AI ile toplu haber özetleme ve sentiment analizi logic'i.
- Arama ve filtreleme StateFlow'ları.

#### [MODIFY] [MacroIntelligenceViewModel.kt](file:///C:/Users/amcao/AndroidStudioProjects/Porsuk/app/src/main/java/com/nexus/porsuk/feature/macro/MacroIntelligenceViewModel.kt)
- FRED verilerini gösterge bazlı (Fed Rate, CPI, VIX vb.) UI state'e dönüştürme.
- Grafik çizimi için zaman serisi verisi hazırlama.

### [Component Name] UI Layer

#### [MODIFY] [NewsScreen.kt](file:///C:/Users/amcao/AndroidStudioProjects/Porsuk/app/src/main/java/com/nexus/porsuk/feature/news/NewsScreen.kt)
- Kategori sekmeleri (TabRow).
- Modern haber kartları (Görsel, Kaynak, AI Özet, Sentiment Tag).
- SearchBar ve Filter FAB.

#### [MODIFY] [MacroIntelligenceScreen.kt](file:///C:/Users/amcao/AndroidStudioProjects/Porsuk/app/src/main/java/com/nexus/porsuk/feature/macro/MacroIntelligenceScreen.kt)
- Gösterge kartları grid yapısı.
- Etkileşimli makro grafikler (LineChart).

#### [MODIFY] [CalendarScreen.kt](file:///C:/Users/amcao/AndroidStudioProjects/Porsuk/app/src/main/java/com/nexus/porsuk/feature/calendar/CalendarScreen.kt)
- Ekonomik takvim listesi (Beklenti, Önceki, Gerçekleşen).
- Ülke bazlı filtreleme ve "Takvime Ekle" butonu.

## Verification Plan

### Automated Tests
- NewsAPI ve FRED mapper testleri.
- AI sentiment analizi prompt doğrulama testleri.

### Manual Verification
- Haberlerin kategorilerine göre doğru filtrelendiği kontrol edilecek.
- Makro grafiklerin FRED verileriyle birebir örtüştüğü (FRED web sitesi ile) teyit edilecek.
- Yüksek etkili bir takvim olayı için bildirim oluşturma akışı test edilecek.
