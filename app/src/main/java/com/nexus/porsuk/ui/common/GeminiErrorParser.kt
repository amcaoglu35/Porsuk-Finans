package com.nexus.porsuk.ui.common

import java.net.ConnectException
import java.net.UnknownHostException
import java.net.SocketTimeoutException

object GeminiErrorParser {

    /**
     * Gemini API'den dönen karmaşık veya teknik hata mesajlarını kullanıcı dostu ve çözüm odaklı Türkçe açıklamalara dönüştürür.
     */
    fun parse(e: Throwable): String {
        val message = e.message ?: ""
        val localizedMessage = e.localizedMessage ?: ""
        val fullErrorText = "$message $localizedMessage"

        // 1. İnternet / Bağlantı Sorunları
        if (e is UnknownHostException || e is ConnectException || fullErrorText.contains("Unable to resolve host")) {
            return "İnternet Bağlantısı Hatası:\n\nİnternet bağlantınızı kontrol edin. Google Generative AI servislerine erişilemiyor. Lütfen ağınızı kontrol edip tekrar deneyin."
        }
        if (e is SocketTimeoutException || fullErrorText.contains("timeout", ignoreCase = true)) {
            return "Bağlantı Zaman Aşımı:\n\nSunucu isteğe zamanında yanıt vermedi. Lütfen internet bağlantınızı kontrol edip tekrar deneyin."
        }

        // 2. 401 Yetkisiz Erişim (Auth Error)
        if (fullErrorText.contains("401") || fullErrorText.contains("UNAUTHORIZED", ignoreCase = true)) {
            return "Gemini API Yetkilendirme Hatası (401):\n\n" +
                   "API anahtarınız doğrulanamadı. Lütfen anahtarın doğru kopyalandığından ve Google AI Studio'da aktif olduğundan emin olun."
        }

        // 3. 404 Model Bulunamadı / API Etkin Değil Sorunu (En sık karşılaşılan hata)
        if (fullErrorText.contains("is not found for API version", ignoreCase = true) || 
            fullErrorText.contains("ModelService.ListModels", ignoreCase = true) ||
            fullErrorText.contains("404") || 
            fullErrorText.contains("NOT_FOUND", ignoreCase = true)) {
            val sysDetail = extractJsonErrorMessage(fullErrorText) ?: e.message ?: ""
            return "Gemini API Servis Hatası (404):\n\n" +
                   "Yapay zeka modeli (gemini-2.0-flash vb.) veya API servisi projenizde aktif değil.\n\n" +
                   "💡 KESİN ÇÖZÜM:\n" +
                   "1. Google AI Studio (https://aistudio.google.com/) adresine gidin.\n" +
                   "2. 'Get API Key' butonuna basın ve aktif bir API anahtarı seçin veya yeni bir tane oluşturun.\n" +
                   "3. API anahtarının bağlı olduğu projenin 'Generative Language API' servisine erişimi olduğundan emin olun.\n" +
                   "4. Anahtarı kopyalayıp Porsuk 'Ayarlar' sayfasından tekrar kaydedin.\n\n" +
                   "[Sistem Detayı: $sysDetail]"
        }

        // 3. 429 Kota Sınırı / Rate Limit
        if (fullErrorText.contains("quota", ignoreCase = true) || 
            fullErrorText.contains("RESOURCE_EXHAUSTED", ignoreCase = true) ||
            fullErrorText.contains("429") || 
            fullErrorText.contains("rate limit", ignoreCase = true)) {
            return "İstek Sınırı Aşıldı (429):\n\n" +
                   "Google Gemini API dakikalık/günlük ücretsiz kota sınırına (RPM/RPD) ulaşıldı.\n\n" +
                   "💡 KESİN ÇÖZÜM:\n" +
                   "1. Uygulama otomatik olarak alternatif 'gemini-2.5-flash', 'gemini-2.0-flash' ve 'gemini-1.5-flash' modellerine geçiş yapmayı dener.\n" +
                   "2. 15-30 saniye bekleyip tekrar deneyin.\n" +
                   "3. Dilerseniz Google AI Studio'dan (https://aistudio.google.com/) yeni bir API anahtarı alıp Ayarlar'dan güncelleyebilirsiniz."
        }

        // 4. 403 API Anahtarı Geçersiz / Yetkisiz
        if (fullErrorText.contains("API key not valid", ignoreCase = true) || 
            fullErrorText.contains("API_KEY_INVALID", ignoreCase = true) || 
            fullErrorText.contains("invalid key", ignoreCase = true) || 
            fullErrorText.contains("403") || 
            fullErrorText.contains("PERMISSION_DENIED", ignoreCase = true)) {
            return "Geçersiz API Anahtarı (403):\n\n" +
                   "Girdiğiniz Gemini API anahtarı geçersiz veya kısıtlanmış. Lütfen Ayarlar sayfasından doğru ve aktif bir API anahtarı girdiğinizden emin olun."
        }

        // 5. Bloklanmış İçerik / Güvenlik Filtresi (Safety Block)
        if (fullErrorText.contains("SAFETY", ignoreCase = true) || 
            fullErrorText.contains("blocked", ignoreCase = true) || 
            fullErrorText.contains("candidate", ignoreCase = true)) {
            return "Yapay Zeka Güvenlik Uyarısı:\n\n" +
                   "Üretilen içerik veya gönderilen analiz talebi Google'ın güvenlik politikalarına takıldı. Lütfen daha standart finansal terimlerle tekrar deneyin."
        }

        // 6. kotlinx.serialization MissingFieldException (Details alanı eksikliği hatası)
        if (fullErrorText.contains("MissingFieldException", ignoreCase = true) || 
            fullErrorText.contains("GRpcError", ignoreCase = true)) {
            // Eğer hata gövdesinde yine de bir 404 mesajı varsa yukarıdaki açıklamayı gösterelim
            if (fullErrorText.contains("gemini-", ignoreCase = true)) {
                val sysDetail = extractJsonErrorMessage(fullErrorText) ?: e.message ?: ""
                return "Gemini API Yetkilendirme Hatası (404):\n\n" +
                       "API anahtarınızın bağlı olduğu Google Cloud projesinde 'Generative Language API' (Gemini API) etkinleştirilmemiş veya model erişimi engellenmiş.\n\n" +
                       "💡 KESİN ÇÖZÜM:\n" +
                       "1. Google AI Studio (https://aistudio.google.com/) adresine gidin.\n" +
                       "2. Ücretsiz yeni bir API anahtarı (Get API Key) oluşturun.\n" +
                       "3. Aldığınız anahtarı Porsuk uygulamasının 'Ayarlar' sayfasından kaydedin.\n\n" +
                       "[Sistem Detayı: $sysDetail]"
            }
            return "Gemini Sunucu Yanıtı Hatası:\n\n" +
                   "Gemini API sunucusu eksik veya hatalı biçimlendirilmiş bir hata yanıtı döndürdü. Lütfen API anahtarınızın doğruluğunu ve internet bağlantınızı kontrol edin."
        }

        val finalSysDetail = extractJsonErrorMessage(fullErrorText) ?: e.localizedMessage ?: "Bilinmeyen bir hata oluştu."
        // Genel Hata Mesajı
        return "Yapay Zeka Servis Hatası:\n\n$finalSysDetail"
    }

    private fun extractJsonErrorMessage(fullText: String): String? {
        try {
            val msgRegex = Regex("\"message\"\\s*:\\s*\"([^\"]+)\"")
            val statusRegex = Regex("\"status\"\\s*:\\s*\"([^\"]+)\"")
            
            val msgMatch = msgRegex.find(fullText)
            val statusMatch = statusRegex.find(fullText)
            
            if (msgMatch != null) {
                val extracted = msgMatch.groupValues[1]
                val status = statusMatch?.groupValues?.get(1) ?: "ERROR"
                return "$extracted ($status)"
            }
        } catch (_: Exception) {}
        return null
    }
}
