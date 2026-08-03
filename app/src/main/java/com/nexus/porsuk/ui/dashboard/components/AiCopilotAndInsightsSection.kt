package com.nexus.porsuk.ui.dashboard.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.ui.theme.Manrope
import com.nexus.porsuk.ui.theme.PozitifGreen

data class SmartInsightItem(
    val title: String,
    val summary: String,
    val category: String, // Portföy, Risk, Haber, Temettü, Döviz
    val icon: String,
    val impactScore: String,
    val fullExplanation: String,
    val aiCommentary: String,
    val riskAnalysis: String,
    val scenarioBullish: String,
    val scenarioBearish: String,
    val recommendedAction: String
)

@Composable
fun AiCopilotHeroCard(
    onInsightClick: (SmartInsightItem) -> Unit
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val primaryContainer = MaterialTheme.colorScheme.primaryContainer
    val surfaceColor = MaterialTheme.colorScheme.surface
    val backgroundColor = MaterialTheme.colorScheme.background
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant
    val outlineColor = MaterialTheme.colorScheme.outline

    val sampleInsights = remember {
        listOf(
            SmartInsightItem(
                title = "Teknoloji Sektör Yoğunlaşması",
                summary = "Portföyün teknoloji hisselerine %42 fazla ağırlık vermiş.",
                category = "Portföy",
                icon = "⚡",
                impactScore = "Yüksek",
                fullExplanation = "Portföyünüzün toplam varlık dağılımında Teknoloji ve Yazılım sektör hisseleri %42 paya sahiptir. Bu oran genel piyasa ortalamasının üzerindedir.",
                aiCommentary = "Sektörel konsantrasyon teknoloji rallilerinde getiri artışı sağlasa da olası bir düzeltmede portföy volatilitesini yükseltebilir.",
                riskAnalysis = "Orta-Yüksek risk seviyesi. Sektör dağılım dengesizliği +%3.2 ek dalgalanma risk puanı eklemektedir.",
                scenarioBullish = "Teknoloji rallisinin devamında portföy %14 ek getiri üretebilir.",
                scenarioBearish = "Kar satışlarında portföy ortalama %6.8 geri çekilme yaşayabilir.",
                recommendedAction = "Portföyü %15 oranında Savunma ve Temettü hisseleri ile dengeleyin."
            ),
            SmartInsightItem(
                title = "Risk Seviyesi Yükseliş Trendinde",
                summary = "Son 7 günde portföy risk seviyen %68'e yükseldi.",
                category = "Risk",
                icon = "🛡️",
                impactScore = "Orta",
                fullExplanation = "Piyasa volatilitesi ve portföy içi betaların artması sonucu son 7 günlük risk skorunuz %58'den %68'e tırmanmıştır.",
                aiCommentary = "Yükselen volatilite kısa vadede stop-loss seviyelerinin yakından takip edilmesini gerektirir.",
                riskAnalysis = "Volatilitesi yüksek hisselerin ağırlığı %30 seviyesine yaklaşmıştır.",
                scenarioBullish = "Momentum korunursa kısa vadeli %8 prim potansiyeli mevcuttur.",
                scenarioBearish = "Olası kar satışlarında varlık koruma marjı daralabilir.",
                recommendedAction = "Nakit oranınızı %12 seviyesine yükselterek varlık koruma kalkanı oluşturun."
            ),
            SmartInsightItem(
                title = "THYAO Haber Katalizörü",
                summary = "THYAO yolcu büyüme verilerinden olumlu etkilenebilir.",
                category = "Haber",
                icon = "🚀",
                impactScore = "Güçlü",
                fullExplanation = "Havayolu yolcu sayıları ve dış hat büyüme rakamları analist beklentilerini %4.2 aşmıştır.",
                aiCommentary = "Yolcu doluluk oranları ve jet yakıtı marjları bilançoyu destekleyecek seviyededir.",
                riskAnalysis = "Düşük-Orta risk. Haber ve KAP entropy sinyalleri %88 güven aralığındadır.",
                scenarioBullish = "Hisse ₺320 direncini kırarak rekor tazeleyebilir.",
                scenarioBearish = "Jeopolitik gelişmelerde ₺295 desteği test edilebilir.",
                recommendedAction = "Mevcut pozisyonu koruyun, kademeli alım bölgesi ₺298 - ₺302."
            ),
            SmartInsightItem(
                title = "Temettü Sezonu Yaklaşıyor",
                summary = "Portföyünüzdeki 3 şirket bu çeyrek temettü dağıtacak.",
                category = "Temettü",
                icon = "💰",
                impactScore = "Fırsat",
                fullExplanation = "Portföyünüzde yer alan Ereğli, Tüpraş ve Aselsan nakit temettü tarihlerine yaklaşmaktadır.",
                aiCommentary = "Nakit akışı oluşturmak ve temettü verimini bileşik getiriye dönüştürmek için ideal zamanlama.",
                riskAnalysis = "Çok düşük risk. Temettü ödemeleri doğrudan nakit bakiyenize eklenecektir.",
                scenarioBullish = "Temettü verimi portföye %4.5 nakit girişi sağlayacaktır.",
                scenarioBearish = "Temettü sonrası hisse başı düzeltmeler kısa sürede kapanma eğilimindedir.",
                recommendedAction = "Temettü ödemelerini otomatik olarak model sepetlerde yeniden yatırıma dönüştürün."
            )
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shadow(4.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = surfaceColor),
        border = BorderStroke(1.dp, outlineColor)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🤖", fontSize = 18.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            "AI COPILOT & SMART INSIGHTS",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black, letterSpacing = 1.2.sp, fontFamily = Manrope),
                            color = onSurfaceColor
                        )
                        Text(
                            "Proaktif Yatırım Asistanı",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp),
                            color = primaryColor
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = PozitifGreen.copy(alpha = 0.12f)
                ) {
                    Text(
                        "● Canlı",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, fontSize = 9.sp),
                        color = PozitifGreen,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Section 1: Günün Özeti Grid Cards
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = primaryContainer.copy(alpha = 0.5f),
                border = BorderStroke(1.dp, primaryColor.copy(alpha = 0.2f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🌅", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Günün Özeti & Piyasa Nabzı", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, fontFamily = Manrope), color = primaryColor)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "BİST 100 bugün %1,35 yükseldi. Savunma ve teknoloji öncülüğünde rekor tazeleniyor. Portföyünüz bugün endeksi %0.8 yenerek daha iyi performans gösterdi.",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, lineHeight = 15.sp),
                        color = onSurfaceColor.copy(alpha = 0.85f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text("💡 Öne Çıkan Akıllı İçgörüler", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = onSurfaceVariant)

            Spacer(modifier = Modifier.height(8.dp))

            // Scrollable Smart Insight Cards Row
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(sampleInsights, key = { it.title }) { item ->
                    Card(
                        modifier = Modifier
                            .width(220.dp)
                            .clickable { onInsightClick(item) },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = backgroundColor),
                        border = BorderStroke(1.dp, outlineColor)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(item.icon, fontSize = 16.sp)
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = primaryContainer
                                ) {
                                    Text(
                                        item.category,
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp, fontWeight = FontWeight.ExtraBold),
                                        color = primaryColor,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(item.title, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = onSurfaceColor, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(item.summary, style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp, lineHeight = 13.sp), color = onSurfaceVariant, maxLines = 2, overflow = TextOverflow.Ellipsis)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsightDetailBottomSheet(
    insight: SmartInsightItem,
    onDismiss: () -> Unit
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val primaryContainer = MaterialTheme.colorScheme.primaryContainer
    val surfaceColor = MaterialTheme.colorScheme.surface
    val backgroundColor = MaterialTheme.colorScheme.background
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = surfaceColor,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(insight.icon, fontSize = 28.sp)
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(insight.title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, fontFamily = Manrope), color = onSurfaceColor)
                    Surface(shape = RoundedCornerShape(8.dp), color = primaryContainer) {
                        Text(insight.category, style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold), color = primaryColor, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Açıklama
            Text("📘 Detaylı Açıklama", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold), color = primaryColor)
            Text(insight.fullExplanation, style = MaterialTheme.typography.bodySmall.copy(lineHeight = 16.sp), color = onSurfaceColor)

            Spacer(modifier = Modifier.height(12.dp))

            // AI Yorumu
            Text("🤖 AI Uzman Yorumu", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold), color = primaryColor)
            Text(insight.aiCommentary, style = MaterialTheme.typography.bodySmall.copy(lineHeight = 16.sp), color = onSurfaceColor)

            Spacer(modifier = Modifier.height(12.dp))

            // Risk Analizi
            Text("🛡️ Risk Analizi & Etki", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold), color = primaryColor)
            Text(insight.riskAnalysis, style = MaterialTheme.typography.bodySmall.copy(lineHeight = 16.sp), color = onSurfaceColor)

            Spacer(modifier = Modifier.height(12.dp))

            // Olası Senaryolar
            Surface(shape = RoundedCornerShape(14.dp), color = backgroundColor, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("🔮 Olası Senaryolar", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold), color = onSurfaceColor)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("• Boğa Senaryosu: ${insight.scenarioBullish}", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = PozitifGreen)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text("• Ayı Senaryosu: ${insight.scenarioBearish}", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = onSurfaceVariant)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Önerilen Aksiyon Button
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text("🎯 Önerilen Aksiyon: ${insight.recommendedAction}", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
