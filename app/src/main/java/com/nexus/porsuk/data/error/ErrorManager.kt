package com.nexus.porsuk.data.error

import com.nexus.porsuk.core.common.DataError
import com.nexus.porsuk.core.common.NetworkResult
import com.nexus.porsuk.data.logging.DataLogger
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Porsuk Data Center — Merkezi Hata Yöneticisi (Central Error Manager)
 *
 * Veri ve ağ katmanında oluşan istisnaları yakalar, `DataError` modellerine dönüştürür,
 * kullanıcıya sunulacak Türkçe anlaşılır mesajlar üretir ve hataları kayıt altına alır (Logging).
 */
@Singleton
class ErrorManager @Inject constructor(
    private val dataLogger: DataLogger
) {

    /**
     * Yakalanan bir Throwable nesnesini uygun DataError tipine dönüştürür.
     */
    fun mapThrowableToDataError(throwable: Throwable): DataError {
        dataLogger.logError("ErrorManager", "Throwable haritalanıyor: ${throwable.localizedMessage}", throwable)
        return when (throwable) {
            is java.net.SocketTimeoutException -> DataError.Network.REQUEST_TIMEOUT
            is java.net.UnknownHostException -> DataError.Network.NO_INTERNET
            is retrofit2.HttpException -> when (throwable.code()) {
                401 -> DataError.Network.UNAUTHORIZED
                403 -> DataError.Network.FORBIDDEN
                404 -> DataError.Network.NOT_FOUND
                429 -> DataError.Network.RATE_LIMIT_EXCEEDED
                in 500..599 -> DataError.Network.SERVER_ERROR
                else -> DataError.Network.UNKNOWN_NETWORK_ERROR
            }
            is android.database.sqlite.SQLiteConstraintException -> DataError.Database.CONSTRAINT_VIOLATION
            is android.database.sqlite.SQLiteException -> DataError.Database.READ_FAILED
            else -> DataError.Network.UNKNOWN_NETWORK_ERROR
        }
    }

    /**
     * DataError tipini son kullanıcıya gösterilecek Türkçe dostça mesaja çevirir.
     */
    fun getReadableErrorMessage(error: DataError): String {
        return when (error) {
            is DataError.Network -> when (error) {
                DataError.Network.NO_INTERNET -> "İnternet bağlantısı kurulamadı. Lütfen ağ ayarlarınızı kontrol edin."
                DataError.Network.REQUEST_TIMEOUT -> "Sunucu yanıt vermedi, istek zaman aşımına uğradı."
                DataError.Network.RATE_LIMIT_EXCEEDED -> "Çok fazla istek gönderildi. Lütfen kısa süre sonra tekrar deneyin."
                DataError.Network.SERVER_ERROR -> "Sunucularımızda geçici bir aksaklık yaşanıyor."
                DataError.Network.UNAUTHORIZED -> "Oturum süreniz doldu. Lütfen tekrar giriş yapın."
                DataError.Network.FORBIDDEN -> "Bu veriye erişim yetkiniz bulunmuyor."
                DataError.Network.NOT_FOUND -> "Aradığınız finansal veri sunucuda bulunamadı."
                DataError.Network.UNKNOWN_NETWORK_ERROR -> "Bilinmeyen bir ağ hatası oluştu."
            }
            is DataError.Database -> when (error) {
                DataError.Database.DISK_FULL -> "Cihaz hafızası dolu olduğu için veri kaydedilemedi."
                DataError.Database.READ_FAILED -> "Yerel veriler okunurken bir sorun oluştu."
                DataError.Database.WRITE_FAILED -> "Veriler yerel hafızaya kaydedilemedi."
                DataError.Database.CONSTRAINT_VIOLATION -> "Çakışan veya mükerrer veri kaydı hatası."
                DataError.Database.DATA_CORRUPTED -> "Yerel veritabanı dosyası bozulmuş olabilir."
            }
            is DataError.Validation -> when (error) {
                DataError.Validation.MISSING_REQUIRED_FIELDS -> "Zorunlu finansal alanlar eksik."
                DataError.Validation.INVALID_PRICE_OR_VALUE -> "Geçersiz fiyat veya miktar değeri."
                DataError.Validation.DUPLICATE_ENTRY -> "Bu veri zaten mevcut."
                DataError.Validation.MALFORMED_IDENTIFIER -> "Geçersiz sembol veya sembol formatı."
            }
            is DataError.Cache -> when (error) {
                DataError.Cache.EXPIRED -> "Önbellekteki verinin süresi dolmuş."
                DataError.Cache.NOT_FOUND -> "Önbellekte veri bulunamadı."
                DataError.Cache.WRITE_FAILED -> "Önbelleğe yazma hatası."
            }
        }
    }
}
