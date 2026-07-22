package com.nexus.porsuk.ui.onboarding

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.ui.theme.*
import kotlinx.coroutines.launch

private val TextPrimary = InkText
private val TextMuted = SubText
private val BorderLine = LineBorder
private val Background = BackgroundNew
private val Surface = CardNew
private val PositiveGreen = PrimaryTeal
private val NegativeRed = NegatifRed
private val Aqua = PrimaryTeal
private val AquaLight = TealSoft

data class OnboardingPage(
    val title: String,
    val description: String,
    val emoji: String,
    val color: Color
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onFinished: () -> Unit
) {
    val pages = listOf(
        OnboardingPage(
            "Porsuk'a Hoş Geldiniz",
            "Yatırımlarınızı profesyonelce takip edin ve yapay zeka ile portföyünüzü güçlendirin.",
            "🐹",
            Aqua
        ),
        OnboardingPage(
            "Orakul Analizi",
            "O-EAGI formülü ile hisse senetlerinizi 7 farklı katmanda analiz edin ve derin içgörüler kazanın.",
            "🔮",
            Color(0xFF8B5CF6)
        ),
        OnboardingPage(
            "Akıllı Sepetler",
            "Hisselerinizi tematik sepetlerde toplayın, performanslarını benchmark'larla karşılaştırın.",
            "🧺",
            Orange
        ),
        OnboardingPage(
            "Borsa Profesörü",
            "Aklınıza takılan tüm finansal soruları portföyünüzü bilen yapay zeka asistanınıza sorun.",
            "🎓",
            PositiveGreen
        )
    )

    val pagerState = rememberPagerState { pages.size }
    val scope = rememberCoroutineScope()

    Scaffold(
        containerColor = Background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { index ->
                val page = pages[index]
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(160.dp)
                            .clip(CircleShape)
                            .background(page.color.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(page.emoji, fontSize = 80.sp)
                    }
                    Spacer(modifier = Modifier.height(40.dp))
                    Text(
                        text = page.title,
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = TextPrimary,
                        textAlign = TextAlign.Center,
                        fontFamily = Manrope
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = page.description,
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextMuted,
                        textAlign = TextAlign.Center,
                        fontFamily = Manrope,
                        lineHeight = 24.sp
                    )
                }
            }

            // Bottom Controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp, vertical = 40.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Indicators
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    repeat(pages.size) { index ->
                        val isSelected = pagerState.currentPage == index
                        Box(
                            modifier = Modifier
                                .size(if (isSelected) 24.dp else 8.dp, 8.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) Aqua else BorderLine)
                        )
                    }
                }

                // Next / Start Button
                Button(
                    onClick = {
                        if (pagerState.currentPage < pages.size - 1) {
                            scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                        } else {
                            onFinished()
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Aqua),
                    modifier = Modifier.height(56.dp).width(120.dp)
                ) {
                    Text(
                        if (pagerState.currentPage == pages.size - 1) "Başlat" else "İleri",
                        fontWeight = FontWeight.Bold,
                        fontFamily = Manrope
                    )
                }
            }
        }
    }
}
