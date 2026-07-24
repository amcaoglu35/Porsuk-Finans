package com.nexus.porsuk.feature.dividend

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.nexus.porsuk.feature.dividend.components.DividendStockCard
import com.nexus.porsuk.feature.dividend.components.IncomeProjectionCard

/**
 * Porsuk Dividend Intelligence Center — Ana Ekran (DividendScreen)
 *
 * 5 Temettü kalite skorunu (Safety, Growth, Consistency), pasif gelir tahminlerini ve temettü takvimini sunar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DividendScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: DividendViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Dividend Intelligence Center",
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
            // 1. Pasif Gelir & Temettü Özeti (Income Projection Card)
            IncomeProjectionCard(
                projection = uiState.projection,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // 2. Geleceğe Hazır Orakul AI Dividend Forecast & Passive Income Planner Stub Kartı
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
                )
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = "💡 Orakul AI Pasif Gelir Planlayıcı",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Orakul AI: Temettü gelirlerinizi otomatik DRIP ile yeniden yatırıma yönlendirerek 5 yılda bileşik büyümenizi %42 artırabilirsiniz.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }

            // 3. Temettü Hisseleri Akış Listesi
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else if (uiState.stocks.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Temettü hissesi bulunamadı.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(
                            items = uiState.stocks,
                            key = { it.symbol }
                        ) { item ->
                            DividendStockCard(item = item)
                        }
                    }
                }
            }
        }
    }
}
