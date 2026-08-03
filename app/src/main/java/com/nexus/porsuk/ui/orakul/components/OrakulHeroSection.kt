package com.nexus.porsuk.ui.orakul.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.ui.theme.*

private val CardWhite = Color(0xFFFFFFFF)
private val PrimaryPurple = Color(0xFF6C4CF1)
private val PurpleSoftBg = Color(0xFFF3F0FF)
private val TextDark = Color(0xFF0F172A)
private val TextSecondary = Color(0xFF64748B)
private val BorderColor = Color(0xFFF1F5F9)

@Composable
fun OracleTopBar(
    onShareClick: () -> Unit,
    onNotificationClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Orakul AI",
                fontSize = 26.sp,
                fontWeight = FontWeight.Black,
                color = TextDark,
                fontFamily = Manrope,
                letterSpacing = (-0.5).sp
            )
            Text(
                text = "Piyasa Tahmin & Portföy Motoru",
                fontSize = 13.sp,
                color = TextSecondary,
                fontFamily = Manrope
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            IconButton(
                onClick = onShareClick,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(CardWhite)
                    .border(1.dp, BorderColor, CircleShape)
            ) {
                Icon(Icons.Outlined.Share, contentDescription = "Paylaş", tint = TextDark, modifier = Modifier.size(18.dp))
            }

            IconButton(
                onClick = onNotificationClick,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(CardWhite)
                    .border(1.dp, BorderColor, CircleShape)
            ) {
                Icon(Icons.Outlined.Notifications, contentDescription = "Bildirimler", tint = TextDark, modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
fun OracleHeroCard(
    orbScale: Float,
    orbRotation: Float,
    sourceEngine: String = "Gemini 2.0 Flash",
    onShareClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Source Engine Badge
            Surface(
                color = PurpleSoftBg,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryPurple.copy(alpha = 0.2f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(PrimaryPurple)
                    )
                    Text(
                        text = "MOTOR: $sourceEngine",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryPurple,
                        fontFamily = IBMPlexMono
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Cosmic Glass Orb Canvas
            Box(
                modifier = Modifier.size(160.dp),
                contentAlignment = Alignment.Center
            ) {
                CosmicOrbCanvas(orbScale = orbScale, orbRotation = orbRotation)

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "%88",
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Black,
                        color = PrimaryPurple,
                        fontFamily = IBMPlexMono
                    )
                    Text(
                        text = "Konsensüs Güveni",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        fontFamily = Manrope
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Yapay Zeka Konsensüs Analizi",
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextDark,
                fontFamily = Manrope
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Çoklu model ve adli muhasebe algoritmalarının ortak sinyal çıktısı",
                fontSize = 12.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                fontFamily = Manrope
            )
        }
    }
}

@Composable
fun CosmicOrbCanvas(orbScale: Float, orbRotation: Float) {
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .scale(orbScale)
            .rotate(orbRotation)
    ) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.minDimension / 2.2f

        // Outer glow gradient
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(PrimaryPurple.copy(alpha = 0.25f), Color.Transparent),
                center = center,
                radius = radius * 1.3f
            ),
            center = center,
            radius = radius * 1.3f
        )

        // Glass sphere background
        drawCircle(
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFFF3F0FF), Color(0xFFE8E0FF)),
                start = Offset(0f, 0f),
                end = Offset(size.width, size.height)
            ),
            center = center,
            radius = radius
        )

        // Orbit ring
        drawCircle(
            color = PrimaryPurple.copy(alpha = 0.4f),
            center = center,
            radius = radius * 0.85f,
            style = Stroke(width = 2.dp.toPx())
        )

        // Wave accent arc
        val wavePath = Path().apply {
            moveTo(center.x - radius * 0.7f, center.y)
            cubicTo(
                center.x - radius * 0.3f, center.y - radius * 0.5f,
                center.x + radius * 0.3f, center.y + radius * 0.5f,
                center.x + radius * 0.7f, center.y
            )
        }
        drawPath(
            path = wavePath,
            color = PrimaryPurple,
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}
