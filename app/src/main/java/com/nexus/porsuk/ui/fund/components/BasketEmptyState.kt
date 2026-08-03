package com.nexus.porsuk.ui.fund.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.ui.theme.*

@Composable
fun EmptyBasketState(onAdd: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("📊", fontSize = 48.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Bu sepette henüz hisse yok", style = MaterialTheme.typography.bodyLarge, color = SubText, fontFamily = Manrope)
        Button(
            onClick = onAdd,
            modifier = Modifier.padding(top = 16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal)
        ) {
            Text("Hisse Ekle", fontFamily = Manrope)
        }
    }
}
