package com.nexus.porsuk.feature.calendar.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.ui.theme.*
import dev.jeziellago.compose.markdowntext.MarkdownText
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun OrakulInsightBanner(
    isLoading: Boolean,
    isVisible: Boolean,
    hasKey: Boolean,
    insightText: String,
    errorText: String?,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0B1F1C)),
        border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryTeal.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(PrimaryTeal.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.AutoAwesome, null, tint = PrimaryTeal, modifier = Modifier.size(16.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "Orakul AI Analizi",
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Manrope
                    )
                }

                IconButton(onClick = onToggle) {
                    Icon(
                        imageVector = if (isVisible) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.6f)
                    )
                }
            }

            AnimatedVisibility(
                visible = isVisible,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(16.dp))
                    if (!hasKey) {
                        OrakulInsightContentCard(
                            text = "Yapay zeka analizlerini görebilmek için Ayarlar menüsünden Gemini API anahtarınızı tanımlamanız gerekmektedir.",
                            isError = true
                        )
                    } else if (isLoading) {
                        Box(modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = PrimaryTeal, modifier = Modifier.size(24.dp))
                        }
                    } else if (errorText != null) {
                        OrakulInsightContentCard(text = "Analiz yüklenirken hata oluştu: $errorText", isError = true)
                    } else {
                        OrakulInsightContentCard(text = insightText)
                    }
                }
            }
        }
    }
}

@Composable
fun OrakulInsightContentCard(
    text: String,
    isError: Boolean = false,
    label: String? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (isError) Color(0xFF3B1E1E) else Color.White.copy(alpha = 0.05f))
            .border(1.dp, if (isError) NegatifRed.copy(alpha = 0.3f) else Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        if (label != null) {
            Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = PrimaryTeal, fontFamily = JetBrainsMono)
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        if (text.isBlank()) {
            Text("Analiz hazırlanıyor...", color = Color.White.copy(alpha = 0.4f), fontSize = 13.sp, fontFamily = Manrope)
        } else {
            MarkdownText(
                markdown = text,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 13.sp,
                    lineHeight = 20.sp,
                    fontFamily = Manrope
                )
            )
        }
    }
}

@Composable
fun TimelineConnector(
    color: Color = LineBorder,
    isFirst: Boolean = false,
    isLast: Boolean = false
) {
    Column(
        modifier = Modifier.width(24.dp).fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .width(2.dp)
                .weight(1f)
                .background(if (isFirst) Color.Transparent else color)
        )
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(BackgroundNew)
                .border(2.dp, color, CircleShape)
        )
        Box(
            modifier = Modifier
                .width(2.dp)
                .weight(1f)
                .background(if (isLast) Color.Transparent else color)
        )
    }
}

fun formatDate(timestamp: Long): String {
    return SimpleDateFormat("dd MMM yyyy", Locale("tr")).format(Date(timestamp))
}
