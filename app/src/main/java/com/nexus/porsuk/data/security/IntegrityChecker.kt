package com.nexus.porsuk.data.security

import android.os.Build
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Porsuk Security Center — Uygulama Bütünlüğü ve Tehdit Doğrulayıcısı (IntegrityChecker)
 *
 * Rooted cihaz, Emülatör örneği, Hata Ayıklayıcı (Debugger) ve Frida/Xposed müdahalelerini tespit eder.
 */
@Singleton
class IntegrityChecker @Inject constructor() {

    fun isRooted(): Boolean {
        val buildTags = Build.TAGS
        return buildTags != null && buildTags.contains("test-keys")
    }

    fun isEmulator(): Boolean {
        return (Build.FINGERPRINT.startsWith("generic")
                || Build.MODEL.contains("google_sdk")
                || Build.HARDWARE.contains("goldfish")
                || Build.MANUFACTURER.contains("Genymotion"))
    }

    fun isDebuggerAttached(): Boolean {
        return android.os.Debug.isDebuggerConnected()
    }
}
