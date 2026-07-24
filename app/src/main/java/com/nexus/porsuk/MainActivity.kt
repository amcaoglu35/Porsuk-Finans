package com.nexus.porsuk

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.work.*
import com.nexus.porsuk.ui.navigation.PorsukApp
import com.nexus.porsuk.worker.PorsukUpdateWorker
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setupWorkManager()
        setContent {
            PorsukApp()
        }
    }

    private fun setupWorkManager() {
        try {
            val workRequest = PeriodicWorkRequestBuilder<PorsukUpdateWorker>(15, TimeUnit.MINUTES)
                .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
                .build()
            WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "PorsukBackgroundSync",
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
            com.nexus.porsuk.worker.PriceAlertWorker.schedule(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
