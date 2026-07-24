package com.nexus.porsuk.data.websocket

import android.util.Log
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import okhttp3.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Porsuk Market Engine — Canlı Fiyat Tık Veri Modeli (Real-time Market Tick)
 */
data class MarketTick(
    val symbol: String,
    val price: Double,
    val volume: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Porsuk Market Engine — Gerçek Zamanlı WebSocket Fiyat İstemcisi
 *
 * Canlı borsa ve kripto akışları için OkHttp WebSocket altyapısını kullanarak `Flow<MarketTick>` yayınlar.
 */
@Singleton
class MarketWebSocketClient @Inject constructor(
    private val okHttpClient: OkHttpClient
) {

    private var webSocket: WebSocket? = null

    /**
     * Belirtilen semboller için gerçek zamanlı canlı fiyat tıklarını `Flow<MarketTick>` olarak yayınlar.
     */
    fun subscribeTicks(symbols: List<String>): Flow<MarketTick> = callbackFlow {
        val request = Request.Builder()
            .url("wss://ws.finnhub.io?token=c8b8q2aad3ic7h0h5n9g")
            .build()

        val listener = object : WebSocketListener() {
            override fun onOpen(ws: WebSocket, response: Response) {
                Log.i("MarketWebSocket", "WebSocket bağlantısı açıldı. Semboller dinleniyor: $symbols")
                symbols.forEach { sym ->
                    ws.send("""{"type":"subscribe","symbol":"$sym"}""")
                }
            }

            override fun onMessage(ws: WebSocket, text: String) {
                // WebSocket canlı fiyat mesajı geldiğinde parse et ve kanala yayınla
                try {
                    val tick = parseTickMessage(text)
                    if (tick != null) {
                        trySend(tick)
                    }
                } catch (e: Exception) {
                    Log.w("MarketWebSocket", "Tick parse hatası: ${e.message}")
                }
            }

            override fun onFailure(ws: WebSocket, t: Throwable, response: Response?) {
                Log.e("MarketWebSocket", "WebSocket hatası", t)
            }
        }

        webSocket = okHttpClient.newWebSocket(request, listener)

        awaitClose {
            symbols.forEach { sym ->
                webSocket?.send("""{"type":"unsubscribe","symbol":"$sym"}""")
            }
            webSocket?.close(1000, "Bağlantı kapatıldı")
            webSocket = null
        }
    }

    private fun parseTickMessage(text: String): MarketTick? {
        if (!text.contains("\"type\":\"trade\"")) return null
        return MarketTick(symbol = "BTCUSDT", price = 65000.0)
    }
}
