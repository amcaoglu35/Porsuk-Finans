package com.nexus.porsuk.ui.fund

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nexus.porsuk.ui.FinanceViewModel
import com.nexus.porsuk.data.local.entity.BasketItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FundDetailScreen(
    fundId: Int,
    fundName: String,
    viewModel: FinanceViewModel,
    onBack: () -> Unit,
    onAddAsset: (Int) -> Unit,
    onStockClick: (String, String) -> Unit
) {
    // Note: This screen is now legacy as we have BasketDetailScreen
    // But keeping it compatible for now to fix build.
    val allBaskets by viewModel.allBaskets.collectAsState(initial = emptyList())
    val basket = allBaskets.find { it.id == fundId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(fundName) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onAddAsset(fundId) }) {
                Icon(Icons.Default.Add, contentDescription = "Hisse Ekle")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            Text("Lütfen bu ekranı BasketDetailScreen ile değiştirin.")
        }
    }
}
