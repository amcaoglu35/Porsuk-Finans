package com.nexus.porsuk.feature.companydetail.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.nexus.porsuk.data.local.entity.NewsEntity
import com.nexus.porsuk.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TabNewsContent(
    newsList: List<com.nexus.porsuk.data.local.entity.NewsEntity>,
    modifier: Modifier = Modifier
) {
    val mainGreen = Color(0xFF14B88A)

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        if (newsList.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                Text("Şirket ile ilgili haber bulunamadı.", color = SubText, fontSize = 14.sp)
            }
            return@Column
        }

        // AI Haber Özeti Kartı
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = mainGreen.copy(alpha = 0.05f)),
            border = BorderStroke(1.dp, mainGreen.copy(alpha = 0.2f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = mainGreen, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "AI Duyarlılık Analizi", style = MaterialTheme.typography.labelLarge, color = mainGreen, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "En son paylaşılan ${newsList.size} haber analiz edildi. Genel duyarlılık dengeli seyrediyor.",
                    style = MaterialTheme.typography.bodySmall,
                    color = InkText
                )
            }
        }

        // Haber Kartları
        newsList.forEach { news ->
            NewsCard(news = news)
        }
    }
}

@Composable
fun NewsCard(news: com.nexus.porsuk.data.local.entity.NewsEntity) {
    val mainGreen = Color(0xFF14B88A)
    val sdf = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault())

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = "${news.source} • ${sdf.format(Date(news.publishedAt))}",
                    style = MaterialTheme.typography.labelSmall,
                    color = SubText
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = news.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = InkText,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            if (news.summary.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = news.summary,
                    style = MaterialTheme.typography.bodySmall,
                    color = SubText,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
