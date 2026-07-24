package com.nexus.porsuk.feature.companydetail.components

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nexus.porsuk.data.local.entity.CompanyEntity

/**
 * Porsuk Company Detail Module — Üst Üst Bölüm Kartı (CompanyHeaderCard)
 *
 * Logo, Sembol, Şirket Adı, Borsa, Sektör, Ülke, Favori Yıldızı ve Paylaş Butonunu içerir.
 */
@Composable
fun CompanyHeaderCard(
    symbol: String,
    company: CompanyEntity?,
    isFavorite: Boolean,
    onFavoriteToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val name = company?.companyName ?: symbol
    val exchange = company?.exchange ?: "BIST"
    val sector = company?.sector ?: "Finans / Teknoloji"
    val country = company?.country ?: "TR"

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Sol Taraf: Şirket Logosu / Avatarı ve Bilgiler
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Logo Avatar Kutusu
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = symbol.take(2).uppercase(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = symbol,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                    Row(
                        modifier = Modifier.padding(top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        BadgeChip(text = exchange)
                        BadgeChip(text = sector)
                        BadgeChip(text = country)
                    }
                }
            }

            // Sağ Taraf: Favori Yıldızı & Paylaş Butonları
            Row {
                IconButton(onClick = onFavoriteToggle) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Star else Icons.Outlined.StarBorder,
                        contentDescription = "Favori",
                        tint = if (isFavorite) Color(0xFFFFB300) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = { shareCompanyDetails(context, symbol, name) }) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Paylaş",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun BadgeChip(text: String) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

private fun shareCompanyDetails(context: Context, symbol: String, name: String) {
    val shareIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(
            Intent.EXTRA_TEXT,
            "Porsuk Finans ile $symbol ($name) şirketini inceliyorum! Detaylar için uygulamaya göz atın."
        )
        type = "text/plain"
    }
    context.startActivity(Intent.createChooser(shareIntent, "Şirketi Paylaş"))
}
