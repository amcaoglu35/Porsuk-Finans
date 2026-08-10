package com.nexus.porsuk.ui.ailab.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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

data class AiToolItem(
    val title: String,
    val emoji: String,
    val color: Color
)

@Composable
fun AiToolsGridSection(onToolClick: (String) -> Unit) {
    val primaryColor = PrimaryTeal
    val tools = listOf(
        AiToolItem("Smart Screener", "🎯", primaryColor),
        AiToolItem("Scenario Simulator", "🎲", primaryColor),
        AiToolItem("Risk Analysis", "⚠️", primaryColor),
        AiToolItem("Master Score", "🏆", primaryColor),
        AiToolItem("Dividend Wizard", "💵", primaryColor),
        AiToolItem("AI Earnings Summary", "📊", primaryColor),
        AiToolItem("Backtest Engine", "🧪", primaryColor),
        AiToolItem("Whale Tracking", "🐋", primaryColor)
    )

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            "Yapay Zeka Laboratuvarı",
            style = MaterialTheme.typography.titleMedium,
            color = InkText,
            fontWeight = FontWeight.Bold,
            fontFamily = Manrope
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.height(280.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            userScrollEnabled = false
        ) {
            items(tools) { tool ->
                AnimatedAiToolCard(tool = tool, onClick = { onToolClick(tool.title) })
            }
        }
    }
}

@Composable
fun AnimatedAiToolCard(tool: AiToolItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = CardNew),
        border = androidx.compose.foundation.BorderStroke(1.dp, LineBorder)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(tool.color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(tool.emoji, fontSize = 18.sp)
            }

            Text(
                text = tool.title,
                style = MaterialTheme.typography.labelMedium,
                color = InkText,
                fontWeight = FontWeight.Bold,
                fontFamily = Manrope,
                maxLines = 1
            )
        }
    }
}
