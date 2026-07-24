package com.nexus.porsuk.feature.automation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nexus.porsuk.domain.model.AutomationRuleModel
import com.nexus.porsuk.domain.model.AutomationWorkflow
import com.nexus.porsuk.domain.model.NotificationCenterItem

/**
 * Bildirim Kartı (NotificationItemCard)
 */
@Composable
fun NotificationItemCard(
    item: NotificationCenterItem,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${item.category.iconEmoji} ${item.title}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(item.priority.colorHex).copy(alpha = 0.2f)
                ) {
                    Text(
                        text = item.priority.displayName,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(item.priority.colorHex),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = item.message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Otomasyon Kural Kartı (AutomationRuleRowCard)
 */
@Composable
fun AutomationRuleRowCard(
    rule: AutomationRuleModel,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = "⚡ ${rule.title}",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "EĞER: ${rule.ifConditionText}",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "AKSİYON: ${rule.actionText}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Otomasyon İş Akışı Kartı (WorkflowTemplateCard)
 */
@Composable
fun WorkflowTemplateCard(
    workflow: AutomationWorkflow,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(workflow.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text("Zamanlama: ${workflow.cronScheduleText}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Badge(containerColor = Color(0xFF00C853)) {
                Text(workflow.lastRunStatusText, modifier = Modifier.padding(4.dp))
            }
        }
    }
}
