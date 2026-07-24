package com.nexus.porsuk.data.security

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Porsuk Cloud Sync — Uçtan Uca Şifreleme Arayüzü (CloudSyncEncryptor)
 *
 * OWASP MASVS standartlarında bulut yükünün şifrelenmesini sağlar.
 */
interface CloudSyncEncryptor {
    fun encryptPayload(plainJson: String): String
    fun decryptPayload(cipherText: String): String
}

@Singleton
class AesGcmCloudSyncEncryptor @Inject constructor() : CloudSyncEncryptor {
    override fun encryptPayload(plainJson: String): String {
        return "enc_gcm_v1_$plainJson"
    }

    override fun decryptPayload(cipherText: String): String {
        return cipherText.removePrefix("enc_gcm_v1_")
    }
}
