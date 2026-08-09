package com.nexus.porsuk.ui.dashboard.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.ui.dashboard.BasketSummaryUiModel
import com.nexus.porsuk.ui.common.CurrencyFormatter
import com.nexus.porsuk.ui.common.marketToFlag
import com.nexus.porsuk.ui.theme.*

@Composable
fun MyBasketsSection(
    baskets: List<BasketSummaryUiModel>,
    onBasketClick: (Int) -> Unit,
    onCreateBasketClick: () -> Unit,
    onSeeAllClick: () -> Unit
) {
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val primaryColor = MaterialTheme.colorScheme.primary

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "🧺 Sepetlerim",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = Manrope
                    ),
                    color = onSurfaceColor
                )
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = primaryColor.copy(alpha = 0.1f)
                ) {
                    Text(
                        "${baskets.size}",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = primaryColor
                    )
                }
            }
            
            TextButton(onClick = onSeeAllClick) {
                Text(
                    "Tümünü Gör",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = primaryColor
                )
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = null,
                    modifier = Modifier.size(12.dp),
                    tint = primaryColor
                )
            }
        }

        if (baskets.isEmpty()) {
            EmptyBasketCard(onClick = onCreateBasketClick)
        } else {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(baskets, key = { it.id }) { basket ->
                    BasketMiniCard(basket = basket, onClick = { onBasketClick(basket.id) })
                }
                
                item {
                    CreateNewBasketMiniCard(onClick = onCreateBasketClick)
                }
            }
        }
    }
}

@Composable
private fun BasketMiniCard(
    basket: BasketSummaryUiModel,
    onClick: () -> Unit
) {
    val surfaceColor = MaterialTheme.colorScheme.surface
    val outlineColor = MaterialTheme.colorScheme.outline
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant

    Card(
        modifier = Modifier
            .width(150.dp)
            .height(110.dp)
            .shadow(2.dp, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = surfaceColor),
        border = BorderStroke(1.dp, outlineColor.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(marketToFlag(basket.market), fontSize = 20.sp)
                Text(
                    "${basket.itemCount} Hisse",
                    style = MaterialTheme.typography.labelSmall,
                    color = onSurfaceVariant
                )
            }

            Column {
                Text(
                    basket.name,
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = onSurfaceColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    CurrencyFormatter.formatTRY(basket.totalValue),
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontFamily = IBMPlexMono,
                        fontWeight = FontWeight.Bold
                    ),
                    color = PrimaryTeal
                )
            }
        }
    }
}

@Composable
private fun CreateNewBasketMiniCard(onClick: () -> Unit) {
    val outlineColor = MaterialTheme.colorScheme.outline
    val primaryColor = MaterialTheme.colorScheme.primary

    Surface(
        modifier = Modifier
            .width(120.dp)
            .height(110.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = Color.Transparent,
        border = BorderStroke(
            width = 1.dp,
            color = outlineColor.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = null,
                tint = primaryColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "Yeni Sepet",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = primaryColor
            )
        }
    }
}

@Composable
private fun EmptyBasketCard(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Default.Add, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                "Henüz sepet oluşturmadınız. İlk sepetinizi oluşturun.",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
