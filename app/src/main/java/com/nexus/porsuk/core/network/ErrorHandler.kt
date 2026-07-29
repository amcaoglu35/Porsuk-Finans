package com.nexus.porsuk.core.network

import com.nexus.porsuk.core.common.DataError
import com.nexus.porsuk.core.common.NetworkResult
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ErrorHandler @Inject constructor() {

    fun handleError(throwable: Throwable): NetworkResult.Error {
        return when (throwable) {
            is IOException -> {
                val errorType = if (throwable is SocketTimeoutException) {
                    DataError.Network.REQUEST_TIMEOUT
                } else {
                    DataError.Network.NO_INTERNET
                }
                NetworkResult.Error(
                    error = errorType,
                    message = if (errorType == DataError.Network.REQUEST_TIMEOUT) "İstek zaman aşımına uğradı." else "İnternet bağlantısı yok."
                )
            }
            is HttpException -> {
                val (errorType, message) = when (throwable.code()) {
                    401 -> DataError.Network.UNAUTHORIZED to "Yetkisiz erişim (401). Geçersiz API anahtarı veya oturum sonlandı."
                    403 -> DataError.Network.FORBIDDEN to "Erişim engellendi (403). Bu servise erişim izniniz bulunmuyor."
                    404 -> DataError.Network.NOT_FOUND to "Kaynak bulunamadı (404). Aradığınız veri sunucuda yer almıyor."
                    429 -> DataError.Network.RATE_LIMIT_EXCEEDED to "Çok fazla istek gönderildi (429). Lütfen bir süre sonra tekrar deneyin."
                    500 -> DataError.Network.SERVER_ERROR to "Sunucu içi hata (500). Servis geçici olarak kullanılamıyor."
                    502 -> DataError.Network.SERVER_ERROR to "Kötü Ağ Geçidi (502). Sunucu yanıt veremiyor."
                    503 -> DataError.Network.SERVER_ERROR to "Hizmet Kullanılamıyor (503). Sunucu aşırı yüklü veya bakımda."
                    in 500..599 -> DataError.Network.SERVER_ERROR to "Sunucu hatası (${throwable.code()})."
                    else -> DataError.Network.UNKNOWN_NETWORK_ERROR to "Bilinmeyen ağ hatası (${throwable.code()})."
                }
                NetworkResult.Error(
                    error = errorType,
                    message = message
                )
            }
            else -> {
                NetworkResult.Error(
                    error = DataError.Network.UNKNOWN_NETWORK_ERROR,
                    message = throwable.localizedMessage ?: "Bilinmeyen bir hata oluştu."
                )
            }
        }
    }
}
