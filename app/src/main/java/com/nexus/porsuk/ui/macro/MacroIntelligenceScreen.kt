package com.nexus.porsuk.ui.macro

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.ui.common.Sparkline
import com.nexus.porsuk.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MacroIntelligenceScreen(
    viewModel: MacroViewModel,
    onBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🌐 Makro Ekonomi & FRED Göstergeleri", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.loadMacroData() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Yenile", tint = PrimaryTeal)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundNew)
            )
        },
        containerColor = BackgroundNew
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = uiState.isLoading,
            onRefresh = { viewModel.loadMacroData() },
            modifier = Modifier.padding(padding)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(uiState.indicators, key = { it.seriesId }) { item ->
                    MacroIndicatorCard(item = item)
                }
            }
        }
    }
}

@Composable
fun MacroIndicatorCard(item: MacroIndicatorItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CardNew),
        border = BorderStroke(1.dp, LineBorder)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = PrimaryTeal.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = item.seriesId,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryTeal,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        fontFamily = JetBrainsMono
                    )
                }
            }

            Text(
                text = item.title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = InkText,
                fontFamily = Manrope,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 15.sp
            )

            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = item.currentValue,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = InkText,
                    fontFamily = IBMPlexMono
                )
                Text(
                    text = item.unit,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = SubText,
                    fontFamily = Manrope,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }

            if (item.historyPoints.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp)
                ) {
                    Sparkline(
                        values = item.historyPoints,
                        color = PrimaryTeal,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Text(
                text = item.description,
                fontSize = 9.5.sp,
                color = SubText,
                fontFamily = Manrope,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 13.sp
            )
        }
    }
}
