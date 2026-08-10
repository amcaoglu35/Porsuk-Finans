package com.nexus.porsuk.ui.ailab.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.ui.theme.*

@Composable
fun TripleAiModulesGridSection(
    onNavigateToPerformance: () -> Unit,
    onNavigateToStrategy: () -> Unit,
    onNavigateToIntelligence: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            "Gelişmiş AI Modülleri",
            style = MaterialTheme.typography.titleMedium,
            color = InkText,
            fontWeight = FontWeight.Bold,
            fontFamily = Manrope
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickActionTile(
                title = "AI Performance",
                emoji = "📈",
                onClick = onNavigateToPerformance,
                modifier = Modifier.weight(1f)
            )
            QuickActionTile(
                title = "Strategy Mimarı",
                emoji = "🏗️",
                onClick = onNavigateToStrategy,
                modifier = Modifier.weight(1f)
            )
            QuickActionTile(
                title = "Global Zeka",
                emoji = "🌐",
                onClick = onNavigateToIntelligence,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun TripleNotificationsAndReportsGridSection(
    onNavigateToNotifications: () -> Unit,
    onNavigateToReports: () -> Unit,
    isPriceAlertsEnabled: Boolean,
    onPriceAlertsToggle: (Boolean) -> Unit,
    isNewsSentimentEnabled: Boolean,
    onNewsSentimentToggle: (Boolean) -> Unit,
    isAiAutomationEnabled: Boolean,
    onAiAutomationToggle: (Boolean) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            "Bildirimler & Raporlama",
            style = MaterialTheme.typography.titleMedium,
            color = InkText,
            fontWeight = FontWeight.Bold,
            fontFamily = Manrope
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = CardNew),
            border = androidx.compose.foundation.BorderStroke(1.dp, LineBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                AutomationSwitchRow("Akıllı Fiyat Alarmları", isPriceAlertsEnabled, onPriceAlertsToggle)
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = LineBorder.copy(alpha = 0.5f))
                AutomationSwitchRow("Haber Duyarlılık Analizi", isNewsSentimentEnabled, onNewsSentimentToggle)
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = LineBorder.copy(alpha = 0.5f))
                AutomationSwitchRow("AI Otomasyon Motoru", isAiAutomationEnabled, onAiAutomationToggle)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onNavigateToNotifications,
                modifier = Modifier.weight(1f).height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal)
            ) {
                Text("Tüm Bildirimler", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            OutlinedButton(
                onClick = onNavigateToReports,
                modifier = Modifier.weight(1f).height(48.dp),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryTeal)
            ) {
                Text("Rapor Merkezi", color = PrimaryTeal, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun AutomationSwitchRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.bodyMedium, color = InkText, fontFamily = Manrope)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = PrimaryTeal,
                uncheckedThumbColor = SubText,
                uncheckedTrackColor = LineBorder
            )
        )
    }
}

@Composable
fun QuickActionsGridSection(
    onNavigateToDoctor: () -> Unit,
    onNavigateToSimulator: () -> Unit,
    onNavigateToOpportunities: () -> Unit,
    onNavigateToOptimization: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            "Hızlı Aksiyonlar",
            style = MaterialTheme.typography.titleMedium,
            color = InkText,
            fontWeight = FontWeight.Bold,
            fontFamily = Manrope
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickActionTile("Doktor", "🩺", onNavigateToDoctor, Modifier.weight(1f))
            QuickActionTile("Simülatör", "🎮", onNavigateToSimulator, Modifier.weight(1f))
            QuickActionTile("Fırsatlar", "🔥", onNavigateToOpportunities, Modifier.weight(1f))
            QuickActionTile("Optimize", "⚖️", onNavigateToOptimization, Modifier.weight(1f))
        }
    }
}

@Composable
fun QuickActionTile(
    title: String,
    emoji: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(90.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardNew),
        border = androidx.compose.foundation.BorderStroke(1.dp, LineBorder)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(PrimaryTeal.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(emoji, fontSize = 20.sp)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = InkText,
                fontFamily = Manrope
            )
        }
    }
}
