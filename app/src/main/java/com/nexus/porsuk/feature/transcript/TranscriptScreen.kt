package com.nexus.porsuk.feature.transcript

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
 * Earnings Call & Transcripts Intelligence Platform — Ana Ekran (TranscriptScreen)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TranscriptScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: TranscriptViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Earnings Call & Transcripts Intelligence", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = "${uiState.selectedSymbol} • ${uiState.currentCall?.period ?: "2026-Q2"} Transcript",
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
                // Toplantı Arşivi Seçici (LazyRow)
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.recentCalls) { call ->
                        FilterChip(
                            selected = uiState.selectedCallId == call.callId,
                            onClick = { viewModel.selectCall(call.callId) },
                            label = { Text("${call.callType.iconEmoji} ${call.period}") }
                        )
                    }
                }

                // Platform Tab Row
                ScrollableTabRow(
                    selectedTabIndex = uiState.activeTab.ordinal,
                    edgePadding = 16.dp
                ) {
                    TranscriptTab.entries.forEach { tab ->
                        Tab(
                            selected = uiState.activeTab == tab,
                            onClick = { viewModel.selectTab(tab) },
                            text = { Text("${tab.iconEmoji} ${tab.title}") }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                when (uiState.activeTab) {
                    TranscriptTab.TRANSCRIPT_VIEWER -> {
                        TranscriptViewerTabContent(uiState = uiState)
                    }
                    TranscriptTab.MANAGEMENT_ANALYSIS -> {
                        ManagementTabContent(uiState = uiState)
                    }
                    TranscriptTab.QNA_INTELLIGENCE -> {
                        QnaTabContent(uiState = uiState)
                    }
                    TranscriptTab.AI_INTELLIGENCE -> {
                        AiTabContent(uiState = uiState)
                    }
                    TranscriptTab.SEARCH_ANALYTICS -> {
                        SearchTabContent(uiState = uiState, viewModel = viewModel)
                    }
                }
            }
        }
    }
}

@Composable
private fun TranscriptViewerTabContent(uiState: TranscriptUiState) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            SpeakerTalkTimeDistributionCard(visuals = uiState.visuals)
        }

        item {
            Text("🎙️ Transkript Konuşma Akışı (Transcript Stream)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        }

        items(uiState.utterances) { utt ->
            TranscriptUtteranceCard(utterance = utt)
        }
    }
}

@Composable
private fun ManagementTabContent(uiState: TranscriptUiState) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        uiState.managementAnalysis?.let { mgmt ->
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("👔 CEO Açılış Konuşması & Stratejik Özet", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(mgmt.ceoOpeningStatementSummary, style = MaterialTheme.typography.bodySmall)

                        Spacer(modifier = Modifier.height(12.dp))
                        Text("📊 CFO Finansal Sunum Özeti", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(mgmt.cfoFinancialReviewSummary, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.25f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("📈 İleriye Dönük Beklentiler (Guidance Statements)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        mgmt.guidanceStatements.forEach { g ->
                            Text("• $g", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text("🚀 Stratejik Hedefler:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        mgmt.forwardLookingStatements.forEach { f ->
                            Text("• $f", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QnaTabContent(uiState: TranscriptUiState) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("💬 Analist Soru - Cevap Bölümü (Q&A Intelligence)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        }

        items(uiState.qnaExchanges) { qna ->
            QnaExchangeCard(qna = qna)
        }
    }
}

@Composable
private fun AiTabContent(uiState: TranscriptUiState) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            ManagementConfidenceGaugeCard(aiSummary = uiState.aiSummary)
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("🚀 Geleceğe Hazır Transkript Suite Status", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("• Voice-to-Text Speech Engine: ${if (uiState.futureStubs.isVoiceToTextActive) "Aktif 🟢" else "Pasif (Hazır)"}", style = MaterialTheme.typography.bodySmall)
                    Text("• Live Earnings Call Streaming: ${if (uiState.futureStubs.isLiveCallActive) "Aktif 🟢" else "Pasif (Hazır)"}", style = MaterialTheme.typography.bodySmall)
                    Text("• Real-Time AI Summary: ${if (uiState.futureStubs.isRealtimeAiSummaryActive) "Aktif 🟢" else "Pasif (Hazır)"}", style = MaterialTheme.typography.bodySmall)
                    Text("• LLM Q&A Assistant: ${if (uiState.futureStubs.isLlmQnaAssistantActive) "Aktif 🟢" else "Pasif"}", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
private fun SearchTabContent(
    uiState: TranscriptUiState,
    viewModel: TranscriptViewModel
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Transkript İçi Kelime / Konu Ara (Örn: Marj, Gelir, Yakıt)") },
                singleLine = true
            )
        }

        if (uiState.searchResults.isNotEmpty()) {
            item {
                Text("🔍 Arama Sonuçları (${uiState.searchResults.size} Eşleşme)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            }

            items(uiState.searchResults) { res ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("${res.matchedUtterance.speaker.name} (${res.matchedUtterance.timeLabel})", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(res.highlightedSnippet, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}
