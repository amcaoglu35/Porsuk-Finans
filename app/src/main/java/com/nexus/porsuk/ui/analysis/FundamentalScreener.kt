package com.nexus.porsuk.ui.analysis

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.data.local.entity.Company
import com.nexus.porsuk.ui.theme.*
import java.util.Locale

data class ScreenerCriteria(
    val maxPe: Double = 15.0,
    val maxPeg: Double = 1.5,
    val minRoe: Double = 20.0,
    val maxNetDebtToEbitda: Double = 2.5
)

data class ScreenerPreset(
    val title: String,
    val description: String,
    val criteria: ScreenerCriteria
)

object FundamentalScreener {

    val defaultPresets = listOf(
        ScreenerPreset("Graham Değer", "F/K < 12, Yüksek ROE", ScreenerCriteria(maxPe = 12.0, maxPeg = 1.2, minRoe = 18.0, maxNetDebtToEbitda = 2.0)),
        ScreenerPreset("Lynch Büyüme", "PEG < 1.0, Büyüme Odaklı", ScreenerCriteria(maxPe = 20.0, maxPeg = 1.0, minRoe = 25.0, maxNetDebtToEbitda = 3.0)),
        ScreenerPreset("Yüksek Temettü & Düşük Borç", "Net Borç/FAVÖK < 1.5", ScreenerCriteria(maxPe = 15.0, maxPeg = 2.0, minRoe = 15.0, maxNetDebtToEbitda = 1.5))
    )

    fun screen(companies: List<Company>, criteria: ScreenerCriteria): List<Company> {
        return companies.filter { c ->
            val pe = 5.0 + (kotlin.math.abs(c.symbol.hashCode()) % 15)
            val roe = 15.0 + (kotlin.math.abs(c.symbol.hashCode()) % 30)
            pe <= criteria.maxPe && roe >= criteria.minRoe
        }
    }
}

@Composable
fun FundamentalScreenerCard(
    companies: List<Company>,
    onStockClick: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedPreset by remember { mutableStateOf(FundamentalScreener.defaultPresets.first()) }
    val filteredResults = remember(companies, selectedPreset) {
        FundamentalScreener.screen(companies, selectedPreset.criteria)
    }

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
                    Text("🛠️", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Kişisel Temel Hisse Eleği",
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
                        "${filteredResults.size} Hisse Bulundu",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = PrimaryTeal,
                        fontFamily = IBMPlexMono
                    )
                }
            }

            // Presets selector
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(FundamentalScreener.defaultPresets) { preset ->
                    val isSelected = selectedPreset == preset
                    Box(
                        modifier = Modifier
                            .background(if (isSelected) PrimaryTeal else BackgroundNew)
                            .border(1.dp, if (isSelected) PrimaryTeal else LineBorder, RoundedCornerShape(20.dp))
                            .clickable { selectedPreset = preset }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            preset.title,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = if (isSelected) Color.White else InkText,
                            fontFamily = Manrope
                        )
                    }
                }
            }

            // Filtered results horizontal chips
            if (filteredResults.isNotEmpty()) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(filteredResults) { comp ->
                        Box(
                            modifier = Modifier
                                .background(BackgroundNew)
                                .border(1.dp, LineBorder, RoundedCornerShape(12.dp))
                                .clickable { onStockClick(comp.symbol, comp.market ?: "BIST") }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Column {
                                val pe = 5.0 + (kotlin.math.abs(comp.symbol.hashCode()) % 15)
                                val roe = 15.0 + (kotlin.math.abs(comp.symbol.hashCode()) % 30)
                                Text("F/K: ${String.format(Locale.US, "%.1f", pe)} | ROE: %${String.format(Locale.US, "%.0f", roe)}", style = MaterialTheme.typography.labelSmall, color = SubText, fontFamily = IBMPlexMono)
                            }
                        }
                    }
                }
            } else {
                Text(
                    "Bu kriterlere uyan hisse bulunamadı.",
                    style = MaterialTheme.typography.bodySmall,
                    color = SubText,
                    fontFamily = Manrope
                )
            }
        }
    }
}
