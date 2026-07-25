package com.nexus.porsuk.ui.ailab

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.ui.chat.ChatViewModel
import com.nexus.porsuk.ui.theme.*

// Design System Tokens (Light Theme Aesthetic with Purple #6C4CF1 Accent)
private val PurpleAccent = Color(0xFF6C4CF1)
private val PurpleSoftBg = Color(0xFFF3F0FF)
private val LightSurfaceBg = Color(0xFFF8F9FD)
private val CardBg = Color(0xFFFFFFFF)
private val TextDark = Color(0xFF1E293B)
private val TextSecondary = Color(0xFF64748B)
private val BorderColor = Color(0xFFE2E8F0)
private val BullishGreen = Color(0xFF10B981)
private val BearishRed = Color(0xFFEF4444)
private val RiskOrange = Color(0xFFF59E0B)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiLabScreen(
    viewModel: ChatViewModel,
    onNavigateToSettings: () -> Unit = {},
    initialPrompt: String? = null,
    onStockClick: (String, String) -> Unit = { _, _ -> }
) {
    var textInput by remember { mutableStateOf("") }

    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        isVisible = true
    }

    // Breathing pulse animation for 3D AI Robot avatar
    val infiniteTransition = rememberInfiniteTransition()
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = LightSurfaceBg,
        topBar = {
            AiLabTopBar(
                onSearchClick = {},
                onNotificationClick = onNavigateToSettings
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. AI Karşılama Kartı (AI Greeting Hero Card)
            item(key = "ai_greeting_hero") {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(400)) + slideInVertically(initialOffsetY = { 30 })
                ) {
                    AiGreetingHeroCard(pulseScale = pulseScale)
                }
            }

            // 2. AI Chat (AI Asistan Sohbeti Card)
            item(key = "ai_chat_section") {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(500)) + slideInVertically(initialOffsetY = { 40 })
                ) {
                    AiChatCardSection(
                        textInput = textInput,
                        onTextInputChange = { textInput = it },
                        onSendMessage = {
                            if (textInput.isNotBlank()) {
                                viewModel.sendMessage(textInput)
                                textInput = ""
                            }
                        }
                    )
                }
            }

            // 3. AI Araçları (AI Tools 10-Item Grid)
            item(key = "ai_tools_grid") {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(600)) + slideInVertically(initialOffsetY = { 50 })
                ) {
                    AiToolsGridSection(
                        onToolClick = { toolName ->
                            viewModel.sendMessage("$toolName analizi yap")
                        }
                    )
                }
            }

            // 4. Oracle & 5. AI Portföy Doktoru & 6. AI Öğrenme (3 Cards Grid)
            item(key = "oracle_doctor_learning_grid") {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(700)) + slideInVertically(initialOffsetY = { 60 })
                ) {
                    TripleAiModulesGridSection(
                        onOracleClick = { viewModel.sendMessage("Oracle bugünkü tahminlerini getir") },
                        onDoctorClick = { viewModel.sendMessage("Portföyümü sağlık ve risk açısından tara") }
                    )
                }
            }

            // 7. Akıllı Bildirimler & 8. AI Otomasyonları & 9. Son AI Raporları (3 Cards Grid)
            item(key = "notifications_automations_reports_grid") {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(800)) + slideInVertically(initialOffsetY = { 70 })
                ) {
                    TripleNotificationsAndReportsGridSection()
                }
            }

            // 10. Hızlı Eylemler (Quick Actions 4 Large Buttons)
            item(key = "quick_actions_buttons") {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(900)) + slideInVertically(initialOffsetY = { 80 })
                ) {
                    QuickActionsGridSection(
                        onStartChat = { viewModel.sendMessage("Merhaba, bugünkü piyasaları değerlendirir misin?") },
                        onRunOracle = { viewModel.sendMessage("Oracle tahmin raporu oluştur") },
                        onScanPortfolio = { viewModel.sendMessage("Portföyü tara") },
                        onNewAnalysis = { viewModel.sendMessage("Yeni detaylı analiz başlat") }
                    )
                }
            }
        }
    }
}

// ── ÜST BAR (Top Bar) ──
@Composable
private fun AiLabTopBar(
    onSearchClick: () -> Unit,
    onNotificationClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(LightSurfaceBg)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("🦩", fontSize = 22.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Text(
                    "PORSUK",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black, letterSpacing = 2.sp),
                    color = TextDark
                )
                Text(
                    "F İ N A N S",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 8.sp, letterSpacing = 2.5.sp),
                    color = PurpleAccent
                )
            }
        }

        Text(
            "AI Lab",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope),
            color = TextDark
        )

        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            IconButton(onClick = onSearchClick) {
                Icon(Icons.Outlined.Search, contentDescription = "Ara", tint = TextDark)
            }
            IconButton(onClick = onNotificationClick) {
                Icon(Icons.Outlined.Notifications, contentDescription = "Bildirimler", tint = PurpleAccent)
            }
        }
    }
}

