package com.nexus.porsuk.ui.analysis

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.data.remote.TcmbMacroData
import com.nexus.porsuk.ui.theme.*
import java.util.Locale

@Composable
fun MacroMetricsCard(
    macroData: TcmbMacroData,
    modifier: Modifier = Modifier
) {
    val yieldSpread = macroData.bond10Y - macroData.bond2Y
    val isInverted = yieldSpread < 0.0

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardNew),
        border = androidx.compose.foundation.BorderStroke(1.dp, LineBorder)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🏛️", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "TCMB Makro & Getiri Eğrisi",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = InkText,
                        fontFamily = Manrope
                    )
                }
                Box(
                    modifier = Modifier
                        .background(AquaSoft)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .border(1.dp, PrimaryTeal.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                ) {
                    Text(
                        "TCMB EVDS",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = PrimaryTeal,
                        fontFamily = IBMPlexMono
                    )
                }
            }

            // Grid of EVDS indicators
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MacroTile(
                    title = "Politika Faizi",
                    value = "%${String.format(Locale.US, "%.1f", macroData.policyRate)}",
                    subtitle = "TCMB",
                    modifier = Modifier.weight(1f)
                )
                MacroTile(
                    title = "TÜFE Enflasyon",
                    value = "%${String.format(Locale.US, "%.1f", macroData.cpiInflation)}",
                    subtitle = "Yıllık",
                    valueColor = RoseNew,
                    modifier = Modifier.weight(1f)
                )
                MacroTile(
                    title = "Reel Kur (REER)",
                    value = String.format(Locale.US, "%.1f", macroData.reerUsd),
                    subtitle = "TÜFE Bazlı",
                    modifier = Modifier.weight(1f)
                )
            }

            // Yield Curve visualization bar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BackgroundNew)
                    .padding(12.dp)
                    .border(1.dp, LineBorder, RoundedCornerShape(12.dp)),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "📈 Tahvil Getiri Eğrisi (Yield Curve)",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = InkText,
                        fontFamily = Manrope
                    )
                    Text(
                        text = if (isInverted) "⚠️ Ters Eğri (Resesyon Sinyali)" else "✅ Normal Eğri",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (isInverted) RoseNew else EmeraldNew,
                        fontFamily = Manrope
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "2 Yıllık Tahvil: %${String.format(Locale.US, "%.2f", macroData.bond2Y)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = SubText,
                        fontFamily = IBMPlexMono
                    )
                    Text(
                        "10 Yıllık Tahvil: %${String.format(Locale.US, "%.2f", macroData.bond10Y)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = SubText,
                        fontFamily = IBMPlexMono
                    )
                }

                Text(
                    "2Y/10Y Getiri Makası: ${String.format(Locale.US, "%+.2f%%", yieldSpread)}",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                    color = InkText,
                    fontFamily = IBMPlexMono
                )
            }
        }
    }
}

@Composable
private fun MacroTile(
    title: String,
    value: String,
    subtitle: String,
    valueColor: Color = PrimaryTeal,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(BackgroundNew)
            .padding(10.dp)
            .border(1.dp, LineBorder, RoundedCornerShape(10.dp))
    ) {
        Column {
            Text(title, style = MaterialTheme.typography.labelSmall, color = SubText, fontFamily = Manrope)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = valueColor, fontFamily = IBMPlexMono)
            Spacer(modifier = Modifier.height(2.dp))
            Text(subtitle, style = MaterialTheme.typography.labelSmall, color = SubText.copy(alpha = 0.7f), fontSize = 9.sp, fontFamily = Manrope)
        }
    }
}
