# APK Alımı Sırasındaki Hafıza Hatasının Giderilmesi

APK oluşturma (build) işlemi sırasında "java.lang.OutOfMemoryError: Java heap space" hatası alındığı tespit edildi. Bu durum, projenin büyüklüğü ve eklenen yeni özellikler nedeniyle Kotlin derleyicisinin (compiler) mevcut hafıza limitlerini aşmasından kaynaklanmaktadır.

## Önerilen Değişiklikler

### [Bileşen: Gradle Yapılandırması]

#### [MODIFY] [gradle.properties](file:///C:/Users/amcao/AndroidStudioProjects/Porsuk/gradle.properties)
- `org.gradle.jvmargs` değeri `2048m`den `4096m`ye yükseltilecek.
- `kotlin.daemon.jvmargs=-Xmx2048m` ayarı eklenerek Kotlin derleme sürecine özel hafıza alanı ayrılacak.
- `org.gradle.parallel=true` aktif edilerek derleme hızı optimize edilecek.

## Doğrulama Planı

### Otomatik Testler
- `gradlew assembleDebug` komutu çalıştırılarak APK paketleme işleminin başarıyla tamamlandığı doğrulanacak.

### Manuel Doğrulama
- Hafıza ayarları güncellendikten sonra build işleminin takılmadan bittiği gözlemlenecek.
