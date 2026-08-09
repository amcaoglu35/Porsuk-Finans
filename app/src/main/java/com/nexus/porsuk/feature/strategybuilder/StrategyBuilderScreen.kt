package com.nexus.porsuk.feature.strategybuilder

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nexus.porsuk.feature.strategybuilder.components.*

/**
 * Porsuk Strategy Builder Pro — Görsel Kodsuz Strateji Ekranı (StrategyBuilderScreen)
 *
 * 12 Strateji türünü, 10 şablonu, Plugin Architecture düğümlerini ve kural doğrulamasını sunar.
 */
import com.nexus.porsuk.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StrategyBuilderScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: StrategyBuilderViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Strategy Builder Pro",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Geri"
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 1. 12 Strateji Türü Seçim Barı
            StrategyTypeChips(
                selectedType = uiState.selectedType,
                onTypeSelected = { viewModel.selectType(it) }
            )

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Spacer(modifier = Modifier.height(2.dp))

                    // 2. Kural Doğrulama Kartı
                    StrategyValidationBadgeCard(validationResult = uiState.validationResult)

                    // 3. Hazır Strateji Şablonları Listesi
                    Text(
                        text = "🏛️ Örnek Strateji Şablonları",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    uiState.templates.forEach { tpl ->
                        StrategyTemplateCard(
                            template = tpl,
                            onClick = { viewModel.selectTemplate(tpl) }
                        )
                    }

                    // 4. Geleceğe Hazır Orakul AI Natural Language Strategy Builder Stub Kartı
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "🗣️ Orakul AI Doğal Dil ile Strateji Oluşturucu",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Orakul AI: 'Warren Buffett tarzı ucuz ve yüksek kârlı hisseleri bul' şeklinde cümlenizi otomatik strateji düğümlerine dönüştürür.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}
