package com.nexus.porsuk.feature.calendar.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.nexus.porsuk.domain.model.CalendarImpactLevel
import com.nexus.porsuk.domain.model.CalendarViewMode

/**
 * Porsuk Economic Calendar Engine — Görünüm Modu Geçiş Barı (Günlük, Haftalık, Aylık, Liste)
 */
@Composable
fun CalendarViewToggleBar(
    selectedMode: CalendarViewMode,
    onModeSelected: (CalendarViewMode) -> Unit,
    modifier: Modifier = Modifier
) {
    SingleChoiceSegmentedButtonRow(
        modifier = modifier.padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        CalendarViewMode.entries.forEachIndexed { index, mode ->
            SegmentedButton(
                selected = mode == selectedMode,
                onClick = { onModeSelected(mode) },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = CalendarViewMode.entries.size)
            ) {
                Text(text = mode.displayName, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

/**
 * Etki Seviyesi Görsel Rozeti (ImpactLevelBadge)
 */
@Composable
fun ImpactLevelBadge(
    impactLevel: CalendarImpactLevel,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = androidx.compose.foundation.shape.RoundedCornerShape(6.dp),
        color = Color(impactLevel.colorHex).copy(alpha = 0.15f),
        modifier = modifier
    ) {
        Text(
            text = impactLevel.displayName,
            color = Color(impactLevel.colorHex),
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}
