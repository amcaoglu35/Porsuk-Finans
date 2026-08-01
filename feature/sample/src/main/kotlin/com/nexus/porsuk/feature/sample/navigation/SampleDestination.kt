package com.nexus.porsuk.feature.sample.navigation

import androidx.compose.runtime.Immutable
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Immutable
sealed interface SampleDestination : NavKey {
    @Serializable
    data object StockList : SampleDestination

    @Serializable
    data object StockAnalysis : SampleDestination

    @Serializable
    data class StockDetail(val symbol: String) : SampleDestination
}
