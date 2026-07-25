package com.nexus.porsuk.feature.quant

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nexus.porsuk.domain.model.*

/**
 * Porsuk Quant Research Studio — Ana Ekran (QuantResearchScreen)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuantResearchScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: QuantResearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Quantitative AI Research & Alpha Factory", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = uiState.activeWorkspace.title,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Platform Tab Seçici (ScrollableTabRow)
                ScrollableTabRow(
                    selectedTabIndex = uiState.activeTab.ordinal,
                    edgePadding = 16.dp
                ) {
                    QuantPlatformTab.entries.forEach { tab ->
                        Tab(
                            selected = uiState.activeTab == tab,
                            onClick = { viewModel.selectTab(tab) },
                            text = { Text("${tab.iconEmoji} ${tab.title}") }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                when (uiState.activeTab) {
                    QuantPlatformTab.ALPHA_FACTORY -> {
                        AlphaFactoryTabContent(uiState = uiState, viewModel = viewModel)
                    }
                    QuantPlatformTab.ACADEMIC_MODELS -> {
                        AcademicModelsTabContent(uiState = uiState, viewModel = viewModel)
                    }
                    QuantPlatformTab.VALIDATION_ANALYTICS -> {
                        ValidationAndAnalyticsTabContent(uiState = uiState, viewModel = viewModel)
                    }
                    QuantPlatformTab.FEATURE_ML_STORE -> {
                        FeatureStoreAndMlTabContent(uiState = uiState, viewModel = viewModel)
                    }
                    QuantPlatformTab.EXPERIMENTS_NOTEBOOK -> {
                        ExperimentsAndNotebookTabContent(uiState = uiState, viewModel = viewModel)
                    }
                }
            }
        }
    }
}

/**
 * Tab 1: Alpha Factory & Multi-Factor Engine Content
 */
