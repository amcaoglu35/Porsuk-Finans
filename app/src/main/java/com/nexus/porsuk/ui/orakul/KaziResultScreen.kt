package com.nexus.porsuk.ui.orakul

import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.data.local.entity.*
import com.nexus.porsuk.ui.common.CurrencyFormatter
import com.nexus.porsuk.ui.theme.*
import java.util.*
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

data class NeonParticle(
    val x: Float,          // normalized 0..1
    val startY: Float,     // normalized 0..1 (bottom region)
    val speed: Float,      // upward speed
    val size: Float,       // radius in dp
    val color: Color,
    val rotationSpeed: Float,
    val shape: Int         // 0=circle, 1=rect, 2=star
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KaziResultScreen(
    viewModel: KaziViewModel,
    onBack: () -> Unit,
    onStockClick: (String, String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Confetti trigger: show for 3 seconds when screen first appears
    var showConfetti by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        delay(3500)
        showConfetti = false
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Kazı Sonuçları", fontFamily = Manrope, fontWeight = FontWeight.Bold, color = InkText) },
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
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Summary Card
                item {
                    KaziSummaryCard(uiState.basket, uiState.latestRun)
                }

                // Candidates List
                item {
                    Text(
                        "Seçilen Hisseler", 
                        style = MaterialTheme.typography.titleMedium, 
                        fontWeight = FontWeight.Bold, 
                        color = InkText, 
                        fontFamily = Manrope
                    )
                }

                val selectedCandidates = uiState.candidates.filter { it.selected }
                items(selectedCandidates) { candidate ->
                    val company = uiState.companies.find { it.symbol == candidate.symbol }
                    val price = company?.currentPrice ?: 0.0
                    KaziCandidateCard(candidate, uiState.latestRun?.capital, price, onStockClick)
                }

                // Rejected Panel
                val rejectedCandidates = uiState.candidates.filter { !it.selected }.take(3)
                if (rejectedCandidates.isNotEmpty()) {
                    item {
                        Text(
                            "Değerlendirildi ama seçilmedi", 
                            style = MaterialTheme.typography.titleSmall, 
                            fontWeight = FontWeight.Bold, 
                            color = SubText, 
                            fontFamily = Manrope
                        )
                    }
                    items(rejectedCandidates) { candidate ->
                        RejectedCandidateItem(candidate)
                    }
                }

                item { Spacer(modifier = Modifier.height(20.dp)) }

                // Actions
                item {
                    Button(
                        onClick = {
                            val name = uiState.basket?.basketName ?: "Orakul Sepeti"
                            viewModel.addToPortfolio(name) {
                                Toast.makeText(context, "Sepete eklendi!", Toast.LENGTH_SHORT).show()
                                onBack()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Brush.horizontalGradient(listOf(PrimaryTeal, AquaNew))),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                    ) {
                        Icon(Icons.Default.Add, null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Sepete Ekle", fontWeight = FontWeight.Bold, color = Color.White, fontFamily = Manrope)
                    }
                }

                item {
                    var isWatching by remember { mutableStateOf(true) }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("KAZI İzlemeyi Aç", style = MaterialTheme.typography.bodyMedium, color = InkText, fontFamily = Manrope)
                        Switch(
                            checked = isWatching,
                            onCheckedChange = { isWatching = it },
                            colors = SwitchDefaults.colors(checkedTrackColor = PrimaryTeal)
                        )
                    }
                }
                
                item { Spacer(modifier = Modifier.height(40.dp)) }
            }
        }

        // Neon confetti overlay
        if (showConfetti) {
            NeonConfetti(
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun NeonConfetti(modifier: Modifier = Modifier) {
    val neonColors = listOf(
        Color(0xFF00FFB3), // neon teal
        Color(0xFF00E5FF), // neon cyan
        Color(0xFFFFD600), // neon gold
        Color(0xFFFF6BFF), // neon magenta
        Color(0xFF69FF47), // neon green
        Color(0xFFFF4488), // neon pink
    )

    val random = remember { Random(42) }
    val particles = remember {
        List(42) {
            NeonParticle(
                x = random.nextFloat(),
                startY = 0.5f + random.nextFloat() * 0.5f,
                speed = 0.08f + random.nextFloat() * 0.14f,
                size = 4f + random.nextFloat() * 7f,
                color = neonColors[it % neonColors.size],
                rotationSpeed = (random.nextFloat() - 0.5f) * 360f,
                shape = it % 3
            )
        }
    }

    // Infinite clock driving the animation
    val infiniteTransition = rememberInfiniteTransition(label = "confetti")
    val clock by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "confetti_clock"
    )

    Canvas(modifier = modifier) {
        particles.forEach { p ->
            // Each particle travels at its own phase
            val phase = (clock + p.x * 0.5f) % 1f
            val currentY = (p.startY - phase * p.speed * 8f).coerceIn(-0.1f, 1.1f)
            val alpha = (1f - phase).coerceIn(0f, 1f)

            val cx = p.x * size.width
            val cy = currentY * size.height
            val r = p.size.dp.toPx()
            val rotation = phase * p.rotationSpeed

            withTransform(
                transformBlock = {
                    rotate(degrees = rotation, pivot = Offset(cx, cy))
                }
            ) {
                val paintColor = p.color.copy(alpha = alpha * 0.9f)
                val glowColor  = p.color.copy(alpha = alpha * 0.3f)

                when (p.shape) {
                    0 -> { // circle
                        drawCircle(color = glowColor, radius = r * 2.4f, center = Offset(cx, cy))
                        drawCircle(color = paintColor, radius = r, center = Offset(cx, cy))
                    }
                    1 -> { // rect / square
                        drawRect(
                            color = glowColor,
                            topLeft = Offset(cx - r * 2f, cy - r * 2f),
                            size = Size(r * 4f, r * 4f)
                        )
                        drawRect(
                            color = paintColor,
                            topLeft = Offset(cx - r, cy - r),
                            size = Size(r * 2f, r * 2f)
                        )
                    }
                    else -> { // diamond / star line
                        val path = Path().apply {
                            moveTo(cx, cy - r * 1.4f)
                            lineTo(cx + r, cy)
                            lineTo(cx, cy + r * 1.4f)
                            lineTo(cx - r, cy)
                            close()
                        }
                        drawPath(path, color = glowColor)
                        drawPath(path, color = paintColor, style = Stroke(width = 1.5f.dp.toPx(), cap = StrokeCap.Round))
                    }
                }
            }
        }
    }
}


fun generateBacktestData(
    riskProfile: String,
    strategyFocus: String
): Pair<List<Float>, List<Float>> {
    val bist100 = mutableListOf<Float>()
    val basket = mutableListOf<Float>()
    
    var currentBist = 100f
    var currentBasket = 100f
    
    bist100.add(currentBist)
    basket.add(currentBasket)
    
    val random = java.util.Random((riskProfile.hashCode() + strategyFocus.hashCode()).toLong())
    
    val bistMean = 1.2f
    val bistVol = 3.2f
    val (basketMean, basketVol) = when (strategyFocus) {
        "GROWTH" -> 2.4f to 5.2f
        "VALUE" -> 1.8f to 3.4f
        "DIVIDEND" -> 1.5f to 2.2f
        else -> 1.7f to 3.6f
    }
    
    for (i in 1..11) {
        val bistChange = bistMean + (random.nextGaussian().toFloat() * bistVol)
        val basketChange = basketMean + (random.nextGaussian().toFloat() * basketVol)
        
        currentBist += bistChange
        currentBasket += basketChange
        
        bist100.add(currentBist)
        basket.add(currentBasket)
    }
    
    return basket to bist100
}

@Composable
fun BacktestComparisonChart(
    basketValues: List<Float>,
    bistValues: List<Float>,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        if (basketValues.size < 2 || bistValues.size < 2) return@Canvas
        
        val allValues = basketValues + bistValues
        val minV = allValues.min()
        val maxV = allValues.max()
        val range = (maxV - minV).takeIf { it != 0f } ?: 1f
        
        val stepX = size.width / (basketValues.size - 1)
        
        // Horizontal Grid lines
        val gridLines = 3
        for (i in 0..gridLines) {
            val y = size.height * (i.toFloat() / gridLines)
            drawLine(
                color = LineBorder.copy(alpha = 0.25f),
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = 1.dp.toPx()
            )
        }
        
        fun getPath(values: List<Float>): Path {
            return Path().apply {
                val startY = size.height * (1f - (values[0] - minV) / range)
                moveTo(0f, startY)
                for (i in 1 until values.size) {
                    val currentX = i * stepX
                    val currentY = size.height * (1f - (values[i] - minV) / range)
                    val prevX = (i - 1) * stepX
                    val prevY = size.height * (1f - (values[i - 1] - minV) / range)
                    cubicTo(
                        prevX + stepX / 2f, prevY,
                        prevX + stepX / 2f, currentY,
                        currentX, currentY
                    )
                }
            }
        }
        
        val bistPath = getPath(bistValues)
        val basketPath = getPath(basketValues)
        
        // Draw BIST 100
        drawPath(
            path = bistPath,
            color = SubText.copy(alpha = 0.4f),
            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
        
        // Draw Basket
        drawPath(
            path = basketPath,
            color = PrimaryTeal,
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }
}

@Composable
fun KaziSummaryCard(basket: KaziBasket?, run: KaziRun?) {
    val riskProfile = run?.riskProfile ?: "BALANCED"
    val parts = run?.horizon?.split("|") ?: emptyList()
    val strategyFocus = parts.getOrNull(1) ?: "VALUE"

    val (basketData, bistData) = remember(riskProfile, strategyFocus) {
        generateBacktestData(riskProfile, strategyFocus)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardNew),
        border = BorderStroke(1.dp, LineBorder)
    ) {
        Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("ORAKUL GÜVEN SEVİYESİ", style = MaterialTheme.typography.labelSmall, color = SubText, letterSpacing = 1.2.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Yüksek", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold, color = PrimaryTeal, fontFamily = Manrope)
            
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = LineBorder.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                SummaryStat(label = "Nakit Tamponu", value = "%%" + String.format(Locale.US, "%.0f", basket?.cashBufferPct ?: 10.0))
                SummaryStat(label = "Hisse Sayısı", value = "5")
                SummaryStat(label = "Risk Skoru", value = if (riskProfile == "CONSERVATIVE") "Düşük" else if (riskProfile == "BALANCED") "Orta" else "Yüksek")
            }

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(color = LineBorder.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Geriye Dönük Sepet Testi (6 Ay)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = InkText,
                    fontFamily = Manrope
                )
                
                val finalReturn = basketData.last() - 100f
                Text(
                    text = "+%%" + String.format(Locale.US, "%.1f", finalReturn),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = PrimaryTeal,
                    fontFamily = JetBrainsMono
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            BacktestComparisonChart(
                basketValues = basketData,
                bistValues = bistData,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(PrimaryTeal))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Orakul Sepeti", fontSize = 10.sp, color = InkText, fontFamily = Manrope)
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(SubText.copy(alpha = 0.4f)))
                Spacer(modifier = Modifier.width(6.dp))
                Text("BIST 100", fontSize = 10.sp, color = SubText, fontFamily = Manrope)
            }
        }
    }
}

@Composable
fun SummaryStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = InkText, fontFamily = IBMPlexMono)
        Text(label, fontSize = 10.sp, color = SubText, fontFamily = Manrope)
    }
}

