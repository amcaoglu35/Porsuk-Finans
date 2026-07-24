package com.nexus.porsuk.feature.markets.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.nexus.porsuk.feature.markets.MarketTab

/**
 * Porsuk Markets Module — 10 Piyasa Sekmeli Material 3 ScrollableTabRow
 */
@Composable
fun MarketTabRow(
    selectedTab: MarketTab,
    onTabSelected: (MarketTab) -> Unit,
    modifier: Modifier = Modifier
) {
    ScrollableTabRow(
        selectedTabIndex = selectedTab.ordinal,
        edgePadding = 16.dp,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.primary,
        divider = {},
        modifier = modifier
    ) {
        MarketTab.entries.forEach { tab ->
            val isSelected = tab == selectedTab
            Tab(
                selected = isSelected,
                onClick = { onTabSelected(tab) },
                text = {
                    Text(
                        text = tab.title,
                        style = if (isSelected) MaterialTheme.typography.titleSmall else MaterialTheme.typography.bodyMedium,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            )
        }
    }
}
