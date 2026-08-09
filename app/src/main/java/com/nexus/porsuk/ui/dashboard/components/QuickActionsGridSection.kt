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
    val colorScheme = MaterialTheme.colorScheme
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
            gradientStart = colorScheme.primary,
            gradientEnd = colorScheme.primary.copy(alpha = 0.8f),
            accentSoft = colorScheme.primary.copy(alpha = 0.1f),
            onClick = onDoctorClick
        ),
        QuickActionItem(
            emoji = "⭐",
            title = "İzleme Listesi",
            subtitle = "Favori Hisselerim",
            gradientStart = colorScheme.tertiary,
            gradientEnd = colorScheme.tertiary.copy(alpha = 0.8f),
            accentSoft = colorScheme.tertiary.copy(alpha = 0.1f),
            onClick = onWatchlistClick
        ),
        QuickActionItem(
            emoji = "🔔",
            title = "Alarmlarım",
            subtitle = "Fiyat & Bildirim",
            gradientStart = NegatifRed,
            gradientEnd = NegatifRed.copy(alpha = 0.8f),
            accentSoft = RedSoft,
            onClick = onAlertsClick
        ),
        QuickActionItem(
            emoji = "☁️",
            title = "Yedekleme",
            subtitle = "Bulut Senkronizasyon",
            gradientStart = colorScheme.secondary,
            gradientEnd = colorScheme.secondary.copy(alpha = 0.8f),
            accentSoft = colorScheme.secondary.copy(alpha = 0.1f),
            onClick = onCloudSyncClick
        ),
        QuickActionItem(
            emoji = "📊",
            title = "Kurumsal Analiz",
            subtitle = "Bloomberg Terminal",
            gradientStart = colorScheme.onSurface,
            gradientEnd = colorScheme.onSurface.copy(alpha = 0.7f),
            accentSoft = colorScheme.onSurface.copy(alpha = 0.1f),
            onClick = onInstitutionalClick
        ),
        QuickActionItem(
            emoji = "📑",
            title = "Rapor Merkezi",
            subtitle = "PDF & Excel Döküm",
            gradientStart = colorScheme.primaryContainer,
            gradientEnd = colorScheme.primary.copy(alpha = 0.6f),
            accentSoft = VioletSoft,
            onClick = onReportingClick
        ),
        QuickActionItem(
            emoji = "🤖",
            title = "AI Yönetimi",
            subtitle = "Cloud & Local Hibrit",
            gradientStart = colorScheme.tertiaryContainer,
            gradientEnd = colorScheme.tertiary.copy(alpha = 0.6f),
            accentSoft = GoldSoft,
            onClick = onAiEngineClick
        ),
        QuickActionItem(
            emoji = "🧰",
            title = "Tüm Araçlar",
            subtitle = "14 Finansal Modül",
            gradientStart = EmeraldNew,
            gradientEnd = EmeraldNew.copy(alpha = 0.8f),
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
            .height(92.dp)
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
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
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
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.5.sp,
                            lineHeight = 14.sp
                        ),
                        color = onSurfaceColor,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        item.subtitle,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp),
                        color = onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