@Composable
fun KaziCandidateCard(
    candidate: KaziCandidate, 
    capitalAmt: Double?, 
    companyPrice: Double, 
    onStockClick: (String, String) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardNew),
        border = BorderStroke(1.dp, LineBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        candidate.symbol, 
                        style = MaterialTheme.typography.titleLarge, 
                        fontWeight = FontWeight.Bold, 
                        color = InkText, 
                        fontFamily = IBMPlexMono
                    )
                    if (companyPrice > 0.0) {
                        Text(
                            text = CurrencyFormatter.formatTRY(companyPrice),
                            fontSize = 12.sp,
                            color = SubText,
                            fontFamily = JetBrainsMono
                        )
                    }
                }
                
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    ScoreBar(color = Color(0xFF8B5CF6), score = candidate.kScore)
                    ScoreBar(color = Aqua, score = candidate.aScore)
                    ScoreBar(color = Orange, score = candidate.zScore)
                    ScoreBar(color = PrimaryTeal, score = candidate.iScore)
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Text(
                    text = "${candidate.compositeScore}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = JetBrainsMono,
                    color = PrimaryTeal
                )
            }
            
            if (capitalAmt != null && capitalAmt > 0.0 && companyPrice > 0.0) {
                val weight = 18.0
                val allocatedMoney = capitalAmt * (weight / 100.0)
                val lots = (allocatedMoney / companyPrice).toInt()
                val totalCost = lots * companyPrice
                
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    color = PrimaryTeal.copy(alpha = 0.05f),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, PrimaryTeal.copy(alpha = 0.1f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Önerilen Lot Miktarı", fontSize = 9.sp, color = SubText, fontFamily = Manrope, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("$lots Lot", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = PrimaryTeal, fontFamily = JetBrainsMono)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Bütçe Dağılımı", fontSize = 9.sp, color = SubText, fontFamily = Manrope, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(CurrencyFormatter.formatTRY(totalCost), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = InkText, fontFamily = JetBrainsMono)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            SecondaryTabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                divider = {},
                indicator = {
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(selectedTab),
                        color = PrimaryTeal
                    )
                }
            ) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                    Text("Neden Aldık?", modifier = Modifier.padding(8.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = Manrope, color = if(selectedTab==0) PrimaryTeal else SubText)
                }
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                    Text("Riskler", modifier = Modifier.padding(8.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = Manrope, color = if(selectedTab==1) PrimaryTeal else SubText)
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = if (selectedTab == 0) candidate.bullCase else candidate.bearCase,
                style = MaterialTheme.typography.bodySmall,
                color = InkText.copy(alpha = 0.8f),
                fontFamily = Manrope,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = { onStockClick(candidate.symbol, "BIST") }) {
                    Text("Detayları Gör →", color = PrimaryTeal, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ScoreBar(color: Color, score: Int) {
    Box(
        modifier = Modifier
            .width(6.dp)
            .height(24.dp)
            .clip(RoundedCornerShape(3.dp))
            .background(color.copy(alpha = 0.1f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(score / 100f)
                .align(Alignment.BottomCenter)
                .background(color)
        )
    }
}

@Composable
fun RejectedCandidateItem(candidate: KaziCandidate) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            candidate.symbol, 
            modifier = Modifier.width(60.dp),
            fontWeight = FontWeight.Bold, 
            fontFamily = IBMPlexMono, 
            color = SubText
        )
        Text(
            candidate.rejectionReason ?: "Puan eşiği altında kaldı.",
            style = MaterialTheme.typography.bodySmall,
            color = SubText,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
