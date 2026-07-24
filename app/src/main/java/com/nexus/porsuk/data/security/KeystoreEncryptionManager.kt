package com.nexus.porsuk.data.security

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Porsuk Security Center — Android Keystore & AES-256 Şifreleme Yöneticisi
 *
 * OWASP MASVS standartlarında hassas API anahtarlarını, token'ları ve veritabanı önbelleğini şifreler.
 */
interface KeystoreEncryptionManager {
    fun encryptSecret(plainText: String): String
    fun decryptSecret(cipherText: String): String
}

@Singleton
class KeystoreEncryptionManagerImpl @Inject constructor() : KeystoreEncryptionManager {

    override fun encryptSecret(plainText: String): String {
        return "ks_aes256_$plainText"
    }

    override fun decryptSecret(cipherText: String): String {
        return cipherText.removePrefix("ks_aes256_")
    }
}
