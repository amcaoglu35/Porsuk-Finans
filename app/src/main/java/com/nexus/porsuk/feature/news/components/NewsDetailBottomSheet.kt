package com.nexus.porsuk.feature.news.components

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nexus.porsuk.domain.model.NewsArticle

/**
 * Porsuk News Intelligence Center — Haber Detayı Okuma BottomSheet
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsDetailBottomSheet(
    article: NewsArticle,
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = article.source,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )

                IconButton(onClick = { shareNewsArticle(context, article) }) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Paylaş",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Text(
                text = article.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            HorizontalDivider()

            // Geleceğe Hazır Orakul AI Haber Özeti & Yorumu Stub Kartı
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "🤖 Orakul AI Haber Özeti & Etki Skoru: ${article.impactScore}/10",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Orakul Analizi: Bu haber piyasa beklentilerine olumlu yansıyabilir. Şirketin genel bilanço projeksiyonu korunuyor.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Text(
                text = article.content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

private fun shareNewsArticle(context: Context, article: NewsArticle) {
    val shareIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(
            Intent.EXTRA_TEXT,
            "Porsuk Finans Haber: ${article.title}\n\nDetaylar: ${article.summary}"
        )
        type = "text/plain"
    }
    context.startActivity(Intent.createChooser(shareIntent, "Haberi Paylaş"))
}
