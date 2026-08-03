package com.nexus.porsuk.ui.analysis.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.ui.common.Sparkline
import com.nexus.porsuk.ui.theme.*

@Composable
fun QuickAnalysisModulesRow(onModuleClick: () -> Unit) {
    val modules = remember {
        listOf(
            QuickModuleItem("Teknik Analiz", "📈"),
            QuickModuleItem("Temel Analiz", "📊"),
            QuickModuleItem("Haber Analizi", "📰"),
            QuickModuleItem("Makro Analiz", "🌐"),
            QuickModuleItem("Portföy Etki", "🍕"),
            QuickModuleItem("Senaryo Simülasyonu", "⚙️")
        )
    }

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        items(modules, key = { it.title }) { item ->
            Column(
                modifier = Modifier
                    .width(72.dp)
                    .clickable(onClick = onModuleClick),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.size(52.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(item.iconEmoji, fontSize = 22.sp)
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    item.title,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp, fontFamily = Manrope),
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

private data class QuickModuleItem(val title: String, val iconEmoji: String)

@Composable
fun DetailedAnalysisModulesSection(onModuleClick: (String) -> Unit) {
    val detailedModules = remember {
        listOf(
            DetailedModuleItem("Teknik Analiz", "RSI, MACD, EMA ve daha fazlası", "75 /100", PozitifGreen, "📈", listOf(40f, 45f, 42f, 48f, 50f)),
            DetailedModuleItem("Temel Analiz", "F/K, PD/DD, ROE, Bilanço ve daha fazlası", "80 /100", PozitifGreen, "📊", listOf(50f, 52f, 55f, 58f, 60f)),
            DetailedModuleItem("Haber Analizi", "Haberlerin hisse üzerindeki etkisi", "78 /100", AmberWarning, "📰", listOf(30f, 32f, 31f, 35f, 38f)),
            DetailedModuleItem("Makro Analiz", "Döviz, faiz, enflasyon ve endeksler", "68 /100", AmberWarning, "🌐", listOf(60f, 59f, 62f, 65f, 68f)),
            DetailedModuleItem("Portföy Etki Analizi", "Portföyünüz üzerindeki olası etkiler", "72 /100", PozitifGreen, "🍕", listOf(40f, 43f, 45f, 48f, 52f)),
            DetailedModuleItem("Senaryo Simülasyonu", "Faiz, kur, enflasyon senaryoları", "Yeni", Color(0xFF6C4CF1), "⚙️", listOf(50f, 55f, 52f, 58f, 60f))
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shadow(4.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                "ANALİZ MODÜLLERİ",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp),
                color = MaterialTheme.colorScheme.primary,
                fontFamily = Manrope
            )

            Spacer(modifier = Modifier.height(14.dp))

            detailedModules.forEach { item ->
                DetailedModuleRow(item = item, onClick = { onModuleClick(item.title) })
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
            }
        }
    }
}

private data class DetailedModuleItem(
    val title: String,
    val description: String,
    val scoreBadge: String,
    val scoreColor: Color,
    val iconEmoji: String,
    val sparkValues: List<Float>
)

@Composable
private fun DetailedModuleRow(item: DetailedModuleItem, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(36.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(item.iconEmoji, fontSize = 18.sp)
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1.3f)) {
            Text(item.title, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = MaterialTheme.colorScheme.onSurface)
            Text(item.description, style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp), color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }

        Surface(
            shape = RoundedCornerShape(10.dp),
            color = item.scoreColor.copy(alpha = 0.12f)
        ) {
            Text(
                item.scoreBadge,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, fontSize = 9.5.sp, fontFamily = IBMPlexMono),
                color = item.scoreColor,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Sparkline(
            values = item.sparkValues,
            color = item.scoreColor,
            modifier = Modifier
                .width(60.dp)
                .height(24.dp),
            filled = true
        )

        Spacer(modifier = Modifier.width(6.dp))

        Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f), modifier = Modifier.size(12.dp))
    }
}
