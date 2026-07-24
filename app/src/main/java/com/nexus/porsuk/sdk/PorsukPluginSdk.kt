package com.nexus.porsuk.sdk

import com.nexus.porsuk.domain.model.*

/**
 * Porsuk Plugin SDK — Ana Eklenti Temel Sözleşmesi (PorsukPlugin)
 */
interface PorsukPlugin {
    val manifest: PluginManifest
    fun onInitialize()
    fun onEnable()
    fun onDisable()
    fun onDestroy()
}

/**
 * Piyasa Veri Sağlayıcı Eklenti Sözleşmesi (MarketDataProviderPlugin)
 */
interface MarketDataProviderPlugin : PorsukPlugin {
    fun fetchRealtimePrice(symbol: String): Double
}

/**
 * Özel Gösterge Eklenti Sözleşmesi (IndicatorPlugin)
 */
interface IndicatorPlugin : PorsukPlugin {
    fun calculateIndicator(priceData: List<Double>): List<Double>
}

/**
 * Strateji Eklenti Sözleşmesi (StrategyPlugin)
 */
interface StrategyPlugin : PorsukPlugin {
    fun evaluateSignal(priceData: List<Double>): String // "BUY", "SELL", "HOLD"
}

/**
 * AI Sağlayıcı Eklenti Sözleşmesi (AiProviderPlugin)
 */
interface AiProviderPlugin : PorsukPlugin {
    fun generateFinancialInsight(prompt: String): String
}

/**
 * SDK Plugin Kayıt Defteri Fabrikası (PluginRegistry)
 */
object PluginRegistry {
    private val registeredPlugins = mutableMapOf<String, PorsukPlugin>()

    fun registerPlugin(plugin: PorsukPlugin) {
        registeredPlugins[plugin.manifest.pluginId] = plugin
        plugin.onInitialize()
    }

    fun unregisterPlugin(pluginId: String) {
        registeredPlugins[pluginId]?.onDestroy()
        registeredPlugins.remove(pluginId)
    }

    fun getRegisteredPlugins(): List<PorsukPlugin> = registeredPlugins.values.toList()
}
