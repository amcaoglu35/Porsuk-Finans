package com.nexus.porsuk.ui.orakul

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.ui.theme.*

import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.geometry.Offset
import com.nexus.porsuk.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KaziAnalysisScreen(
    viewModel: KaziViewModel,
    onBack: () -> Unit,
    onFinished: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val latestRun = uiState.latestRun

    // Navigate to results when completed
    LaunchedEffect(latestRun?.status) {
        if (latestRun?.status == "COMPLETED") {
            onFinished()
        }
    }

    val tickers = listOf("THYAO", "TUPRS", "EREGL", "SASA", "ASELS", "FROTO", "BIMAS", "KCHOL", "YKBNK", "GARAN", "AKBNK", "SAHOL", "ISCTR", "SISE", "PETKM", "TCELL", "PGSUS", "TOASO", "ODAS", "HEKTS", "KOZAL", "MGROS", "ENJSA", "ARCLK", "GUBRF", "VESTL", "KARDMD", "SOKM", "TKFEN")
    var tickerIndex by remember { mutableStateOf(0) }
    LaunchedEffect(latestRun?.status) {
        if (latestRun?.status == "RUNNING") {
            while (true) {
                kotlinx.coroutines.delay(180)
                tickerIndex = (tickerIndex + 1) % tickers.size
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kazı Yapılıyor", fontFamily = Manrope, fontWeight = FontWeight.Bold, color = InkText) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri", tint = PrimaryTeal)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundNew)
            )
        },
        containerColor = BackgroundNew
    ) { padding ->
        val infiniteTransition = rememberInfiniteTransition(label = "pulse")
        val pulseScale by infiniteTransition.animateFloat(
            initialValue = 1f, targetValue = 1.1f,
            animationSpec = infiniteRepeatable(tween(1000), RepeatMode.Reverse), label = "pulse"
        )

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // Radar Sweep Animation
            if (latestRun?.status == "RUNNING") {
                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .clip(CircleShape)
                        .background(PrimaryTeal.copy(alpha = 0.04f))
                        .border(1.dp, PrimaryTeal.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    val sweepAngle by infiniteTransition.animateFloat(
                        initialValue = 0f,
                        targetValue = 360f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(2200, easing = LinearEasing),
                            repeatMode = RepeatMode.Restart
                        ),
                        label = "radar"
                    )
                    
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val center = Offset(size.width / 2f, size.height / 2f)
                        val radius = size.width / 2f
                        
                        // Circular grids
                        drawCircle(color = PrimaryTeal.copy(alpha = 0.15f), radius = radius, style = Stroke(1.dp.toPx()))
                        drawCircle(color = PrimaryTeal.copy(alpha = 0.1f), radius = radius * 0.65f, style = Stroke(1.dp.toPx()))
                        drawCircle(color = PrimaryTeal.copy(alpha = 0.05f), radius = radius * 0.35f, style = Stroke(1.dp.toPx()))
                        drawCircle(color = PrimaryTeal, radius = 4.dp.toPx())
                        
                        // Cross lines
                        drawLine(color = PrimaryTeal.copy(alpha = 0.1f), start = Offset(0f, center.y), end = Offset(size.width, center.y), strokeWidth = 1.dp.toPx())
                        drawLine(color = PrimaryTeal.copy(alpha = 0.1f), start = Offset(center.x, 0f), end = Offset(center.x, size.height), strokeWidth = 1.dp.toPx())
                        
                        // Sweep line
                        val angleRad = Math.toRadians(sweepAngle.toDouble())
                        val lineEnd = Offset(
                            x = center.x + radius * Math.cos(angleRad).toFloat(),
                            y = center.y + radius * Math.sin(angleRad).toFloat()
                        )
                        drawLine(
                            color = PrimaryTeal,
                            start = center,
                            end = lineEnd,
                            strokeWidth = 2.dp.toPx()
                        )
                    }

                    // Ticker name in the center
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = tickers[tickerIndex],
                            fontFamily = JetBrainsMono,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp,
                            color = PrimaryTeal
                        )
                        Text(
                            text = "TARANIYOR",
                            fontFamily = Manrope,
                            fontSize = 7.sp,
                            fontWeight = FontWeight.Bold,
                            color = SubText.copy(alpha = 0.7f),
                            letterSpacing = 1.5.sp
                        )
                    }
                }
            }

            val steps = listOf(
                "Piyasa Evreni Taranıyor (K)",
                "Haberler & KAP İnceleniyor (A)",
                "Zamanlama & Momentum Ölçülüyor (Z)",
                "Makro Senaryo Sentezleniyor (İ)",
                "Sepet Optimizasyonu Yapılıyor",
                "Gerekçe Raporu Yazılıyor"
            )

            val currentStep = latestRun?.currentStep ?: 0

            Column(verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
                steps.forEachIndexed { index, title ->
                    val stepIndex = index + 1
                    val isCompleted = stepIndex < currentStep || (latestRun?.status == "COMPLETED")
                    val isActive = stepIndex == currentStep && latestRun?.status == "RUNNING"

                    AnalysisStepRow(
                        title = title,
                        isCompleted = isCompleted,
                        isActive = isActive,
                        pulseScale = if (isActive) pulseScale else 1f
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            if (latestRun?.status == "FAILED") {
                Card(colors = CardDefaults.cardColors(containerColor = NegatifRed.copy(alpha = 0.1f))) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = NegatifRed)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Kazı işlemi başarısız oldu. Lütfen tekrar deneyin.", color = NegatifRed, fontFamily = Manrope)
                    }
                }
            }

            Text(
                "Bu ekranı kapatabilirsin, bittiğinde bildirim göndeririz.",
                style = MaterialTheme.typography.bodySmall,
                color = SubText,
                textAlign = TextAlign.Center,
                fontFamily = Manrope
            )

            Button(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CardNew),
                border = androidx.compose.foundation.BorderStroke(1.dp, LineBorder)
            ) {
                Text("Arka Plana Al", color = PrimaryTeal, fontWeight = FontWeight.Bold, fontFamily = Manrope)
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun AnalysisStepRow(
    title: String,
    isCompleted: Boolean,
    isActive: Boolean,
    pulseScale: Float
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isActive) AquaSoft.copy(alpha = 0.5f) else Color.Transparent)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(if (isCompleted) PrimaryTeal else if (isActive) PrimaryTeal.copy(alpha = 0.2f) else LineBorder)
                .wrapContentSize(Alignment.Center)
        ) {
            if (isCompleted) {
                Icon(Icons.Default.Check, null, modifier = Modifier.size(14.dp), tint = Color.White)
            } else if (isActive) {
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(PrimaryTeal))
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isCompleted || isActive) InkText else SubText,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
            fontFamily = Manrope,
            modifier = if (isActive) Modifier.padding(start = (10 * (pulseScale - 1f)).dp) else Modifier
        )
    }
}
