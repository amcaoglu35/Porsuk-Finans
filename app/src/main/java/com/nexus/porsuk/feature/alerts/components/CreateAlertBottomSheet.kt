package com.nexus.porsuk.feature.alerts.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nexus.porsuk.domain.model.AlertCategory
import com.nexus.porsuk.domain.model.AlertCondition

/**
 * Porsuk Smart Alert Engine — Yeni Alarm Oluşturma BottomSheet Bileşeni
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAlertBottomSheet(
    onDismissRequest: () -> Unit,
    onCreateAlert: (String, AlertCategory, AlertCondition, Double, String?) -> Unit
) {
    var symbol by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(AlertCategory.PRICE) }
    var selectedCondition by remember { mutableStateOf(AlertCondition.ABOVE) }
    var targetValueText by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Yeni Akıllı Alarm Oluştur",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            // Sembol Girişi
            OutlinedTextField(
                value = symbol,
                onValueChange = { symbol = it.uppercase() },
                label = { Text("Sembol (Örn: THYAO.IS, AAPL, BTC-USD)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Alarm Kategorisi Seçimi
            Text("Alarm Türü", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            DropdownCategorySelector(
                selectedCategory = selectedCategory,
                onCategorySelected = { selectedCategory = it }
            )

            // Koşul Seçimi
            Text("Alarm Koşulu", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            DropdownConditionSelector(
                selectedCondition = selectedCondition,
                onConditionSelected = { selectedCondition = it }
            )

            // Hedef Değer
            OutlinedTextField(
                value = targetValueText,
                onValueChange = { targetValueText = it },
                label = { Text("Hedef Değer (Fiyat / Yüzde / Hacim)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Not Ekleme
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Kişisel Not (Opsiyonel)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    val targetVal = targetValueText.toDoubleOrNull() ?: 0.0
                    if (symbol.isNotBlank()) {
                        onCreateAlert(symbol.trim(), selectedCategory, selectedCondition, targetVal, note.ifBlank { null })
                        onDismissRequest()
                    }
                },
                enabled = symbol.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Alarmı Kur")
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DropdownCategorySelector(
    selectedCategory: AlertCategory,
    onCategorySelected: (AlertCategory) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedCategory.displayName,
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            AlertCategory.entries.forEach { cat ->
                DropdownMenuItem(
                    text = { Text(cat.displayName) },
                    onClick = {
                        onCategorySelected(cat)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DropdownConditionSelector(
    selectedCondition: AlertCondition,
    onConditionSelected: (AlertCondition) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = "${selectedCondition.symbolText} ${selectedCondition.displayName}",
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            AlertCondition.entries.forEach { cond ->
                DropdownMenuItem(
                    text = { Text("${cond.symbolText} ${cond.displayName}") },
                    onClick = {
                        onConditionSelected(cond)
                        expanded = false
                    }
                )
            }
        }
    }
}
