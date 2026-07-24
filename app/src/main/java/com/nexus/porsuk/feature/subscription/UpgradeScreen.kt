package com.nexus.porsuk.feature.subscription

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nexus.porsuk.domain.model.FeaturePermission
import com.nexus.porsuk.domain.model.MembershipPlan

/**
 * Porsuk Premium Membership — Plan Yükseltme Ekranı (UpgradeScreen)
 *
 * Üyelik planlarını, kilitli özellikleri ve Google Play Billing altyapısını sunar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpgradeScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: UpgradeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Premium Membership & Subscription",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Geri"
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // 1. Mevcut Plan Rozeti
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFFFFD600).copy(alpha = 0.2f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Aktif Planınız",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = uiState.currentPlan.displayName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Badge(containerColor = Color(0xFF00C853)) {
                        Text("Aktif Lisans 👑", modifier = Modifier.padding(4.dp))
                    }
                }
            }

            // 2. Plan Seçenekleri
            Text("💎 Abonelik Planını Değiştir", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

            uiState.availablePlans.forEach { plan ->
                val isSelected = plan == uiState.selectedPlan
                Card(
                    onClick = { viewModel.selectPlan(plan) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(plan.displayName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        RadioButton(selected = isSelected, onClick = { viewModel.selectPlan(plan) })
                    }
                }
            }

            // 3. Dahil Olunan Premium Özellikler Listesi
            Text("⚡ Dahil Olan 12 Kurumsal Özellik", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

            FeaturePermission.entries.forEach { perm ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color(0xFF00C853),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(perm.displayName, style = MaterialTheme.typography.bodyMedium)
                }
            }

            // 4. Plana Yükselt Butonu
            Button(
                onClick = { viewModel.upgradeCurrentPlan() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("${uiState.selectedPlan.displayName} Planına Geç", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
