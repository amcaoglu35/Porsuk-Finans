package com.nexus.porsuk.feature.chart.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nexus.porsuk.domain.model.ChartTimeFrame
import com.nexus.porsuk.domain.model.DrawingToolType

/**
 * 10 Zaman Dilimi Çubuğu (ChartTimeFrameBar)
 */
@Composable
fun ChartTimeFrameBar(
    selectedTimeFrame: ChartTimeFrame,
    onTimeFrameSelected: (ChartTimeFrame) -> Unit,
    modifier: Modifier = Modifier
) {
    ScrollableTabRow(
        selectedTabIndex = selectedTimeFrame.ordinal,
        edgePadding = 16.dp,
        containerColor = MaterialTheme.colorScheme.surface,
        divider = {},
        modifier = modifier.padding(vertical = 2.dp)
    ) {
        ChartTimeFrame.entries.forEach { tf ->
            val isSelected = tf == selectedTimeFrame
            Tab(
                selected = isSelected,
                onClick = { onTimeFrameSelected(tf) },
                text = {
                    Text(
                        text = tf.displayName,
                        style = if (isSelected) MaterialTheme.typography.titleSmall else MaterialTheme.typography.bodySmall,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            )
        }
    }
}

/**
 * Çizim Araçları Çubuğu (ChartToolboxBar)
 */
@Composable
fun ChartToolboxBar(
    selectedTool: DrawingToolType,
    onToolSelected: (DrawingToolType) -> Unit,
    modifier: Modifier = Modifier
) {
    ScrollableTabRow(
        selectedTabIndex = selectedTool.ordinal,
        edgePadding = 16.dp,
        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        divider = {},
        modifier = modifier.padding(vertical = 4.dp)
    ) {
        DrawingToolType.entries.forEach { tool ->
            FilterChip(
                selected = tool == selectedTool,
                onClick = { onToolSelected(tool) },
                label = { Text("${tool.iconEmoji} ${tool.displayName}") },
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }
    }
}
