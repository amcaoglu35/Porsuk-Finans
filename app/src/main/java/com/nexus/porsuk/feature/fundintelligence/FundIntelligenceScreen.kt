package com.nexus.porsuk.feature.fundintelligence

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
fun FundIntelligenceScreen(
    fundCode: String,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Fund Intelligence", fontFamily = Manrope, fontWeight = FontWeight.Bold) },
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
                FundHeaderSection(fundCode)
            }
            
            item {
                FundPerformanceSection()
            }
            
            item {
                FundHoldingsSection()
            }
            
            item {
                FundRiskSection()
            }
            
            item {
                FundAiIntelligenceSection()
            }
        }
    }
}

@Composable
fun FundHeaderSection(code: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardNew)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = code, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold, color = PrimaryTeal, fontFamily = JetBrainsMono)
            Text(text = "Professional Analytics Dashboard", style = MaterialTheme.typography.bodySmall, color = SubText)
        }
    }
}

@Composable
fun FundPerformanceSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardNew)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Performance Intelligence", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                MetricBox(value = "+%2.4", label = "Daily", modifier = Modifier.weight(1f))
                MetricBox(value = "+%12.8", label = "YTD", modifier = Modifier.weight(1f))
                MetricBox(value = "+%45.2", label = "1Y", modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun FundHoldingsSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardNew)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Top Holdings", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(10.dp))
            // Example holdings
            listOf("Apple Inc" to "%12.4", "Microsoft" to "%8.2", "NVIDIA" to "%6.5").forEach { (name, weight) ->
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(name, style = MaterialTheme.typography.bodySmall, color = TextPrimary)
                    Text(weight, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = PrimaryTeal)
                }
            }
        }
    }
}

@Composable
fun FundRiskSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardNew)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Risk Intelligence", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MetricBox(value = "1.24", label = "Sharpe", modifier = Modifier.weight(1f))
                MetricBox(value = "0.85", label = "Beta", modifier = Modifier.weight(1f))
                MetricBox(value = "-%14", label = "Drawdown", accentColor = NegatifRed, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun FundAiIntelligenceSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PrimaryTeal.copy(alpha = 0.05f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryTeal.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🧠", fontSize = 20.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text("AI Insight Summary", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = PrimaryTeal)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                "Bu fon, düşük gider oranı ve teknoloji sektöründeki güçlü ağırlığı ile büyüme odaklı portföyler için idealdir. Ancak volatilite piyasa ortalamasının üzerindedir.",
                style = MaterialTheme.typography.bodySmall,
                color = InkText,
                lineHeight = 18.sp
            )
        }
    }
}
