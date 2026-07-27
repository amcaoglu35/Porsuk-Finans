package com.nexus.porsuk.feature.automation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.domain.model.*
import com.nexus.porsuk.ui.theme.*

@Composable
fun AutomationRuleCard(
    rule: AutomationRuleModel,
    onToggle: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardNew),
        border = androidx.compose.foundation.BorderStroke(1.dp, LineBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(rule.category.iconEmoji, fontSize = 20.sp)
                    Text(
                        text = rule.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = InkText,
                        fontFamily = Manrope
                    )
                }
                Switch(
                    checked = rule.isEnabled,
                    onCheckedChange = onToggle,
                    colors = SwitchDefaults.colors(checkedTrackColor = PrimaryTeal)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            ConditionActionBox(rule.ifConditionText, rule.actionText)
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, null, tint = NegatifRed)
                }
            }
        }
    }
}

@Composable
fun ConditionActionBox(condition: String, action: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(BackgroundNew)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(PrimaryTeal.copy(alpha = 0.1f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text("IF", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = PrimaryTeal, fontFamily = JetBrainsMono)
            }
            Text(condition, style = MaterialTheme.typography.bodySmall, color = InkText, fontFamily = Manrope)
        }
        
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Violet.copy(alpha = 0.1f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text("THEN", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Violet, fontFamily = JetBrainsMono)
            }
            Text(action, style = MaterialTheme.typography.bodySmall, color = InkText, fontFamily = Manrope)
        }
    }
}

@Composable
fun AutomationHistoryCard(entry: AutomationHistoryModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardNew),
        border = androidx.compose.foundation.BorderStroke(1.dp, LineBorder)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val statusColor = when (entry.status) {
                "SUCCESS" -> PrimaryTeal
                "FAILED" -> NegatifRed
                else -> SubText
            }
            
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(statusColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (entry.status) {
                        "SUCCESS" -> Icons.Default.CheckCircle
                        "FAILED" -> Icons.Default.Error
                        else -> Icons.Default.Schedule
                    },
                    contentDescription = null,
                    tint = statusColor
                )
            }
            
            Column(modifier = Modifier.weight(1f)) {
                Text(entry.resultSummary, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = InkText, fontFamily = Manrope)
                Text(
                    java.text.SimpleDateFormat("dd MMM, HH:mm", java.util.Locale.getDefault()).format(java.util.Date(entry.executionTime)),
                    style = MaterialTheme.typography.labelSmall,
                    color = SubText,
                    fontFamily = IBMPlexMono
                )
            }
            
            Text("${entry.durationMs}ms", style = MaterialTheme.typography.labelSmall, color = SubText, fontFamily = IBMPlexMono)
        }
    }
}

@Composable
fun AiSuggestionCard(suggestion: AiAutomationSuggestionModel, onApply: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardNew),
        border = androidx.compose.foundation.BorderStroke(1.dp, LineBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(suggestion.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = PrimaryTeal, fontFamily = Manrope)
            Spacer(modifier = Modifier.height(4.dp))
            Text(suggestion.description, style = MaterialTheme.typography.bodySmall, color = InkText, fontFamily = Manrope)
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onApply,
                modifier = Modifier.fillMaxWidth().height(40.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal)
            ) {
                Text("Öneriyi Uygula", fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = Manrope)
            }
        }
    }
}

@Composable
fun TemplateCard(template: AutomationRuleModel, onUse: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardNew),
        border = androidx.compose.foundation.BorderStroke(1.dp, LineBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(template.category.iconEmoji, fontSize = 20.sp)
                Text(template.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = InkText, fontFamily = Manrope)
            }
            Spacer(modifier = Modifier.height(12.dp))
            ConditionActionBox(template.ifConditionText, template.actionText)
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onUse,
                modifier = Modifier.fillMaxWidth().height(40.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BackgroundNew)
            ) {
                Text("Bu Şablonu Kullan", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryTeal, fontFamily = Manrope)
            }
        }
    }
}
