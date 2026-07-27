package com.nexus.porsuk.feature.companydetail.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nexus.porsuk.feature.companydetail.CompanyDetailTab

/**
 * Porsuk Company Detail Module — 5 Sekmeli Material 3 ScrollableTabRow (Redesigned)
 */
@Composable
fun CompanyTabRow(
    selectedTab: CompanyDetailTab,
    onTabSelected: (CompanyDetailTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val mainGreen = Color(0xFF14B88A)

    ScrollableTabRow(
        selectedTabIndex = selectedTab.ordinal,
        edgePadding = 16.dp,
        containerColor = Color.Transparent,
        contentColor = mainGreen,
        divider = {},
        indicator = { tabPositions ->
            if (selectedTab.ordinal < tabPositions.size) {
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab.ordinal]),
                    color = mainGreen
                )
            }
        },
        modifier = modifier
    ) {
        CompanyDetailTab.entries.forEach { tab ->
            val isSelected = tab == selectedTab
            Tab(
                selected = isSelected,
                onClick = { onTabSelected(tab) },
                text = {
                    Text(
                        text = tab.title,
                        style = if (isSelected) MaterialTheme.typography.titleSmall else MaterialTheme.typography.bodyMedium,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) mainGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            )
        }
    }
}
