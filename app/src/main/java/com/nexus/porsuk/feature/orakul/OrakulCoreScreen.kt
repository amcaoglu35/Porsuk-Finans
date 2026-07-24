package com.nexus.porsuk.feature.orakul

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
import com.nexus.porsuk.feature.orakul.components.*

/**
 * Porsuk Orakul Core Engine — Ana Ekran (OrakulCoreScreen)
 *
 * 8 Bağımsız analiz motorunun sonuçlarını ve standart Orakul Analiz Raporunu sunar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrakulCoreScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: OrakulCoreViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Orakul Core (${uiState.symbol})",
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

                // 1. Orakul Genel Durum Özet Kartı
                AnalysisReportCard(report = uiState.report)

                // 2. 12 Modüler Teknik Gösterge Rozet Kartı
                TechnicalIndicatorsChipGrid(technicalData = uiState.technicalData)

                // 3. Standart Rapor: Güçlü/Zayıf Yönler, Riskler, Fırsatlar & İzlenecek Noktalar
                StrengthsWeaknessesCard(report = uiState.report)

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