@Composable
private fun AlphaFactoryTabContent(
    uiState: QuantResearchUiState,
    viewModel: QuantResearchViewModel
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("10 Multi-Factor Kategorisi:", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        selected = uiState.selectedFactorCategory == null,
                        onClick = { viewModel.selectFactorCategory(null) },
                        label = { Text("Tümü") }
                    )
                }
                items(FactorCategory.entries) { cat ->
                    FilterChip(
                        selected = uiState.selectedFactorCategory == cat,
                        onClick = { viewModel.selectFactorCategory(cat) },
                        label = { Text("${cat.iconEmoji} ${cat.displayName}") }
                    )
                }
            }
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("⚙️ Faktör Kombinasyon Stratejisi", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(FactorCombinationStrategy.entries) { strat ->
                            FilterChip(
                                selected = uiState.selectedCombinationStrategy == strat,
                                onClick = { viewModel.changeCombinationStrategy(strat) },
                                label = { Text(strat.displayName) }
                            )
                        }
                    }
                }
            }
        }

        item {
            Text("🏆 Kompozit Alfa Skorları (Composite Alpha Scores)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        }

        items(uiState.combinationResults) { res ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(res.symbol, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text("Strateji: ${res.strategyUsed.displayName}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = "Alfa Skoru: ${res.compositeAlphaScore}",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        item {
            Text("📚 Alfa Faktör Kütüphanesi", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        }

        val filteredFactors = uiState.factorMetrics.filter {
            uiState.selectedFactorCategory == null || it.category == uiState.selectedFactorCategory
        }

        items(filteredFactors) { factor ->
            FactorMetricCard(factor = factor)
        }
    }
}

/**
 * Tab 2: Academic Models Content
 */
@Composable
private fun AcademicModelsTabContent(
    uiState: QuantResearchUiState,
    viewModel: QuantResearchViewModel
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("🎓 Akademik Faktör Modelleri (Academic Factor Models)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(AcademicModelType.entries) { modelType ->
                    FilterChip(
                        selected = uiState.selectedAcademicModel == modelType,
                        onClick = { viewModel.selectAcademicModel(modelType) },
                        label = { Text(modelType.code) }
                    )
                }
            }
        }

        uiState.academicModelResult?.let { res ->
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("${res.modelType.title} (${res.modelType.code})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(res.modelType.formulaDesc, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)

                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Model Alfasını (α): +%${res.alphaPct}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Text("R²: ${res.rSquared} (Adj: ${res.adjustedRSquared})", style = MaterialTheme.typography.bodySmall)
                        }
                        Text("İstatistiki Anlamlılık: p-Value ${res.alphaPValue} (Anlamlı 🟢)", style = MaterialTheme.typography.labelSmall)

                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Faktör Betaları & İstatistikler:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)

                        res.factorBetas.forEach { beta ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("• ${beta.factorName}", style = MaterialTheme.typography.bodySmall)
                                Text("β: ${beta.betaValue} (t: ${beta.tStatistic})", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Tab 3: Validation & Analytics Content
 */
@Composable
private fun ValidationAndAnalyticsTabContent(
    uiState: QuantResearchUiState,
    viewModel: QuantResearchViewModel
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            WalkForwardValidationCard(wf = uiState.walkForwardResult)
        }

        item {
            FactorDecayAndTimelineCard(decay = uiState.factorDecay, persistence = uiState.factorPersistence)
        }

        uiState.correlationMatrix?.let { matrix ->
            item {
                FactorCorrelationMatrixHeatmap(matrix = matrix)
            }
        }

        item {
            PerformanceAttributionCard(attribution = uiState.performanceAttribution)
        }

        uiState.bootstrapResult?.let { bs ->
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("🎲 Bootstrap Resampling Simülasyonu", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Simülasyon Sayısı: ${bs.simulationsCount} • Ortalama Getiri: %${bs.meanReturnPct}", style = MaterialTheme.typography.bodySmall)
                        Text("%95 Güven Aralığı: [%${bs.confidenceInterval95Lower}, %${bs.confidenceInterval95Upper}]", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                        Text("Kayıp Olasılığı: %${bs.probabilityOfLossPct} 🟢", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}

/**
 * Tab 4: Feature Store & ML Tab Content
 */
@Composable
private fun FeatureStoreAndMlTabContent(
    uiState: QuantResearchUiState,
    viewModel: QuantResearchViewModel
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("🤖 Geleceğe Hazır AI & ML Suite Status", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("• AI Alpha Discovery Engine: ${if (uiState.futureSuite.isAiAlphaDiscoveryActive) "Aktif (Hazır) 🟢" else "Pasif"}", style = MaterialTheme.typography.bodySmall)
                    Text("• Auto Feature Engineering: ${if (uiState.futureSuite.isAutoFeatureEngineeringActive) "Aktif (Hazır) 🟢" else "Pasif"}", style = MaterialTheme.typography.bodySmall)
                    Text("• LLM Quant Research Assistant: ${if (uiState.futureSuite.isLlmResearchAssistantActive) "Aktif (Hazır) 🟢" else "Pasif"}", style = MaterialTheme.typography.bodySmall)
                    Text("• Institutional Factor Library: ${if (uiState.futureSuite.isInstitutionalFactorLibraryActive) "Aktif (Hazır) 🟢" else "Pasif"}", style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        item {
            Text("📦 Feature Store Tanımları", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        }

        items(uiState.featureDefinitions) { feat ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(feat.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Text("Dönüşüm: ${feat.transformationUsed.displayName} • Imputation: ${feat.missingStrategy.displayName}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        item {
            Text("🧠 Machine Learning Model Arayüzleri", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        }

        items(uiState.mlModelConfigs) { ml ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                onClick = { viewModel.evaluateMlModel(ml.modelId) }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(ml.modelName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Text("Görev: ${ml.taskType.title}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                    }
                    Button(onClick = { viewModel.evaluateMlModel(ml.modelId) }) {
                        Text("Test Et")
                    }
                }
            }
        }

        uiState.activeMlEvaluation?.let { eval ->
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("📊 ML Değerlendirme Sonucu (${eval.modelId})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text("R²/Doğruluk: ${eval.accuracyOrR2} • Hata (MAE): ${eval.maeOrLoss}", style = MaterialTheme.typography.bodySmall)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Özellik Önem Düzeyleri (Feature Importances):", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        eval.featureImportances.forEach { (feat, imp) ->
                            Text("  - $feat: %${imp * 100}", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}

/**
 * Tab 5: Experiments & Notebook Content
 */
@Composable
private fun ExperimentsAndNotebookTabContent(
    uiState: QuantResearchUiState,
    viewModel: QuantResearchViewModel
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            ResearchNotebookCard(
                workspace = uiState.activeWorkspace,
                onSaveNotes = { viewModel.saveWorkspaceNotes(it) }
            )
        }

        item {
            Text("📑 Kayıtlı Deneyler (Saved Experiments)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        }

        items(uiState.savedExperiments) { exp ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(exp.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Text(exp.notes, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Metrikler: ${exp.metrics}", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

@Composable
private fun FactorMetricCard(factor: FactorMetric) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${factor.category.iconEmoji} ${factor.name}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "Ham Değer: ${factor.rawValue}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(factor.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Z-Score: ${factor.zScore} • Yüzdelik Dilim: %${factor.percentileRank}",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun ResearchNotebookCard(
    workspace: ResearchWorkspace,
    onSaveNotes: (String) -> Unit
) {
    var notesText by remember(workspace.notebookNotes) { mutableStateOf(workspace.notebookNotes) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("📝 Quant Notebook & Araştırma Notları", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = notesText,
                onValueChange = { notesText = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Strateji Not Defteri / Hipotezler") },
                maxLines = 4
            )

            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { onSaveNotes(notesText) },
                modifier = Modifier.align(Alignment.End),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Notu Kaydet")
            }
        }
    }
}
