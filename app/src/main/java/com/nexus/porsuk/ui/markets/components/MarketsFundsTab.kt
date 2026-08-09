package com.nexus.porsuk.ui.markets.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.data.local.entity.TefasFundEntity
import com.nexus.porsuk.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FundsTab(
    funds: List<TefasFundEntity>,
    onFundClick: (String) -> Unit
) {
    if (funds.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize().padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "TEFAS fon verileri senkronize ediliyor...",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Veriler indirildikçe otomatik olarak görüntülenecektir.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(funds, key = { it.code }) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(3.dp, RoundedCornerShape(20.dp))
                        .clickable { onFundClick(item.code) },
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(item.code.take(3), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, fontSize = 10.sp), color = MaterialTheme.colorScheme.primary)
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1.2f)) {
                            Text(item.code, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, fontFamily = Manrope), color = MaterialTheme.colorScheme.onSurface)
                            Text(item.name, style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp), color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }

                        Column(horizontalAlignment = Alignment.End, modifier = Modifier.weight(1.0f)) {
                            Text("₺${String.format("%.4f", item.price)}", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold, fontFamily = IBMPlexMono), color = MaterialTheme.colorScheme.onSurface)
                            Text(item.fundType.ifBlank { "Yatırım Fonu" }, style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp), color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FundDetailItemRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, fontFamily = IBMPlexMono), color = MaterialTheme.colorScheme.onSurface)
    }
}
