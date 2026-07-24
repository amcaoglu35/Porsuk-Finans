package com.nexus.porsuk.feature.settings.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nexus.porsuk.domain.model.AppThemeMode
import com.nexus.porsuk.domain.model.DefaultCurrency

/**
 * Tema Seçim Kartı (ThemeSelectionCard)
 */
@Composable
fun ThemeSelectionCard(
    currentTheme: AppThemeMode,
    onThemeSelect: (AppThemeMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text("Uygulama Görünümü & Tema", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(10.dp))
            AppThemeMode.entries.forEach { mode ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onThemeSelect(mode) }
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("${mode.iconEmoji} ${mode.displayName}", style = MaterialTheme.typography.bodyMedium)
                    RadioButton(
                        selected = currentTheme == mode,
                        onClick = { onThemeSelect(mode) }
                    )
                }
            }
        }
    }
}

/**
 * Para Birimi Seçim Satırı (CurrencyRow)
 */
@Composable
fun CurrencyRow(
    currentCurrency: DefaultCurrency,
    onCurrencySelect: (DefaultCurrency) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text("Varsayılan Para Birimi", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                DefaultCurrency.entries.forEach { curr ->
                    FilterChip(
                        selected = currentCurrency == curr,
                        onClick = { onCurrencySelect(curr) },
                        label = { Text(curr.displayName) }
                    )
                }
            }
        }
    }
}