// ── 1. AI KARŞILAMA KARTI (AI Greeting Hero Card) ──
@Composable
private fun AiGreetingHeroCard(pulseScale: Float) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shadow(8.dp, RoundedCornerShape(26.dp)),
        shape = RoundedCornerShape(26.dp)
    ) {
        Box(
            modifier = Modifier.background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF1E0A4C), Color(0xFF3B1578), Color(0xFF6C4CF1))
                )
            )
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left: 3D Robot Avatar with pulse animation
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .scale(pulseScale)
                            .clip(CircleShape)
                            .background(Color(0x33FFFFFF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🤖", fontSize = 38.sp)
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    // Middle: Greeting text & Online status badge
                    Column(modifier = Modifier.weight(1f)) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0x33FFFFFF)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(BullishGreen)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("AI Online", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold), color = Color.White)
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            "Günaydın, Yusuf! 👋",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold, fontFamily = Manrope),
                            color = Color.White
                        )
                        Text(
                            "Bugün portföyünüzde dikkat edilmesi gereken 2 gelişme bulunuyor.",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, lineHeight = 15.sp),
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Right: Arc Gauge Badge
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("AI Güven Skoru", style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp), color = Color.White.copy(alpha = 0.8f))
                        Text("85", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, fontFamily = IBMPlexMono), color = Color.White)
                        Text("/100", style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp), color = Color.White.copy(alpha = 0.7f))
                        Text("Yüksek", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp), color = BullishGreen)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Color.White.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(14.dp))

                // Bottom Stats Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    HeroStatItem(title = "Bugünkü Analiz", value = "12", iconEmoji = "📈")
                    HeroStatItem(title = "Başarı Oranı", value = "%81", iconEmoji = "🎯")
                    HeroStatItem(title = "Öğrenme Seviyesi", value = "İleri", iconEmoji = "🧠")
                }
            }
        }
    }
}

@Composable
private fun HeroStatItem(title: String, value: String, iconEmoji: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(iconEmoji, fontSize = 14.sp)
        Spacer(modifier = Modifier.width(6.dp))
        Column {
            Text(title, style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp), color = Color.White.copy(alpha = 0.7f), fontFamily = Manrope)
            Text(value, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.5.sp, fontFamily = IBMPlexMono), color = Color.White)
        }
    }
}

