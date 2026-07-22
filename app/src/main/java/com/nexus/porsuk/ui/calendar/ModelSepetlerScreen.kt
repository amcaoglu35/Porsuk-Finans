package com.nexus.porsuk.ui.calendar

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModelSepetlerScreen(
    viewModel: CalendarViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var copiedPortfolio by remember { mutableStateOf<String?>(null) }

    val modelBaskets = listOf(
        ModelPortfolio(
            name = "👑 Warren Buffett Efsane Değer Sepeti",
            market = "BIST",
            yield = "Yüksek Moat & Güvenlik Marjı",
            description = "Warren Buffett'ın kalıcı rekabet üstünlüğü (Moat), yüksek ROE (%20+) ve düşük borçluluk prensiplerine göre oluşturulmuş efsanevi değer sepeti.",
            items = listOf("KCHOL" to 0.25, "BIMAS" to 0.25, "TUPRS" to 0.20, "EREGL" to 0.15, "CCHOL" to 0.15),
            emoji = "👑"
        ),
        ModelPortfolio(
            name = "📈 Peter Lynch PEG Büyüme Sepeti",
            market = "BIST",
            yield = "PEG < 1.0 Hızlı Büyüme",
            description = "Peter Lynch'in PEG < 1.0, yüksek kâr büyüme hızı ve güçlü faaliyet nakit akışı gösteren şirketlerinden oluşan agresif büyüme sepeti.",
            items = listOf("ASELS" to 0.25, "PGSUS" to 0.25, "THYAO" to 0.20, "AKSEN" to 0.15, "ASTOR" to 0.15),
            emoji = "📈"
        ),
        ModelPortfolio(
            name = "🛡️ Ray Dalio Tüm Hava Şartları Sepeti",
            market = "BIST",
            yield = "Risk Paritesi & Defansif Denge",
            description = "Ray Dalio'nun All-Weather risk paritesi yaklaşımına göre enflasyon, kriz ve büyüme dönemlerinde portföyü koruyan dengeli sepet.",
            items = listOf("THYAO" to 0.25, "KCHOL" to 0.25, "TUPRS" to 0.20, "SISE" to 0.15, "BIMAS" to 0.15),
            emoji = "🛡️"
        ),
        ModelPortfolio(
            name = "🏛️ Benjamin Graham Derin Değer Sepeti",
            market = "BIST",
            yield = "Düşük F/K & Yüksek Net Aktif",
            description = "Değer yatırımcılığının babası Benjamin Graham'ın F/K < 10, PD/DD düşük ve net çalışma sermayesi yüksek şirketler sepeti.",
            items = listOf("EREGL" to 0.25, "SAHOL" to 0.25, "ISCTR" to 0.20, "SISE" to 0.15, "HALKB" to 0.15),
            emoji = "🏛️"
        ),
        ModelPortfolio(
            name = "🎯 Temettü Aslanları (BIST)",
            market = "BIST",
            yield = "%6.5 Yıllık Verim",
            description = "BIST100 endeksinde yüksek ve düzenli nakit temettü dağıtımı yapan, borç oranları düşük ve büyümesi istikrarlı 5 dev Türk şirketinden oluşan dengeli bir temettü sepeti.",
            items = listOf("EREGL" to 0.25, "TUPRS" to 0.25, "KCHOL" to 0.20, "BIMAS" to 0.15, "TOASO" to 0.15),
            emoji = "🦁"
        ),
        ModelPortfolio(
            name = "🇺🇸 Global Temettü Kralları (US)",
            market = "NASDAQ",
            yield = "%2.8 Yıllık Verim",
            description = "Dolar bazlı pasif gelir üretmek için ABD borsalarındaki kesintisiz temettü artış geçmişine (Dividend Aristocrats) sahip 5 dev şirketten oluşan küresel sepet.",
            items = listOf("AAPL" to 0.20, "MSFT" to 0.20, "JNJ" to 0.20, "PG" to 0.20, "KO" to 0.20),
            emoji = "👑"
        )
    )

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Model Sepetler",
                            fontFamily = Manrope,
                            fontWeight = FontWeight.Bold,
                            color = InkText,
                            fontSize = 18.sp
                        )
                        Text(
                            "Orakul AI & klasik analiz model portföyleri",
                            fontFamily = Manrope,
                            fontSize = 11.sp,
                            color = SubText
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .padding(8.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(AquaSoft)
                            .border(1.dp, LineBorder, RoundedCornerShape(12.dp))
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri", tint = PrimaryTeal)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundNew)
            )
        },
        containerColor = BackgroundNew
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero Banner
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(PrimaryTeal, Color(0xFF017A63))
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text("🔮", fontSize = 28.sp)
                            Column {
                                Text(
                                    "Orakul AI Model Portföyleri",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White,
                                    fontFamily = Manrope
                                )
                                Text(
                                    "Hazır model sepetleri tek tıkla portföyünüze ekleyin",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontFamily = Manrope
                                )
                            }
                        }
                    }
                }
            }

            // Model Basket Cards
            items(modelBaskets) { portfolio ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardNew),
                    border = BorderStroke(1.dp, LineBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(portfolio.emoji, fontSize = 22.sp)
                                Text(
                                    text = portfolio.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = InkText,
                                    fontFamily = Manrope,
                                    maxLines = 1,
                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(TealSoft)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = portfolio.yield,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryTeal,
                                    fontFamily = Manrope,
                                    maxLines = 1,
                                    softWrap = false
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            portfolio.description,
                            fontSize = 11.sp,
                            color = SubText,
                            fontFamily = Manrope,
                            lineHeight = 15.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            "Sepet İçeriği & Hedef Ağırlıklar:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = InkText,
                            fontFamily = Manrope
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            portfolio.items.forEach { (symbol, weight) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(BackgroundNew)
                                        .padding(horizontal = 12.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        symbol,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = InkText,
                                        fontFamily = IBMPlexMono
                                    )
                                    Text(
                                        String.format(java.util.Locale.US, "%%%d", Math.round(weight * 100)),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = PrimaryTeal,
                                        fontFamily = IBMPlexMono
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        val isAlreadyCopied = copiedPortfolio == portfolio.name
                        Button(
                            onClick = {
                                viewModel.cloneModelPortfolio(
                                    name = portfolio.name
                                        .replace("🎯 ", "")
                                        .replace("🇺🇸 ", "")
                                        .replace("⚡ ", "")
                                        .replace("🌍 ", "")
                                        .replace(" (BIST)", "")
                                        .replace(" (US)", "")
                                        .replace(" (US Tech)", ""),
                                    market = portfolio.market,
                                    items = portfolio.items
                                )
                                copiedPortfolio = portfolio.name
                                android.widget.Toast.makeText(
                                    context,
                                    "Model sepet kopyalanıp portföyünüze eklendi!",
                                    android.widget.Toast.LENGTH_SHORT
                                ).show()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isAlreadyCopied) Color(0xFF2E7D32) else PrimaryTeal
                            )
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (isAlreadyCopied) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        "Sepetlerinize Kopyalandı",
                                        color = Color.White,
                                        fontFamily = Manrope,
                                        fontWeight = FontWeight.Bold
                                    )
                                } else {
                                    Text(
                                        "Kopyala ve Sepetime Ekle",
                                        color = Color.White,
                                        fontFamily = Manrope,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

data class ModelPortfolio(
    val name: String,
    val market: String,
    val yield: String,
    val description: String,
    val items: List<Pair<String, Double>>,
    val emoji: String
)
