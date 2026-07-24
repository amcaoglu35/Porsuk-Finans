package com.nexus.porsuk.feature.watchlist.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nexus.porsuk.domain.model.SmartCategory

/**
 * Porsuk Watchlist Pro — Yeni Takip Listesi Oluşturma Diyaloğu
 */
@Composable
fun CreateWatchlistDialog(
    onDismissRequest: () -> Unit,
    onCreateGroup: (String, SmartCategory?) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<SmartCategory?>(null) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(
                text = "Yeni Takip Listesi Oluştur",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Liste Adı (Örn: Havacılık Hisseleri)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Akıllı Klasör Kategorisi (Opsiyonel)",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                DropdownSmartCategorySelector(
                    selectedCategory = selectedCategory,
                    onCategorySelected = { selectedCategory = it }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onCreateGroup(title.trim(), selectedCategory)
                        onDismissRequest()
                    }
                },
                enabled = title.isNotBlank()
            ) {
                Text("Oluştur")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("İptal")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DropdownSmartCategorySelector(
    selectedCategory: SmartCategory?,
    onCategorySelected: (SmartCategory?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedCategory?.title ?: "Özel Liste (Kategori Yok)",
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Özel Liste (Kategori Yok)") },
                onClick = {
                    onCategorySelected(null)
                    expanded = false
                }
            )
            SmartCategory.entries.forEach { cat ->
                DropdownMenuItem(
                    text = { Text(cat.title) },
                    onClick = {
                        onCategorySelected(cat)
                        expanded = false
                    }
                )
            }
        }
    }
}
