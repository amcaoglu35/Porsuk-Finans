package com.nexus.porsuk.core.common

/**
 * Porsuk Data Center — Ağ ve Veri Operasyonları Sonuç Sarmalayıcısı (Network/Data Result Wrapper)
 *
 * Tüm API, Veritabanı ve Senkronizasyon işlemlerinin çıktılarını tip emniyetli (Type-Safe)
 * olarak modelleyen generic sealed class.
 *
 * @param T Başarılı işlem sonucunda dönen veri tipi.
 */
sealed class NetworkResult<out T> {

    /**
     * İşlemin başarıyla tamamlandığını gösterir.
     * @property data Elde edilen sonuç verisi.
     */
    data class Success<out T>(val data: T) : NetworkResult<T>()

    /**
     * İşlem sırasında yönetilebilir bir veri/ağ hatası oluştuğunu gösterir.
     * @property error Detaylı domain hata modeli [DataError].
     * @property message Kullanıcıya gösterilebilir opsiyonel hata mesajı.
     */
    data class Error(val error: DataError, val message: String? = null) : NetworkResult<Nothing>()

    /**
     * Beklenmeyen bir kural dışı durum (Exception) oluştuğunu gösterir.
     * @property throwable Yakalanan istisna nesnesi.
     */
    data class Exception(val throwable: Throwable) : NetworkResult<Nothing>()

    /**
     * İşlemin devam ettiğini (yüklenme aşamasında olduğunu) gösterir.
     */
    object Loading : NetworkResult<Nothing>()
}
