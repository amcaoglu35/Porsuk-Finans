package com.nexus.porsuk.core.common

import android.content.Context
import android.content.Intent
import android.os.Process
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

        // Restart app logic - removed direct dependency on MainActivity
        val launchIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        launchIntent?.let {
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            it.putExtra("CRASH_RESTART", true)
            context.startActivity(it)
        }

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
