package com.nexus.porsuk.feature.ai

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nexus.porsuk.domain.model.*
import com.nexus.porsuk.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiEngineManagerScreen(
    onBack: () -> Unit,
    viewModel: AiEngineViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val tabs = listOf("Modlar", "Modeller", "Kalite Kontrol", "İstatistik")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "AI Engine Manager",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            fontFamily = Manrope
                        )
                        Text(
                            text = "Cloud & Local Hibrit AI Yönetimi",
                            style = MaterialTheme.typography.labelSmall,
                            color = SubText,
                            fontFamily = Manrope
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Geri")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundNew)
            )
        },
        containerColor = BackgroundNew
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Tab Row
            ScrollableTabRow(
                selectedTabIndex = uiState.selectedTab,
                containerColor = BackgroundNew,
                contentColor = PrimaryTeal,
                edgePadding = 16.dp,
                divider = {}
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = uiState.selectedTab == index,
                        onClick = { viewModel.selectTab(index) },
                        text = {
                            Text(
                                text = title,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = Manrope
                            )
                        }
                    )
                }
            }

            Crossfade(targetState = uiState.selectedTab, label = "ai_tabs") { tabIndex ->
                when (tabIndex) {
                    0 -> AiModesTab(uiState, viewModel)
                    1 -> ModelManagementTab(uiState, viewModel)
                    2 -> QualityControlTab(uiState)
                    3 -> EngineStatsTab(uiState)
                }
            }
        }
    }
}

@Composable
fun AiModesTab(state: AiEngineUiState, viewModel: AiEngineViewModel) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("AI Çalışma Modu Seçin", fontWeight = FontWeight.Bold, color = PrimaryTeal)
        }
        items(AiOperationMode.entries) { mode ->
            val isSelected = state.operationMode == mode
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.setMode(mode) },
                border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, PrimaryTeal) else null,
                colors = CardDefaults.cardColors(containerColor = CardNew)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(mode.iconEmoji, fontSize = 28.sp)
                    Spacer(Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(mode.displayName, fontWeight = FontWeight.Bold)
                        Text(
                            text = when(mode) {
                                AiOperationMode.CLOUD_ONLY -> "Tüm analizler yüksek performanslı bulut sunucularda yapılır."
                                AiOperationMode.LOCAL_ONLY -> "Analizler tamamen cihazınızda yapılır, veri dışarı çıkmaz."
                                AiOperationMode.HYBRID -> "Önce yerel analiz denenir, yetersiz kalırsa bulut kullanılır."
                            },
                            fontSize = 11.sp,
                            color = SubText
                        )
                    }
                    if (isSelected) {
                        Icon(Icons.Default.CheckCircle, null, tint = PrimaryTeal)
                    }
                }
            }
        }
    }
}

@Composable
fun ModelManagementTab(state: AiEngineUiState, viewModel: AiEngineViewModel) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Mevcut Yerel Modeller", fontWeight = FontWeight.Bold, color = PrimaryTeal)
        }
        if (state.availableModels.isEmpty()) {
            item {
                Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text("Henüz yüklü model bulunmuyor.", color = SubText)
                }
            }
        }
        items(state.availableModels) { model ->
            ModelCard(
                model = model,
                onDownload = { viewModel.downloadModel(model.modelId) },
                onDelete = { viewModel.deleteModel(model.modelId) },
                onActivate = { viewModel.setActiveModel(model.modelId) }
            )
        }
    }
}

@Composable
fun ModelCard(
    model: LocalAiModel,
    onDownload: () -> Unit,
    onDelete: () -> Unit,
    onActivate: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardNew)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(model.name, fontWeight = FontWeight.Bold)
                    Text("v${model.version} • ${model.sizeMb} MB", fontSize = 10.sp, color = SubText)
                }
                if (!model.isDownloaded) {
                    IconButton(onClick = onDownload) {
                        Icon(Icons.Default.CloudDownload, null, tint = PrimaryTeal)
                    }
                } else {
                    Row {
                        IconButton(onClick = onActivate) {
                            Icon(Icons.Default.PlayCircle, null, tint = EmeraldNew)
                        }
                        IconButton(onClick = onDelete) {
                            Icon(Icons.Default.Delete, null, tint = NegatifRed)
                        }
                    }
                }
            }
            if (model.isDownloaded) {
                Spacer(Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    ModelMetric("RAM", "${model.ramUsageMb}MB")
                    ModelMetric("CPU", "%${model.cpuUsagePct}")
                }
            }
        }
    }
}

@Composable
fun ModelMetric(label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(label, fontSize = 10.sp, color = SubText)
        Spacer(Modifier.width(4.dp))
        Text(value, fontWeight = FontWeight.Bold, fontSize = 11.sp, fontFamily = IBMPlexMono)
    }
}

@Composable
fun QualityControlTab(state: AiEngineUiState) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        state.qualityMetrics?.let { metrics ->
            item {
                Card(colors = CardDefaults.cardColors(containerColor = PrimaryTeal.copy(alpha = 0.05f))) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("AI Kalite Denetimi", fontWeight = FontWeight.Bold, color = PrimaryTeal)
                        Spacer(Modifier.height(12.dp))
                        QualityMetricRow("Sonuç Benzerliği", metrics.similarityScore)
                        QualityMetricRow("Tutarlılık Oranı", metrics.consistencyRate)
                        QualityMetricRow("Yerel Güven Skoru", metrics.localConfidence / 100.0)
                    }
                }
            }
        }
    }
}

@Composable
fun QualityMetricRow(label: String, value: Double) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, fontSize = 12.sp)
            Text("%${(value * 100).toInt()}", fontWeight = FontWeight.Bold, fontSize = 12.sp, fontFamily = IBMPlexMono)
        }
        LinearProgressIndicator(
            progress = value.toFloat(),
            modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
            color = if (value > 0.8) EmeraldNew else if (value > 0.5) Orange else NegatifRed,
            trackColor = Color.LightGray.copy(alpha = 0.3f)
        )
    }
}

@Composable
fun EngineStatsTab(state: AiEngineUiState) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("AI Motor kullanım istatistikleri yakında eklenecek.", color = SubText)
    }
}
