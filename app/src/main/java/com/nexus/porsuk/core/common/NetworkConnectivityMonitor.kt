package com.nexus.porsuk.core.common

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Porsuk Data Center — Ağ Bağlantı İzleyicisi (Network Connectivity Monitor)
 *
 * Cihazın anlık internet bağlantısını (Wi-Fi, Hücresel vb.) izler ve
 * reaktif bir `Flow<Boolean>` yayınlar.
 */
@Singleton
class NetworkConnectivityMonitor @Inject constructor(
    private val context: Context
) {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    /**
     * İnternet erişim durumunu reaktif bir Flow olarak döndürür.
     * True: Cihaz internete bağlı.
     * False: İnternet bağlantısı yok.
     */
    val isConnected: Flow<Boolean> = callbackFlow {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                trySend(true)
            }

            override fun onLost(network: Network) {
                trySend(false)
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
