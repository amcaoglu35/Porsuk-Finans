package com.nexus.porsuk.feature.ipo

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.ui.theme.*
import com.nexus.porsuk.ui.common.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CorporateActionsScreen(
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Corporate Actions", fontFamily = Manrope, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundNew)
            )
        },
        containerColor = BackgroundNew
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                SectionHeader("Upcoming Dividends")
            }
            
            items(listOf("THYAO", "EREGL", "TUPRS")) { symbol ->
                DividendActionItem(symbol)
            }
            
            item {
                SectionHeader("Stock Splits & Capital Increases")
            }
            
            items(listOf("KCHOL", "SAHOL")) { symbol ->
                SplitActionItem(symbol)
            }

            item {
                SectionHeader("M&A, Spin-Offs & Others")
            }
            
            item {
                CorporateActionHistoryItem("TCELL", "Acquisition", "Vodafone buyback rumor", "PENDING")
            }
        }
    }
}

@Composable
fun CorporateActionHistoryItem(symbol: String, type: String, detail: String, status: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardNew)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = symbol, fontWeight = FontWeight.Bold, fontFamily = JetBrainsMono)
                Badge(containerColor = if(status == "PENDING") Orange else PrimaryTeal) {
                    Text(status, color = Color.White, fontSize = 9.sp, modifier = Modifier.padding(2.dp))
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = type, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryTeal)
            Text(text = detail, fontSize = 11.sp, color = SubText)
        }
    }
}

@Composable
fun DividendActionItem(symbol: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardNew)
    ) {
        Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text(text = symbol, fontWeight = FontWeight.Bold, fontFamily = JetBrainsMono)
                Text("Cash Dividend", fontSize = 11.sp, color = SubText)
            }
            Column(horizontalAlignment = androidx.compose.ui.Alignment.End) {
                Text("1.25 TL", fontWeight = FontWeight.Bold, color = PrimaryTeal, fontFamily = IBMPlexMono)
                Text("Yield: %5.4", fontSize = 10.sp, color = SubText, fontFamily = IBMPlexMono)
            }
        }
    }
}

@Composable
fun SplitActionItem(symbol: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardNew)
    ) {
        Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text(text = symbol, fontWeight = FontWeight.Bold, fontFamily = JetBrainsMono)
                Text("Stock Split (Bonus)", fontSize = 11.sp, color = SubText)
            }
            Text("100%", fontWeight = FontWeight.Bold, color = Orange, fontFamily = IBMPlexMono)
        }
    }
}
