package com.nexus.porsuk.feature.watchlist.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nexus.porsuk.domain.model.SmartCategory
import com.nexus.porsuk.domain.model.WatchlistGroup

/**
 * Porsuk Watchlist Pro — Çoklu Takip Listesi ScrollableTabRow
 */
@Composable
fun WatchlistGroupTabRow(
    groups: List<WatchlistGroup>,
    selectedGroup: WatchlistGroup?,
    onGroupSelected: (WatchlistGroup) -> Unit,
    onCreateGroupClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ScrollableTabRow(
        selectedTabIndex = groups.indexOf(selectedGroup).coerceAtLeast(0),
        edgePadding = 16.dp,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.primary,
        divider = {},
        modifier = modifier
    ) {
        groups.forEach { group ->
            val isSelected = group == selectedGroup
            Tab(
                selected = isSelected,
                onClick = { onGroupSelected(group) },
                text = {
                    Text(
                        text = if (group.isFavorite) "⭐ ${group.title}" else group.title,
                        style = if (isSelected) MaterialTheme.typography.titleSmall else MaterialTheme.typography.bodyMedium,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            )
        }

        // Yeni Liste Ekleme Butonu Tab'ı
        Tab(
            selected = false,
            onClick = onCreateGroupClick,
            text = {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Yeni Liste Ekle",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        )
    }
}

/**
 * 10 Akıllı Klasör (Smart Categories) FilterChip Barı
 */
@Composable
fun SmartCategoryChipBar(
    selectedCategory: SmartCategory?,
    onCategorySelected: (SmartCategory?) -> Unit,
    modifier: Modifier = Modifier
) {
    ScrollableTabRow(
        selectedTabIndex = if (selectedCategory == null) 0 else selectedCategory.ordinal + 1,
        edgePadding = 16.dp,
        containerColor = MaterialTheme.colorScheme.surface,
        divider = {},
        modifier = modifier
    ) {
        // Tümü Chip'i
        FilterChip(
            selected = selectedCategory == null,
            onClick = { onCategorySelected(null) },
            label = { Text("Tüm Varlıklar") },
            modifier = Modifier.padding(horizontal = 4.dp)
        )

        SmartCategory.entries.forEach { cat ->
            FilterChip(
                selected = cat == selectedCategory,
                onClick = { onCategorySelected(cat) },
                label = { Text(cat.title) },
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }
    }
}
