package com.nexus.porsuk.feature.ailab.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nexus.porsuk.domain.model.AiChatMessage
import com.nexus.porsuk.domain.model.AiWorkspaceType

/**
 * 6 AI Çalışma Alanı Chip Barı (AiWorkspaceChips)
 */
@Composable
fun AiWorkspaceChips(
    selectedWorkspace: AiWorkspaceType,
    onWorkspaceSelected: (AiWorkspaceType) -> Unit,
    modifier: Modifier = Modifier
) {
    ScrollableTabRow(
        selectedTabIndex = selectedWorkspace.ordinal,
        edgePadding = 16.dp,
        containerColor = MaterialTheme.colorScheme.surface,
        divider = {},
        modifier = modifier.padding(vertical = 4.dp)
    ) {
        AiWorkspaceType.entries.forEach { ws ->
            FilterChip(
                selected = ws == selectedWorkspace,
                onClick = { onWorkspaceSelected(ws) },
                label = { Text("${ws.iconEmoji} ${ws.displayName}") },
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }
    }
}

/**
 * AI Mesaj Baloncuğu Bileşeni (AiChatMessageItem)
 */
@Composable
fun AiChatMessageItem(
    message: AiChatMessage,
    modifier: Modifier = Modifier
) {
    val align = if (message.isUser) Alignment.CenterEnd else Alignment.CenterStart
    val bgColor = if (message.isUser) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = align
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = bgColor,
            modifier = Modifier.widthIn(max = 300.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = message.senderName,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
