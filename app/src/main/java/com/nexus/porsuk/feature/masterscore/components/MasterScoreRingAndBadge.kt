package com.nexus.porsuk.feature.masterscore.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nexus.porsuk.domain.model.MasterScoreResult
import com.nexus.porsuk.domain.model.ScoreLevel

/**
 * 7 Dereceli Skor Seviye Rozeti (ScoreLevelBadge)
 */
@Composable
fun ScoreLevelBadge(
    level: ScoreLevel,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color(level.colorHex).copy(alpha = 0.2f),
        modifier = modifier
    ) {
        Text(
            text = level.displayName,
            color = Color(level.colorHex),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

/**
 * Porsuk Master Score Engine — 0-100 Genel Skor Kartı (MasterScoreRingCard)
 */
@Composable
fun MasterScoreRingCard(
    result: MasterScoreResult?,
    modifier: Modifier = Modifier
) {
    val score = result?.masterScore ?: 0
    val level = result?.level ?: ScoreLevel.NEUTRAL

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
        )
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "PORSUK MASTER SCORE",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Dairesel Skor Gösterge Kutusu
            Box(
                modifier = Modifier.size(120.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = { score / 100f },
                    modifier = Modifier.fillMaxSize(),
                    color = Color(level.colorHex),
                    strokeWidth = 10.dp,
                    trackColor = Color(level.colorHex).copy(alpha = 0.15f)
                )
                Text(
                    text = "$score",
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(level.colorHex)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            ScoreLevelBadge(level = level)
        }
    }
}
