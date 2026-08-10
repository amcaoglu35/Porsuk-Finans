package com.nexus.porsuk.ui.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.ui.theme.*

@Composable
fun ProfileCard(userName: String, hasApiKey: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFF0B1F1C), PrimaryTeal, Color(0xFF015B4A))
                    )
                )
                .padding(24.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(userName.take(1).uppercase(), fontSize = 28.sp, fontWeight = FontWeight.Black, color = Color.White)
                }
                Spacer(modifier = Modifier.width(20.dp))
                Column {
                    Text(userName, style = MaterialTheme.typography.titleLarge, color = Color.White, fontWeight = FontWeight.Bold, fontFamily = Manrope)
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        color = if (hasApiKey) EmeraldNew.copy(alpha = 0.2f) else WarningGold.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (hasApiKey) Icons.Default.Check else Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = if (hasApiKey) EmeraldNew else WarningGold,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                if (hasApiKey) "AI Modelleri Aktif" else "AI Anahtarı Bekleniyor",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (hasApiKey) EmeraldNew else WarningGold,
                                fontFamily = JetBrainsMono
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsGroup(title: String, content: @Composable (ColumnScope.() -> Unit)) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = SubText,
            modifier = Modifier.padding(start = 8.dp, bottom = 10.dp),
            fontFamily = JetBrainsMono
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CardNew),
            border = androidx.compose.foundation.BorderStroke(1.dp, LineBorder)
        ) {
            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                content()
            }
        }
    }
}

@Composable
fun SettingsRow(
    icon: ImageVector,
    title: String,
    value: String? = null,
    isDestructive: Boolean = false,
    colorIndex: Int = 0,
    valueFontFamily: FontFamily? = null,
    onClick: () -> Unit
) {
    val colors = listOf(PrimaryTeal, Color(0xFF6366F1), Color(0xFFF59E0B), Color(0xFFEC4899), Color(0xFF10B981))
    val iconBgColor = if (isDestructive) NegatifRed.copy(alpha = 0.1f) else colors[colorIndex % colors.size].copy(alpha = 0.1f)
    val iconTintColor = if (isDestructive) NegatifRed else colors[colorIndex % colors.size]

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(iconBgColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = iconTintColor, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isDestructive) NegatifRed else InkText,
            fontFamily = Manrope,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f)
        )
        if (value != null) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodySmall.copy(fontFamily = valueFontFamily ?: Manrope),
                color = SubText,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null,
            tint = LineBorder,
            modifier = Modifier.size(12.dp)
        )
    }
}

@Composable
fun SettingsSwitchRow(
    icon: ImageVector,
    title: String,
    checked: Boolean,
    colorIndex: Int = 0,
    onCheckedChange: (Boolean) -> Unit
) {
    val colors = listOf(PrimaryTeal, Color(0xFF6366F1), Color(0xFFF59E0B), Color(0xFFEC4899), Color(0xFF10B981))
    val iconBgColor = colors[colorIndex % colors.size].copy(alpha = 0.1f)
    val iconTintColor = colors[colorIndex % colors.size]

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(iconBgColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = iconTintColor, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = InkText,
            fontFamily = Manrope,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = PrimaryTeal,
                uncheckedThumbColor = SubText,
                uncheckedTrackColor = LineBorder
            )
        )
    }
}
