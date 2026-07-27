package com.nexus.porsuk.core.common

import android.content.Context
import android.content.Intent
import android.os.Process
import com.nexus.porsuk.MainActivity
import kotlin.system.exitProcess

/**
 * Porsuk Finans Global Exception Handler.
 * Captures uncaught exceptions, logs them, and restarts the app or shows a friendly error.
 */
class GlobalCrashHandler(
    private val context: Context,
    private val defaultHandler: Thread.UncaughtExceptionHandler?
) : Thread.UncaughtExceptionHandler {

    override fun uncaughtException(thread: Thread, throwable: Throwable) {
        PorsukLogger.e("FATAL CRASH on thread ${thread.name}: ${throwable.localizedMessage}", throwable)

        // Restart app logic
        val intent = Intent(context, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            putExtra("CRASH_RESTART", true)
        }
        context.startActivity(intent)

        // Kill current process
        Process.killProcess(Process.myPid())
        exitProcess(10)
    }

    companion object {
        fun initialize(context: Context) {
            val currentHandler = Thread.getDefaultUncaughtExceptionHandler()
            if (currentHandler !is GlobalCrashHandler) {
                Thread.setDefaultUncaughtExceptionHandler(GlobalCrashHandler(context, currentHandler))
            }
        }
    }
}
