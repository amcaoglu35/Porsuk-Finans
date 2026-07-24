package com.nexus.porsuk.feature.strategybuilder.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nexus.porsuk.domain.model.*

/**
 * 12 Strateji Türü Chip Barı (StrategyTypeChips)
 */
@Composable
fun StrategyTypeChips(
    selectedType: StrategyType,
    onTypeSelected: (StrategyType) -> Unit,
    modifier: Modifier = Modifier
) {
    ScrollableTabRow(
        selectedTabIndex = selectedType.ordinal,
        edgePadding = 16.dp,
        containerColor = MaterialTheme.colorScheme.surface,
        divider = {},
        modifier = modifier.padding(vertical = 4.dp)
    ) {
        StrategyType.entries.forEach { type ->
            FilterChip(
                selected = type == selectedType,
                onClick = { onTypeSelected(type) },
                label = { Text("${type.iconEmoji} ${type.displayName}") },
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }
    }
}

/**
 * Hazır Strateji Şablon Kartı (StrategyTemplateCard)
 */
@Composable
fun StrategyTemplateCard(
    template: StrategyModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = "${template.type.iconEmoji} ${template.name}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = template.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Kural Doğrulama Kartı (StrategyValidationBadgeCard)
 */
@Composable
fun StrategyValidationBadgeCard(
    validationResult: StrategyValidationResult,
    modifier: Modifier = Modifier
) {
    val isValid = validationResult.isValid
    val cardColor = if (isValid) Color(0xFF00C853).copy(alpha = 0.15f) else Color(0xFFFF6D00).copy(alpha = 0.15f)
    val textColor = if (isValid) Color(0xFF00C853) else Color(0xFFFF6D00)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = if (isValid) "✅ Strateji Doğrulandı" else "⚠️ Doğrulama Uyarısı",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = validationResult.summaryMessage,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
