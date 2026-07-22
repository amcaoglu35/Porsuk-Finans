package com.nexus.porsuk.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.ui.theme.*
import dev.jeziellago.compose.markdowntext.MarkdownText
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: ChatViewModel, 
    onNavigateToSettings: () -> Unit,
    initialPrompt: String? = null
) {
    val messages by viewModel.messages.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val hasApiKey by viewModel.hasApiKey.collectAsState()
    var textInput by remember { mutableStateOf("") }
    
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val suggestions = listOf(
        "📈 Portföyümün sektörel riski nedir?",
        "🦁 Buffett & Graham kriterlerine göre analiz",
        "🇪🇺 Popüler Avrupa hisseleri hangileri?",
        "💰 En çok temettü veren hisseler"
    )

    // initialPrompt tetikleyicisi
    var initialPromptSent by remember { mutableStateOf(false) }
    LaunchedEffect(initialPrompt) {
        if (!initialPrompt.isNullOrBlank() && !initialPromptSent) {
            initialPromptSent = true
            viewModel.sendMessage(initialPrompt)
        }
    }

    // Mesajlar eklendikçe otomatik olarak aşağı kaydır
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            coroutineScope.launch {
                listState.animateScrollToItem(messages.size - 1)
            }
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Profesör'e Sor", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = InkText, fontFamily = Manrope)
                        Text("Yapay Zeka Portföy Danışmanı", style = MaterialTheme.typography.bodySmall, color = SubText, fontFamily = Manrope)
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Ayarlar",
                            tint = PrimaryTeal
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundNew)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(BackgroundNew)
        ) {
            // Mesaj Listesi
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Gemini API anahtarı yoksa en tepede uyarı bannerı göster
                if (!hasApiKey) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = PrimaryTeal.copy(alpha = 0.08f)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryTeal.copy(alpha = 0.25f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Top) {
                                    Text("✨", fontSize = 20.sp)
                                    Column {
                                        Text(
                                            "Yapay Zeka Sohbet Desteği Aktif Değil",
                                            style = MaterialTheme.typography.titleSmall,
                                            color = InkText,
                                            fontFamily = Manrope,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            "Profesör ile canlı olarak sohbet edebilmek için lütfen Ayarlar sayfasından geçerli bir Gemini API anahtarı tanımlayın.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = SubText,
                                            fontFamily = Manrope
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = onNavigateToSettings,
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth().height(38.dp)
                                ) {
                                    Text("Ayarlar'a Git & API Anahtarı Ekle", color = Color.White, fontFamily = Manrope, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }

                items(messages) { message ->
                    MessageBubble(message)
                }
                
                if (isLoading) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = CardNew),
                                border = androidx.compose.foundation.BorderStroke(1.dp, LineBorder),
                                modifier = Modifier.padding(end = 40.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        color = PrimaryTeal,
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        "Profesör düşünüyor...",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = SubText,
                                        fontFamily = Manrope
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Hızlı Soru Hapları
            if (hasApiKey) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    items(suggestions) { suggestion ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(CardNew)
                                .border(1.dp, LineBorder, RoundedCornerShape(14.dp))
                                .clickable { viewModel.sendMessage(suggestion) }
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                suggestion,
                                style = MaterialTheme.typography.bodySmall,
                                color = InkText,
                                fontFamily = Manrope,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // Mesaj Gönderme Kutusu
            Surface(
                modifier = Modifier.fillMaxWidth(),
                tonalElevation = 0.dp,
                color = CardNew,
                border = androidx.compose.foundation.BorderStroke(1.dp, LineBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                        .imePadding(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = textInput,
                        onValueChange = { textInput = it },
                        placeholder = { Text("Profesör'e bir soru sor...", color = SubText, fontFamily = Manrope) },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 12.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = BackgroundNew,
                            unfocusedContainerColor = BackgroundNew,
                            focusedBorderColor = PrimaryTeal,
                            unfocusedBorderColor = LineBorder,
                            cursorColor = PrimaryTeal,
                            focusedTextColor = InkText,
                            unfocusedTextColor = InkText
                        ),
                        maxLines = 4
                    )
                    
                    IconButton(
                        onClick = {
                            if (textInput.isNotBlank()) {
                                viewModel.sendMessage(textInput)
                                textInput = ""
                            }
                        },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(PrimaryTeal, AquaNew)
                                )
                            ),
                        enabled = textInput.isNotBlank() && !isLoading
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Gönder",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MessageBubble(message: ChatMessage) {
    val alignment = if (message.isUser) Alignment.CenterEnd else Alignment.CenterStart
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        contentAlignment = alignment
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Professor Avatar (left aligned)
            if (!message.isUser) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF7C6CF0), Color(0xFF5C4AD8))
                            )
                        )
                        .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🔮", fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.width(10.dp))
            }
            
            // Speech Bubble Balloon Card
            Card(
                shape = RoundedCornerShape(
                    topStart = if (message.isUser) 18.dp else 6.dp,
                    topEnd = if (message.isUser) 6.dp else 18.dp,
                    bottomStart = 18.dp,
                    bottomEnd = 18.dp
                ),
                colors = CardDefaults.cardColors(
                    containerColor = if (message.isUser) TealSoft else CardNew
                ),
                border = BorderStroke(
                    1.dp,
                    if (message.isUser) PrimaryTeal.copy(alpha = 0.25f) else LineBorder
                ),
                modifier = Modifier
                    .weight(1f, fill = false)
                    .padding(
                        start = if (message.isUser) 48.dp else 0.dp,
                        end = if (message.isUser) 48.dp else 0.dp
                    )
            ) {
                Box(
                    modifier = Modifier.padding(14.dp)
                ) {
                    if (message.isUser) {
                        Text(
                            text = message.text,
                            style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
                            color = InkText,
                            fontFamily = Manrope,
                            fontWeight = FontWeight.Medium
                        )
                    } else {
                        MarkdownText(
                            markdown = message.text,
                            style = androidx.compose.ui.text.TextStyle(
                                color = InkText,
                                fontSize = 14.sp,
                                fontFamily = Manrope,
                                lineHeight = 22.sp
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

