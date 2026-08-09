package com.nexus.porsuk.ui.orakul

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.nexus.porsuk.ui.orakul.components.*
import com.nexus.porsuk.ui.theme.*

private val LightBackground = Color(0xFFFAFAFA)
private val CardWhite = Color(0xFFFFFFFF)
private val BorderColor = Color(0xFFF1F5F9)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrakulScreen(
    viewModel: OrakulViewModel,
    onNavigateToSettings: () -> Unit = {},
    onStockClick: (String, String) -> Unit = { _, _ -> },
    onChatNavigate: (String) -> Unit = {},
    onKaziNavigate: () -> Unit = {}
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedTimeframeIndex by remember { mutableIntStateOf(1) }
    var selectedAssetTab by remember { mutableIntStateOf(0) }
    var activeSectorExplanation by remember { mutableStateOf<SectorItem?>(null) }

    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        isVisible = true
    }

    val infiniteTransition = rememberInfiniteTransition(label = "oracle_orb_loop")
    val orbBreathingScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orb_scale"
    )

    val orbLightRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "orb_rotation"
    )

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = LightBackground,
        topBar = {
            OracleTopBar(
                onShareClick = { Toast.makeText(context, "Oracle tahmini kopyalandı", Toast.LENGTH_SHORT).show() },
                onNotificationClick = onNavigateToSettings
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 36.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Search Bar
            item(key = "hisse_search_bar") {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = CardWhite),
                    border = BorderStroke(1.dp, BorderColor)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                        placeholder = { Text("Analiz edilecek hisse (Örn: AAPL)...") },
                        trailingIcon = {
                            IconButton(onClick = { 
                                if (searchQuery.isNotBlank()) {
                                    viewModel.analyzeSymbol(searchQuery.uppercase())
                                }
                            }) {
                                Icon(Icons.Default.Search, contentDescription = null, tint = Violet)
                            }
                        },
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = Violet
                        )
                    )
                }
            }

            if (uiState.isLoading) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Violet)
                    }
                }
            }

            if (uiState.hisseReport != null) {
                item(key = "hisse_score_grid") {
                    HisseScoreGrid(uiState.hisseReport!!)
                }

                item(key = "hisse_detailed_analysis") {
                    HisseDetailedAnalysis(uiState.hisseReport!!)
                }
            }

            // Hero Card
            item(key = "oracle_hero_card") {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(400)) + slideInVertically(initialOffsetY = { 40 })
                ) {
                    OracleHeroCard(
                        orbScale = orbBreathingScale,
                        orbRotation = orbLightRotation,
                        sourceEngine = uiState.sourceEngine,
                        onShareClick = { Toast.makeText(context, "Oracle tahmini paylaşıldı", Toast.LENGTH_SHORT).show() }
                    )
                }
            }

            // Structured Forecast Card (Includes Sparkline, Bull/Bear Cases, Expandable Weight Breakdown)
            item(key = "structured_forecast_summary") {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(500)) + slideInVertically(initialOffsetY = { 40 })
                ) {
                    StructuredForecastCard(
                        streamingText = uiState.streamingText,
                        symbol = uiState.selectedSymbol,
                        bullCase = uiState.bullCase,
                        bearCase = uiState.bearCase,
                        consensusWeights = uiState.consensusWeights
                    )
                }
            }

            // Timeframe Filters
            item(key = "timeframe_filters") {
                OracleTimeframeFilterRow(
                    selectedIndex = selectedTimeframeIndex,
                    onIndexSelected = { selectedTimeframeIndex = it }
                )
            }

            // Direction Probability
            item(key = "direction_probability_grid") {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(650)) + slideInVertically(initialOffsetY = { 40 })
                ) {
                    MarketDirectionProbabilitySection(marketSentimentScore = uiState.marketSentimentScore)
                }
            }

            // Score Gauges
            item(key = "oracle_score_gauges") {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(800)) + slideInVertically(initialOffsetY = { 40 })
                ) {
                    OracleScoreGaugesSection()
                }
            }

            // Main Scenarios
            item(key = "main_scenarios_section") {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(920)) + slideInVertically(initialOffsetY = { 40 })
                ) {
                    MainScenariosSection()
                }
            }

            // Sectors & Assets
            item(key = "sector_forecast_and_top_assets") {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(1040)) + slideInVertically(initialOffsetY = { 40 })
                ) {
                    SectorsAndTopAssetsSection(
                        selectedAssetTab = selectedAssetTab,
                        onAssetTabSelected = { selectedAssetTab = it },
                        onStockClick = onStockClick,
                        onSectorInsightClick = { sector -> activeSectorExplanation = sector }
                    )
                }
            }
        }

        activeSectorExplanation?.let { sector ->
            SectorExplanationBottomSheet(
                sector = sector,
                onDismiss = { activeSectorExplanation = null }
            )
        }
    }
}