// ── 2. AI CHAT (AI Asistan Sohbeti Card) ──
@Composable
private fun AiChatCardSection(
    textInput: String,
    onTextInputChange: (String) -> Unit,
    onSendMessage: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shadow(4.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        border = BorderStroke(1.dp, BorderColor)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("💬", fontSize = 18.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    "AI Asistan Sohbeti",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope),
                    color = TextDark
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // AI Bubble (Left)
            Row(modifier = Modifier.fillMaxWidth()) {
                Text("🤖", fontSize = 18.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = LightSurfaceBg,
                    border = BorderStroke(1.dp, BorderColor)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("Size nasıl yardımcı olabilirim?", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = TextDark)
                        Text("Piyasa, portföy veya herhangi bir konuda soru sorabilirsiniz.", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp), color = TextSecondary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // User Bubble (Right)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = PurpleAccent
                ) {
                    Text(
                        "ASELS alınır mı? ✓",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Text Input Box & Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = textInput,
                    onValueChange = onTextInputChange,
                    placeholder = { Text("Bir soru sorun...", fontSize = 11.sp, color = TextSecondary) },
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PurpleAccent,
                        unfocusedBorderColor = BorderColor,
                        focusedContainerColor = LightSurfaceBg,
                        unfocusedContainerColor = LightSurfaceBg
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = { },
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(PurpleSoftBg)
                ) {
                    Icon(Icons.Outlined.Mic, contentDescription = "Sesli Giriş", tint = PurpleAccent, modifier = Modifier.size(18.dp))
                }

                Spacer(modifier = Modifier.width(6.dp))

                IconButton(
                    onClick = onSendMessage,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(PurpleAccent)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Gönder", tint = Color.White, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

// ── 3. AI ARAÇLARI (AI Tools 10-Item Grid) ──
@Composable
private fun AiToolsGridSection(onToolClick: (String) -> Unit) {
    val tools = remember {
        listOf(
            AiToolItem("Oracle", "🔮"),
            AiToolItem("Portföy Doktoru", "🩺"),
            AiToolItem("Akıllı Tarayıcı", "🔍"),
            AiToolItem("Risk Analizi", "🛡️"),
            AiToolItem("Senaryo Simülasyonu", "⚙️"),
            AiToolItem("Haber Analizi", "📰"),
            AiToolItem("Makro Analiz", "🌐"),
            AiToolItem("Temettü Asistanı", "🪙"),
            AiToolItem("Vergi Hesaplayıcı", "🧮"),
            AiToolItem("Fon Analizi", "🍕")
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shadow(4.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        border = BorderStroke(1.dp, BorderColor)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🤖", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("AI Araçları", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = TextDark)
                }

                Text("Tüm Araçlar >", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = PurpleAccent)
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 2-row x 5-column Grid
            val firstRow = tools.take(5)
            val secondRow = tools.drop(5)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                firstRow.forEach { item ->
                    ToolGridItem(item = item, onClick = { onToolClick(item.name) })
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                secondRow.forEach { item ->
                    ToolGridItem(item = item, onClick = { onToolClick(item.name) })
                }
            }
        }
    }
}

private data class AiToolItem(val name: String, val iconEmoji: String)

@Composable
private fun ToolGridItem(item: AiToolItem, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(60.dp)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = CircleShape,
            color = PurpleSoftBg,
            border = BorderStroke(1.dp, Color(0xFFE2D9FF)),
            modifier = Modifier.size(44.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(item.iconEmoji, fontSize = 20.sp)
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            item.name,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 8.5.sp, fontFamily = Manrope),
            color = TextDark,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

// ── 4, 5, 6. ORACLE & PORTFÖY DOKTORU & AI ÖĞRENME TRIPLE GRID ──
@Composable
private fun TripleAiModulesGridSection(
    onOracleClick: () -> Unit,
    onDoctorClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Card 1: Oracle
        Card(
            modifier = Modifier
                .weight(1f)
                .shadow(4.dp, RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CardBg),
            border = BorderStroke(1.dp, BorderColor)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Oracle", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontSize = 12.sp), color = TextDark)
                    Surface(shape = RoundedCornerShape(8.dp), color = PurpleSoftBg) {
                        Text("%87 Güven", style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, fontWeight = FontWeight.Bold), color = PurpleAccent, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text("🔮", fontSize = 32.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    "Piyasalarda pozitif momentum devam ediyor.",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, lineHeight = 12.sp),
                    color = TextSecondary,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onOracleClick,
                    modifier = Modifier.fillMaxWidth().height(30.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PurpleAccent),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("Oracle'ı Aç >", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Card 2: Portföy Doktoru
        Card(
            modifier = Modifier
                .weight(1f)
                .shadow(4.dp, RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CardBg),
            border = BorderStroke(1.dp, BorderColor)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Portföy Doktoru", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp), color = TextDark)
                    Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFECFDF5)) {
                        Text("Yeni", style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, fontWeight = FontWeight.Bold), color = BullishGreen, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("72", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, fontFamily = IBMPlexMono), color = TextDark)
                    Text("/100", style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp), color = TextSecondary)
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text("Risk: Orta", style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp, color = RiskOrange, fontWeight = FontWeight.Bold))
                        Text("Sağlık: İyi", style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp, color = BullishGreen, fontWeight = FontWeight.Bold))
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    "Teknoloji ağırlığınız yüksek. Savunma sektörüne yönelmeniz önerilir.",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp, lineHeight = 11.sp),
                    color = TextSecondary,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onDoctorClick,
                    modifier = Modifier.fillMaxWidth().height(30.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BullishGreen),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("Portföyü Tara >", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Card 3: AI Öğrenme
        Card(
            modifier = Modifier
                .weight(1f)
                .shadow(4.dp, RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CardBg),
            border = BorderStroke(1.dp, BorderColor)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🎓", fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("AI Öğrenme", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp), color = TextDark)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Bugünkü Analiz", style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp), color = TextSecondary)
                    Text("128", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 8.5.sp, fontFamily = IBMPlexMono), color = TextDark)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Başarı Oranı", style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp), color = TextSecondary)
                    Text("%81", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 8.5.sp, fontFamily = IBMPlexMono), color = BullishGreen)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Yeni Öğrendikler", style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp), color = TextSecondary)
                    Text("+24", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 8.5.sp, fontFamily = IBMPlexMono), color = BullishGreen)
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { },
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Detaylı Rapor", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp), color = PurpleAccent)
                    Spacer(modifier = Modifier.width(2.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, tint = PurpleAccent, modifier = Modifier.size(8.dp))
                }
            }
        }
    }
}

