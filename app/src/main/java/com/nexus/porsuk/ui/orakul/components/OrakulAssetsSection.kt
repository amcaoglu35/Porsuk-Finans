package com.nexus.porsuk.ui.orakul.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
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
import dev.jeziellago.compose.markdowntext.MarkdownText

private val CardWhite = Color(0xFFFFFFFF)
private val PurpleSoftBg = Color(0xFFF3F0FF)
private val SuccessGreen = Color(0xFF00C48C)
private val TextDark = Color(0xFF0F172A)
private val TextSecondary = Color(0xFF64748B)
private val BorderColor = Color(0xFFF1F5F9)

data class SectorItem(
    val name: String,
    val score: String,
    val explanation: String
)

@Composable
fun OracleTimeframeFilterRow(
    selectedIndex: Int,
    onIndexSelected: (Int) -> Unit
) {
    val filters = listOf("Kısa Vade (1A)", "Orta Vade (6A)", "Uzun Vade (1Y)")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        filters.forEachIndexed { index, title ->
            val isSelected = selectedIndex == index
            FilterChip(
                selected = isSelected,
                onClick = { onIndexSelected(index) },
                label = {
                    Text(
                        text = title,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontFamily = Manrope
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Violet,
                    selectedLabelColor = Color.White,
                    containerColor = CardWhite,
                    labelColor = TextSecondary
                ),
                shape = RoundedCornerShape(12.dp),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = isSelected,
                    borderColor = BorderColor,
                    selectedBorderColor = Violet
                )
            )
        }
    }
}

@Composable
fun SectorsAndTopAssetsSection(
    selectedAssetTab: Int,
    onAssetTabSelected: (Int) -> Unit,
    onStockClick: (String, String) -> Unit,
    onSectorInsightClick: (SectorItem) -> Unit
) {
    val sectors = listOf(
        SectorItem("Bilişim & Yazılım", "+%18.4", "Bulut bilişim çözümleri ve SaaS gelirlerindeki artış ivmesi sektörü öne çıkarıyor."),
        SectorItem("Havacılık & Ulaştırma", "+%14.2", "Yolcu doluluk oranları ve dış hat talebindeki güçlü mevsimsellik."),
        SectorItem("Banka & Finans", "+%11.8", "Net faiz marjlarındaki toparlanma ve özkaynak kârlılık artışı.")
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        border = BorderStroke(1.dp, BorderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Öne Çıkan Sektörel Fırsatlar",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                color = TextDark,
                fontFamily = Manrope
            )

            sectors.forEach { sector ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(PurpleSoftBg.copy(alpha = 0.5f))
                        .clickable { onSectorInsightClick(sector) }
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(sector.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextDark, fontFamily = Manrope)
                        Text("Yapay zeka analiz detayları için dokunun", fontSize = 10.sp, color = TextSecondary, fontFamily = Manrope)
                    }

                    Text(sector.score, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = SuccessGreen, fontFamily = IBMPlexMono)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SectorExplanationBottomSheet(
    sector: SectorItem,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = CardWhite
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "📊 ${sector.name} Sektör Analizi",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = TextDark,
                fontFamily = Manrope
            )

            Text(
                text = "Beklenen İvme: ${sector.score}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = SuccessGreen,
                fontFamily = IBMPlexMono
            )

            MarkdownText(
                markdown = sector.explanation,
                style = androidx.compose.ui.text.TextStyle(
                    color = TextDark,
                    fontSize = 13.sp,
                    fontFamily = Manrope,
                    lineHeight = 20.sp
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Violet)
            ) {
                Text("Kapat", color = Color.White, fontWeight = FontWeight.Bold, fontFamily = Manrope)
            }
        }
    }
}
