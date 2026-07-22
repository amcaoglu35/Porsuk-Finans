package com.nexus.porsuk.ui.common

import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.remember
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import java.util.Locale
import com.nexus.porsuk.ui.theme.Aqua
import com.nexus.porsuk.ui.theme.AquaDeep
import com.nexus.porsuk.ui.theme.AquaLight

/**
 * Şirket logosu bileşeni.
 *
 * Dışarıdan gelen [modifier] üzerinden boyut ve şekil ayarlanabilir.
 * İç padding logo görüntüsü için [imagePadding] ile kontrol edilir —
 * varsayılan değer sıfır olarak bırakılmıştır, böylece logo kutuyu tamamen kaplar.
 *
 * Yedek (fallback) sıralaması:
 *   1. [logoUrl] → Clearbit/CDN
 *   2. Google Favicons API (sz=128)
 *   3. Harfler fallback ([initials])
 */
@Composable
fun CompanyLogo(
    logoUrl: String?,
    initials: String,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 12.dp,
    imagePadding: Dp = 4.dp
) {
    val context = LocalContext.current

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(AquaLight),
        contentAlignment = Alignment.Center
    ) {
        if (!logoUrl.isNullOrEmpty()) {
            val imageRequest = ImageRequest.Builder(context)
                .data(logoUrl)
                .addHeader(
                    "User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36"
                )
                .crossfade(true)
                .build()

            SubcomposeAsyncImage(
                model = imageRequest,
                contentDescription = "$initials Logosu",
                // Inside: görüntüyü orijinal en-boy oranında sığdırır, taşırmaz
                contentScale = ContentScale.Inside,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(imagePadding),
                loading = {
                    CircularProgressIndicator(
                        color = Aqua,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(16.dp)
                    )
                },
                error = {
                    // Birincil kaynak başarısız → Google Favicons'a düş
                    val domain = when {
                        logoUrl.contains("clearbit.com/") ->
                            logoUrl.substringAfter("clearbit.com/").trim()
                        else ->
                            logoUrl.substringAfter("://").substringBefore("/")
                    }
                    val googleFaviconUrl =
                        "https://www.google.com/s2/favicons?sz=128&domain=$domain"

                    val fallbackRequest = ImageRequest.Builder(context)
                        .data(googleFaviconUrl)
                        .crossfade(true)
                        .build()

                    SubcomposeAsyncImage(
                        model = fallbackRequest,
                        contentDescription = "$initials Logosu Fallback",
                        contentScale = ContentScale.Inside,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(imagePadding),
                        loading = {
                            CircularProgressIndicator(
                                color = Aqua,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        error = {
                            FallbackLogoText(initials)
                        }
                    )
                }
            )
        } else {
            FallbackLogoText(initials)
        }
    }
}

@Composable
private fun FallbackLogoText(initials: String) {
    val colors = remember(initials) {
        val hash = initials.hashCode()
        val color1 = when (kotlin.math.abs(hash) % 5) {
            0 -> Color(0xFF3B82F6) // Cobalt Blue
            1 -> Color(0xFF0D9488) // Teal
            2 -> Color(0xFF8B5CF6) // Violet
            3 -> Color(0xFFEC4899) // Pink
            else -> Color(0xFFF59E0B) // Amber
        }
        val color2 = color1.copy(alpha = 0.5f)
        listOf(color1, color2)
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.linearGradient(colors)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials.take(2).uppercase(),
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

fun getSectorColor(symbol: String): Color {
    val sym = symbol.uppercase(Locale.US)
    return when {
        sym == "THYAO" || sym == "PGSUS" || sym.contains("AERO") || sym.contains("AIR") -> Color(0xFFE23744)
        sym == "EREGL" || sym == "KARDMD" || sym == "ISDMR" || sym.contains("STEEL") || sym.contains("IRON") -> Color(0xFF2456A8)
        sym == "TUPRS" || sym == "ASTOR" || sym == "SMRTG" || sym == "ODAS" || sym == "ALARK" || sym.contains("ENJR") || sym.contains("ENERGY") -> Color(0xFF017A63)
        sym == "ASELS" || sym == "SDTTR" || sym == "OTKAR" || sym.contains("DEF") || sym.contains("MIL") -> Color(0xFF1B2A5B)
        sym == "SASA" || sym == "HEKTS" || sym == "GUBRF" || sym.contains("CHEM") -> Color(0xFF0E9F6E)
        sym == "KCHOL" || sym == "SAHOL" || sym == "AGHOL" || sym == "DOHOL" || sym.contains("HOLD") -> Color(0xFFE8862F)
        else -> Color(0xFF00A388)
    }
}

@Composable
fun StockLogoBadge(
    logoUrl: String?,
    initials: String,
    sectorColor: Color,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Box(
        modifier = modifier
            .size(36.dp)
            .clip(RoundedCornerShape(11.dp))
            .background(sectorColor.copy(alpha = 0.12f))
            .border(1.dp, sectorColor.copy(alpha = 0.2f), RoundedCornerShape(11.dp)),
        contentAlignment = Alignment.Center
    ) {
        if (!logoUrl.isNullOrEmpty()) {
            val imageRequest = ImageRequest.Builder(context)
                .data(logoUrl)
                .addHeader("User-Agent", "Mozilla/5.0")
                .crossfade(true)
                .build()

            SubcomposeAsyncImage(
                model = imageRequest,
                contentDescription = "$initials Logosu",
                contentScale = ContentScale.Inside,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(3.dp),
                error = {
                    val domain = when {
                        logoUrl.contains("clearbit.com/") -> logoUrl.substringAfter("clearbit.com/").trim()
                        else -> logoUrl.substringAfter("://").substringBefore("/")
                    }
                    val googleFaviconUrl = "https://www.google.com/s2/favicons?sz=128&domain=$domain"
                    val fallbackRequest = ImageRequest.Builder(context)
                        .data(googleFaviconUrl)
                        .crossfade(true)
                        .build()
                    SubcomposeAsyncImage(
                        model = fallbackRequest,
                        contentDescription = "$initials Logosu Fallback",
                        contentScale = ContentScale.Inside,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(3.dp),
                        error = {
                            FallbackStockLogoText(initials, sectorColor)
                        }
                    )
                }
            )
        } else {
            FallbackStockLogoText(initials, sectorColor)
        }
    }
}

@Composable
private fun FallbackStockLogoText(initials: String, color: Color) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.linearGradient(listOf(color, color.copy(alpha = 0.6f)))),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials.take(2).uppercase(Locale.US),
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}
