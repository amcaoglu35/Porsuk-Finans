package com.nexus.porsuk.feature.markets.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nexus.porsuk.feature.markets.MarketSortOption

/**
 * Porsuk Markets Module — Material 3 Sıralama Seçenekleri BottomSheet Bileşeni
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketFilterSortSheet(
    selectedOption: MarketSortOption,
    onOptionSelected: (MarketSortOption) -> Unit,
    onDismissRequest: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Text(
                text = "Piyasa Sıralama Seçenekleri",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            MarketSortOption.entries.forEach { option ->
                val isSelected = option == selectedOption
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onOptionSelected(option)
                            onDismissRequest()
                        }
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = option.displayName,
                        style = if (isSelected) MaterialTheme.typography.bodyLarge else MaterialTheme.typography.bodyMedium,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Seçili",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
