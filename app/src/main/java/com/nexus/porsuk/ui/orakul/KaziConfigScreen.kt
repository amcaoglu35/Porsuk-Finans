package com.nexus.porsuk.ui.orakul

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun KaziConfigScreen(
    viewModel: KaziViewModel,
    onBack: () -> Unit,
    onStartMining: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Derin Kazı Yapılandırma", fontFamily = Manrope, fontWeight = FontWeight.Bold, color = InkText) },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .padding(8.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(AquaSoft)
                            .border(1.dp, LineBorder, RoundedCornerShape(12.dp))
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri", tint = PrimaryTeal)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundNew)
            )
        },
        containerColor = BackgroundNew
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(modifier = Modifier.height(6.dp))

            // Warning note
            Card(
                colors = CardDefaults.cardColors(containerColor = AquaSoft.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, LineBorder)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = PrimaryTeal)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "Bu işlem tüm piyasayı tarar ve 5-10 dakika sürebilir. Arka planda devasa bir O-EAGI quants hesaplaması yapar.",
                        style = MaterialTheme.typography.bodySmall,
                        color = InkText,
                        fontFamily = Manrope
                    )
                }
            }

            // Risk Profile
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Risk Profili", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = InkText, fontFamily = Manrope)
                KaziOptionSegmentRow(
                    options = listOf("CONSERVATIVE" to "Muhafazakar", "BALANCED" to "Dengeli", "AGGRESSIVE" to "Agresif"),
                    selectedOption = uiState.riskProfile,
                    onOptionSelected = viewModel::setRiskProfile
                )
            }

            // Horizon
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Yatırım Ufku", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = InkText, fontFamily = Manrope)
                KaziOptionSegmentRow(
                    options = listOf("SHORT" to "Kısa Vade", "MEDIUM" to "Orta Vade", "LONG" to "Uzun Vade"),
                    selectedOption = uiState.horizon,
                    onOptionSelected = viewModel::setHorizon
                )
            }

            // Strategy Focus
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Yatırım Stratejisi", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = InkText, fontFamily = Manrope)
                KaziOptionSegmentRow(
                    options = listOf("VALUE" to "Değer (Moat)", "GROWTH" to "Büyüme", "DIVIDEND" to "Temettü"),
                    selectedOption = uiState.strategyFocus,
                    onOptionSelected = viewModel::setStrategyFocus
                )
            }

            // Capital
            OutlinedTextField(
                value = uiState.capital,
                onValueChange = viewModel::setCapital,
                label = { Text("Yatırılacak Tutar (Opsiyonel)", fontFamily = Manrope) },
                placeholder = { Text("Örn: 50.000", fontFamily = Manrope) },
                suffix = { Text("TL", fontFamily = IBMPlexMono) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryTeal, unfocusedBorderColor = LineBorder)
            )

            // Excluded Sectors
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Hariç Tutulacak Sektörler", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = InkText, fontFamily = Manrope)
                val sectors = listOf("Teknoloji", "Finans", "Enerji", "Sanayi", "Perakende", "Sağlık")
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    sectors.forEach { sector ->
                        val isExcluded = uiState.excludedSectors.contains(sector)
                        FilterChip(
                            selected = isExcluded,
                            onClick = { viewModel.toggleSector(sector) },
                            label = { Text(sector, fontFamily = Manrope, fontSize = 11.sp) },
                            leadingIcon = if (isExcluded) { { Icon(Icons.Default.Warning, null, modifier = Modifier.size(14.dp)) } } else null
                        )
                    }
                }
            }

            // Target Stock Count
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Hedef Hisse Sayısı", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = InkText, fontFamily = Manrope)
                KaziOptionSegmentRow(
                    options = listOf(3 to "3 Hisse", 5 to "5 Hisse", 7 to "7 Hisse", 10 to "10 Hisse"),
                    selectedOption = uiState.targetStockCount,
                    onOptionSelected = viewModel::setTargetStockCount
                )
            }

            // AI Reasoning Depth
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Yapay Zeka Analiz Derinliği", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = InkText, fontFamily = Manrope)
                KaziOptionSegmentRow(
                    options = listOf("STANDART" to "Hızlı", "DEEP" to "Derin", "ULTRA" to "Quant Ultra"),
                    selectedOption = uiState.reasoningDepth,
                    onOptionSelected = viewModel::setReasoningDepth
                )
            }

            // Min Quality Score
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Min. Hisse Kalite Barajı", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = InkText, fontFamily = Manrope)
                KaziOptionSegmentRow(
                    options = listOf(0 to "Tümü", 50 to "50+", 65 to "65+", 75 to "75+"),
                    selectedOption = uiState.minQualityScore,
                    onOptionSelected = viewModel::setMinQualityScore
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    viewModel.startMining()
                    onStartMining()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Brush.horizontalGradient(listOf(PrimaryTeal, AquaNew))),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
            ) {
                Text("⛏️ Derin Kazıya Başla", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White, fontFamily = Manrope)
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun <T> KaziOptionSegmentRow(
    options: List<Pair<T, String>>,
    selectedOption: T,
    onOptionSelected: (T) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CardNew)
            .border(1.dp, LineBorder, RoundedCornerShape(14.dp))
            .padding(3.dp),
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        options.forEach { (key, label) ->
            val isSelected = selectedOption == key
            Box(
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isSelected) PrimaryTeal else Color.Transparent)
                    .clickable { onOptionSelected(key) }
                    .padding(horizontal = 4.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    ),
                    color = if (isSelected) Color.White else InkText,
                    fontFamily = Manrope,
                    maxLines = 2,
                    softWrap = true,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    fontSize = 10.5.sp,
                    lineHeight = 13.sp
                )
            }
        }
    }
}
