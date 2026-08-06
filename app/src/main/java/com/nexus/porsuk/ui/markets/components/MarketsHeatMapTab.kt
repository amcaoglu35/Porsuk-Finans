package com.nexus.porsuk.ui.markets.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nexus.porsuk.ui.common.HeatmapItem
import com.nexus.porsuk.ui.common.PortfolioHeatmap

@Composable
fun HeatMapTab() {
    val sampleMarketItems = listOf(
        HeatmapItem("THYAO", 285.0, 2.4, "BIST"),
        HeatmapItem("GARAN", 112.5, -1.2, "BIST"),
        HeatmapItem("EREGL", 48.2, 0.8, "BIST"),
        HeatmapItem("KCHOL", 220.0, 3.1, "BIST"),
        HeatmapItem("TUPRS", 168.0, -0.5, "BIST"),
        HeatmapItem("AKBNK", 56.4, 1.9, "BIST")
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item(key = "full_heatmap_section") {
            PortfolioHeatmap(items = sampleMarketItems)
        }
    }
}

