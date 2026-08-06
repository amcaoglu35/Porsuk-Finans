package com.nexus.porsuk.ui.dashboard.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardTopBar(
    alertCount: Int,
    onSearchClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onSettingsClick: () -> Unit = {}
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val backgroundColor = MaterialTheme.colorScheme.background

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // High-Res Geometric P Logo Vector Canvas
        Row(verticalAlignment = Alignment.CenterVertically) {
            Canvas(modifier = Modifier.size(32.dp)) {
                drawCircle(
                    brush = Brush.linearGradient(colors = listOf(Color(0xFF8B5CF6), primaryColor))
                )
                drawCircle(
                    color = Color.White,
                    radius = size.minDimension * 0.28f,
                    center = Offset(size.width * 0.45f, size.height * 0.4f)
                )
                drawCircle(
                    color = primaryColor,
                    radius = size.minDimension * 0.16f,
                    center = Offset(size.width * 0.45f, size.height * 0.4f)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    "PORSUK",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black, letterSpacing = 2.sp),
                    color = onSurfaceColor
                )
                Text(
                    "F İ N A N S",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 8.sp, letterSpacing = 2.5.sp),
                    color = primaryColor
                )
            }
        }

        // Title
        Text(
            "Anasayfa",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope),
            color = onSurfaceColor
        )

        // Actions
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            IconButton(onClick = onSearchClick) {
                Icon(Icons.Outlined.Search, contentDescription = "Ara", tint = onSurfaceColor)
            }
            IconButton(onClick = onNotificationClick) {
                BadgedBox(
                    badge = {
                        if (alertCount > 0) {
                            Badge(containerColor = NegatifRed) {
                                Text("$alertCount", fontSize = 9.sp, color = Color.White)
                            }
                        }
                    }
                ) {
                    Icon(Icons.Outlined.Notifications, contentDescription = "Bildirimler", tint = primaryColor)
                }
            }
            IconButton(onClick = onSettingsClick) {
                Icon(Icons.Outlined.Settings, contentDescription = "Ayarlar", tint = onSurfaceColor)
            }
        }
    }
}
