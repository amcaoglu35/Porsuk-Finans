package com.nexus.porsuk.feature.ipo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.ui.theme.*
import com.nexus.porsuk.ui.common.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IpoIntelligenceScreen(
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("IPO Intelligence", fontFamily = Manrope, fontWeight = FontWeight.Bold) },
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
                IpoDashboardSummary()
            }
            
            item {
                SectionHeader("Upcoming IPOs")
            }
            
            // Mock items
            items(listOf("PORSUK", "NEXUS", "AI_CORP")) { symbol ->
                IpoCalendarItem(symbol)
            }
            
            item {
                SectionHeader("IPO Pricing & Allocation")
            }
            
            item {
                IpoPricingCard("PORSUK", 15.40, 25000000)
            }

            item {
                SectionHeader("IPO Prospectus & Analysis")
            }
            
            item {
                IpoAiAnalysisCard("PORSUK")
            }
        }
    }
}

@Composable
fun IpoPricingCard(symbol: String, price: Double, totalLots: Long) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardNew)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Pricing Structure: $symbol", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Base Price", fontSize = 10.sp, color = SubText)
                    Text("${price} TL", fontWeight = FontWeight.Bold, fontFamily = IBMPlexMono)
                }
                Column(horizontalAlignment = androidx.compose.ui.Alignment.End) {
                    Text("Issue Size", fontSize = 10.sp, color = SubText)
                    Text("${totalLots * price / 1000000}M TL", fontWeight = FontWeight.Bold, fontFamily = IBMPlexMono)
                }
            }
        }
    }
}

@Composable
fun IpoAiAnalysisCard(symbol: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PrimaryTeal.copy(alpha = 0.05f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryTeal.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Text("🧠", fontSize = 20.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Orakul IPO Analysis", fontWeight = FontWeight.Bold, color = PrimaryTeal)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                "Bu halka arzda %25 iskonto oranı öngörülmektedir. Sektörel çarpanlara göre fiyat cazip görünmektedir. Dağıtım eşit olacağı için bireysel yatırımcı ilgisi yüksek olabilir.",
                fontSize = 11.sp,
                lineHeight = 16.sp,
                color = InkText
            )
        }
    }
}

@Composable
fun IpoDashboardSummary() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardNew)
    ) {
        Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MetricBox(value = "5", label = "Upcoming", modifier = Modifier.weight(1f))
            MetricBox(value = "2", label = "Active", accentColor = Orange, modifier = Modifier.weight(1f))
            MetricBox(value = "12", label = "Total 2026", modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun IpoCalendarItem(symbol: String, status: String = "UPCOMING") {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardNew)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = symbol, fontWeight = FontWeight.Bold, fontFamily = JetBrainsMono, color = PrimaryTeal)
                Badge(containerColor = if(status == "ACTIVE") Orange else SubText) {
                    Text(status, color = Color.White, fontSize = 10.sp, modifier = Modifier.padding(2.dp))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Tech & Finance Solutions Inc.", style = MaterialTheme.typography.bodyMedium, color = InkText)
            Spacer(modifier = Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Price: 15.40 TL", fontSize = 11.sp, color = SubText, fontFamily = IBMPlexMono)
                Text("Date: 12-14 May", fontSize = 11.sp, color = SubText, fontFamily = IBMPlexMono)
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = PrimaryTeal,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = 1.2.sp,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}
