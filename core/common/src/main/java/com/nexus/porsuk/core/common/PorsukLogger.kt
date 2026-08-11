package com.nexus.porsuk.core.common

import android.util.Log

/**
 * Porsuk Finans Unified Logging System.
 * Standardizes log levels and prepares hooks for production crash/analytic reporting.
 */
object PorsukLogger {

    private const val DEFAULT_TAG = "PORSUK_FINANS"

    fun d(message: String, tag: String = DEFAULT_TAG) {
        Log.d(tag, "🔍 DEBUG: $message")
    }

    fun i(message: String, tag: String = DEFAULT_TAG) {
        Log.i(tag, "💡 INFO: $message")
    }

    fun w(message: String, tag: String = DEFAULT_TAG) {
        Log.w(tag, "⚠️ WARNING: $message")
    }

    fun e(message: String, throwable: Throwable? = null, tag: String = DEFAULT_TAG) {
        Log.e(tag, "❌ ERROR: $message", throwable)
        // Hook for Crashlytics or Sentry can be added here
    }
}
