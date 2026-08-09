package com.nexus.porsuk.core.common

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Porsuk Data Center — Ağ Bağlantı İzleyicisi (Network Connectivity Monitor)
 */
@Singleton
class NetworkConnectivityMonitor @Inject constructor(
    @ApplicationContext private val context: Context,
    private val eventBus: PorsukEventBus
) {
    private val scope = CoroutineScope(Dispatchers.IO)
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    /**
     * İnternet erişim durumunu reaktif bir Flow olarak döndürür.
     */
    val isConnected: Flow<Boolean> = callbackFlow {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                trySend(true)
                scope.launch {
                    eventBus.publish(PorsukEvent.InternetConnectionRestored)
                }
            }

            override fun onLost(network: Network) {
                trySend(false)
                scope.launch {
                    eventBus.publish(PorsukEvent.InternetConnectionLost)
                }
            }

            override fun onUnavailable() {
                trySend(false)
            }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(request, callback)

        // Başlangıç durumunu anında kontrol et ve yay
        trySend(checkCurrentConnection())

        awaitClose {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }

    /**
     * Anlık senkron kontrol fonksiyonu
     */
    fun isNetworkAvailable(): Boolean {
        return checkCurrentConnection()
    }

    private fun checkCurrentConnection(): Boolean {
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