// ── 7, 8, 9. AKILLI BİLDİRİMLER & AI OTOMASYONLARI & SON RAPORLAR TRIPLE GRID ──
@Composable
private fun TripleNotificationsAndReportsGridSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Card 1: Akıllı Bildirimler
        Card(
            modifier = Modifier
                .weight(1f)
                .shadow(4.dp, RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CardBg),
            border = BorderStroke(1.dp, BorderColor)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🔔", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Akıllı Bildirimler", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.5.sp), color = TextDark)
                    }
                    Text("Tümü", style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, fontWeight = FontWeight.Bold), color = PurpleAccent)
                }

                Spacer(modifier = Modifier.height(8.dp))

                NotificationMiniRow("↗️ ASELS güçlü sinyal verdi", "5dk önce")
                NotificationMiniRow("📈 Bankacılık sektörü güçleniyor", "15dk önce")
                NotificationMiniRow("📰 Fed açıklaması yaklaşıyor", "1s önce")
            }
        }

        // Card 2: AI Otomasyonları
        Card(
            modifier = Modifier
                .weight(1f)
                .shadow(4.dp, RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CardBg),
            border = BorderStroke(1.dp, BorderColor)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("⚙️", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("AI Otomasyonları", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.5.sp), color = TextDark)
                    }
                    Text("Tümü", style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, fontWeight = FontWeight.Bold), color = PurpleAccent)
                }

                Spacer(modifier = Modifier.height(8.dp))

                AutomationMiniRow("Portföyü her sabah analiz et")
                AutomationMiniRow("Risk artınca haber ver")
                AutomationMiniRow("Belirlediğim hisseleri takip et")
            }
        }

        // Card 3: Son AI Raporları
        Card(
            modifier = Modifier
                .weight(1f)
                .shadow(4.dp, RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CardBg),
            border = BorderStroke(1.dp, BorderColor)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("📄", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Son AI Raporları", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.5.sp), color = TextDark)
                    }
                    Text("Tümü", style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, fontWeight = FontWeight.Bold), color = PurpleAccent)
                }

                Spacer(modifier = Modifier.height(8.dp))

                ReportMiniRow("📄 Bugünkü Analiz Raporu", "2s önce")
                ReportMiniRow("📊 Haftalık Piyasa Raporu", "1gün önce")
                ReportMiniRow("🔮 Oracle Tahmin Raporu", "1hafta önce")
            }
        }
    }
}

@Composable
private fun NotificationMiniRow(title: String, time: String) {
    Column(modifier = Modifier.padding(vertical = 3.dp)) {
        Text(title, style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp, fontWeight = FontWeight.Bold), color = TextDark, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(time, style = MaterialTheme.typography.labelSmall.copy(fontSize = 7.5.sp), color = TextSecondary)
    }
}

@Composable
private fun AutomationMiniRow(title: String) {
    var isChecked by remember { mutableStateOf(true) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, fontWeight = FontWeight.Medium), color = TextDark, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
        Switch(
            checked = isChecked,
            onCheckedChange = { isChecked = it },
            modifier = Modifier.scale(0.6f)
        )
    }
}

@Composable
private fun ReportMiniRow(title: String, time: String) {
    Column(modifier = Modifier.padding(vertical = 3.dp)) {
        Text(title, style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp, fontWeight = FontWeight.Bold), color = TextDark, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(time, style = MaterialTheme.typography.labelSmall.copy(fontSize = 7.5.sp), color = TextSecondary)
    }
}

// ── 10. HIZLI EYLEMLER (Quick Actions Grid) ──
@Composable
private fun QuickActionsGridSection(
    onStartChat: () -> Unit,
    onRunOracle: () -> Unit,
    onScanPortfolio: () -> Unit,
    onNewAnalysis: () -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Text("⚡ Hızlı Eylemler", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = TextDark)
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            QuickActionButtonCard(title = "AI Sohbeti Başlat", iconEmoji = "💬", onClick = onStartChat, modifier = Modifier.weight(1f))
            QuickActionButtonCard(title = "Oracle", iconEmoji = "🔮", onClick = onRunOracle, modifier = Modifier.weight(1f))
            QuickActionButtonCard(title = "Portföy Tara", iconEmoji = "🩺", onClick = onScanPortfolio, modifier = Modifier.weight(1f))
            QuickActionButtonCard(title = "Yeni Analiz", iconEmoji = "📈", onClick = onNewAnalysis, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun QuickActionButtonCard(
    title: String,
    iconEmoji: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .shadow(3.dp, RoundedCornerShape(18.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        border = BorderStroke(1.dp, BorderColor)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 14.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = CircleShape,
                color = PurpleSoftBg,
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(iconEmoji, fontSize = 16.sp)
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                title,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.5.sp, fontFamily = Manrope),
                color = TextDark,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
