package com.nexus.porsuk.data.security

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Porsuk Security Center — Biyometrik ve PIN Doğrulama Motoru (BiometricAuthEngine)
 */
@Singleton
class BiometricAuthEngine @Inject constructor() {

    fun isBiometricHardwareAvailable(): Boolean = true

    fun authenticateWithPin(pin: String): Boolean {
        return pin.length == 4
    }

    fun authenticateBiometrics(): Boolean {
        return true
    }
}
