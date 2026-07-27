package com.nexus.porsuk.feature.companydetail.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nexus.porsuk.feature.companydetail.*

@Composable
fun TabCorporateContent(
    board: List<BoardMember>,
    ownership: List<OwnerData>,
    timeline: List<TimelineEvent>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Yönetim Kurulu
        CorporateSectionCard(title = "Yönetim Kurulu") {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                board.forEach { member ->
                    BoardMemberItem(member = member)
                }
            }
        }
        
        // Ortaklık Yapısı
        CorporateSectionCard(title = "Ortaklık Yapısı") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ownership.forEach { owner ->
                    OwnerItem(owner = owner)
                }
            }
        }
        
        // Zaman Çizelgesi (Timeline)
        CorporateSectionCard(title = "Kurumsal Zaman Çizelgesi") {
            Column {
                timeline.forEach { event ->
                    TimelineItem(event = event, isLast = event == timeline.last())
                }
            }
        }
    }
}

@Composable
fun CorporateSectionCard(title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(text = title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            content()
        }
    }
}

@Composable
fun BoardMemberItem(member: BoardMember) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier.size(40.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Text(text = member.name.take(1), style = MaterialTheme.typography.titleSmall)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = member.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
            Text(text = member.role, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun OwnerItem(owner: OwnerData) {
    val mainGreen = Color(0xFF14B88A)
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(text = owner.name, style = MaterialTheme.typography.bodyMedium)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = String.format("%%%s%.2f", "", owner.share), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(8.dp))
            LinearProgressIndicator(
                progress = (owner.share / 100).toFloat(),
                modifier = Modifier.width(60.dp).height(4.dp).clip(CircleShape),
                color = mainGreen,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }
    }
}

@Composable
fun TimelineItem(event: TimelineEvent, isLast: Boolean) {
    val mainGreen = Color(0xFF14B88A)
    Row {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(mainGreen))
            if (!isLast) {
                Box(modifier = Modifier.width(2.dp).height(40.dp).background(MaterialTheme.colorScheme.outlineVariant))
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.padding(bottom = if (isLast) 0.dp else 16.dp)) {
            Text(text = event.date, style = MaterialTheme.typography.labelSmall, color = mainGreen, fontWeight = FontWeight.Bold)
            Text(text = event.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
            Text(text = event.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
