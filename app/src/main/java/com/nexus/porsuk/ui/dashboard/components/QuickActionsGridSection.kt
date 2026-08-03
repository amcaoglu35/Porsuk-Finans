package com.nexus.porsuk.ui.dashboard.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.ui.theme.*

data class QuickActionItem(
    val emoji: String,
    val title: String,
    val subtitle: String,
    val gradientStart: Color,
    val gradientEnd: Color,
    val accentSoft: Color,
    val onClick: () -> Unit
)

@Composable
fun QuickActionsGrid(
    onLedgerClick: () -> Unit,
    onCalendarClick: () -> Unit,
    onAnalysisClick: () -> Unit,
    onModelSepetlerClick: () -> Unit,
    onKapRadarClick: () -> Unit = {},
    onInstitutionalClick: () -> Unit = {},
    onReportingClick: () -> Unit = {},
    onAiEngineClick: () -> Unit = {},
    onPluginsClick: () -> Unit = {},
    onCloudSyncClick: () -> Unit = {},
    onDoctorClick: () -> Unit = {},
    onWatchlistClick: () -> Unit = {},
    onAlertsClick: () -> Unit = {},
    onAllToolsClick: () -> Unit = {}
) {
    val actions = listOf(
        QuickActionItem(
            emoji = "📋",
            title = "İşlem Defterim",
            subtitle = "Alım / satım geçmişi",
            gradientStart = PrimaryTeal,
            gradientEnd = Color(0xFF007A58),
            accentSoft = TealSoft,
            onClick = onLedgerClick
        ),
        QuickActionItem(
            emoji = "📅",
            title = "Borsa Takvimi",
            subtitle = "Temettü & Bilanço",
            gradientStart = AquaNew,
            gradientEnd = Color(0xFF008BA3),
            accentSoft = AquaSoft,
            onClick = onCalendarClick
        ),
        QuickActionItem(
            emoji = "🩺",
            title = "Portföy Doktoru",
            subtitle = "Sağlık Skoru & Rebalans",
            gradientStart = Color(0xFF6C4CF1),
            gradientEnd = Color(0xFF4C2CE1),
            accentSoft = Color(0xFFF3F0FF),
            onClick = onDoctorClick
        ),
        QuickActionItem(
            emoji = "⭐",
            title = "İzleme Listesi",
            subtitle = "Favori Hisselerim",
            gradientStart = Color(0xFFF59E0B),
            gradientEnd = Color(0xFFD97706),
            accentSoft = Color(0xFFFEF3C7),
            onClick = onWatchlistClick
        ),
        QuickActionItem(
            emoji = "🔔",
            title = "Alarmlarım",
            subtitle = "Fiyat & Bildirim",
            gradientStart = Color(0xFFEF4444),
            gradientEnd = Color(0xFFB91C1C),
            accentSoft = Color(0xFFFEE2E2),
            onClick = onAlertsClick
        ),
        QuickActionItem(
            emoji = "☁️",
            title = "Yedekleme",
            subtitle = "Bulut Senkronizasyon",
            gradientStart = Color(0xFF3B82F6),
            gradientEnd = Color(0xFF1D4ED8),
            accentSoft = Color(0xFFDBEAFE),
            onClick = onCloudSyncClick
        ),
        QuickActionItem(
            emoji = "📊",
            title = "Kurumsal Analiz",
            subtitle = "Bloomberg Terminal",
            gradientStart = Color(0xFF1E293B),
            gradientEnd = Color(0xFF0F172A),
            accentSoft = Color(0xFFE2E8F0),
            onClick = onInstitutionalClick
        ),
        QuickActionItem(
            emoji = "📑",
            title = "Rapor Merkezi",
            subtitle = "PDF & Excel Döküm",
            gradientStart = Color(0xFF7C6CF0),
            gradientEnd = Color(0xFF5C4AD8),
            accentSoft = Color(0xFFECE9FE),
            onClick = onReportingClick
        ),
        QuickActionItem(
            emoji = "🤖",
            title = "AI Yönetimi",
            subtitle = "Cloud & Local Hibrit",
            gradientStart = Color(0xFFE8A93B),
            gradientEnd = Color(0xFFC8891E),
            accentSoft = Color(0xFFFBF1DD),
            onClick = onAiEngineClick
        ),
        QuickActionItem(
            emoji = "🧰",
            title = "Tüm Araçlar",
            subtitle = "14 Finansal Modül",
            gradientStart = Color(0xFF10B981),
            gradientEnd = Color(0xFF047857),
            accentSoft = AquaSoft,
            onClick = onAllToolsClick
        )
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        for (i in actions.indices step 2) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                PremiumQuickActionCard(item = actions[i], modifier = Modifier.weight(1f))
                if (i + 1 < actions.size) {
                    PremiumQuickActionCard(item = actions[i + 1], modifier = Modifier.weight(1f))
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun PremiumQuickActionCard(
    item: QuickActionItem,
    modifier: Modifier = Modifier
) {
    val surfaceColor = MaterialTheme.colorScheme.surface
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant
    val outlineColor = MaterialTheme.colorScheme.outline

    var isCardPressed by remember { mutableStateOf(false) }
    val cardScale by animateFloatAsState(
        targetValue = if (isCardPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "scale"
    )

    Card(
        modifier = modifier
            .height(82.dp)
            .graphicsLayer(
                scaleX = cardScale,
                scaleY = cardScale
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                item.onClick()
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = surfaceColor),
        border = BorderStroke(1.dp, outlineColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(item.gradientStart.copy(alpha = 0.05f), Color.Transparent)
                        )
                    )
            )

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(item.accentSoft),
                    contentAlignment = Alignment.Center
                ) {
                    Text(item.emoji, fontSize = 20.sp)
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        item.title,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = onSurfaceColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        item.subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
