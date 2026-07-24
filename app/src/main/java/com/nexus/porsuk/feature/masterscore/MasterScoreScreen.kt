package com.nexus.porsuk.feature.masterscore

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
import com.nexus.porsuk.feature.masterscore.components.MasterScoreRingCard
import com.nexus.porsuk.feature.masterscore.components.SubScoresGridCard

/**
 * Porsuk Master Score Engine — Ana Ekran (MasterScoreScreen)
 *
 * 0-100 arasında genel Master Skorunu, 7 dereceli skor seviyesini ve 8 alt skor bileşenini sunar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MasterScoreScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: MasterScoreViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Master Score (${uiState.symbol})",
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
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(modifier = Modifier.height(4.dp))

                // 1. Dairesel 0-100 Master Skor ve Rozet Kartı
                MasterScoreRingCard(result = uiState.scoreResult)

                // 2. Geleceğe Hazır Orakul AI Recommendation Stub Kartı
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.35f)
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "🤖 Orakul AI Sektörel Sıralama & Tavsiye",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Sektör İçi Sıralama: #3 / 48 Şirket (Sektör Lideri)\nOrakul AI: Yüksek Master Score ve güçlü bilanço rasyoları nedeniyle portföy birikimine uygundur.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }

                // 3. 8 Alt Skor Bileşeni Izgarası
                SubScoresGridCard(result = uiState.scoreResult)

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
