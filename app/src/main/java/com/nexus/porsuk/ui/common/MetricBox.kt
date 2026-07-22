package com.nexus.porsuk.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.ui.theme.*

/**
 * Modern MetricBox component with top 3dp accent strip, elevation shadow,
 * JetBrains Mono 15sp 800 weight value, 8.5sp uppercase label, and optional tag pill.
 */
@Composable
fun MetricBox(
    value: String,
    label: String,
    accentColor: Color = PrimaryTeal,
    modifier: Modifier = Modifier,
    tag: String? = null,
    tagType: MetricTagType = MetricTagType.GOOD,
    subValue: String? = null
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(14.dp), ambientColor = InkText.copy(alpha = 0.04f), spotColor = InkText.copy(alpha = 0.04f)),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardNew),
        border = androidx.compose.foundation.BorderStroke(1.dp, LineBorder)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // 3dp top accent bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .background(accentColor)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = label.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = SubText,
                        fontFamily = Manrope,
                        fontWeight = FontWeight.Bold,
                        fontSize = 8.5.sp,
                        letterSpacing = 0.8.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    if (!tag.isNullOrBlank()) {
                        val (tagBg, tagText) = when (tagType) {
                            MetricTagType.GOOD -> TealSoft to PrimaryTeal
                            MetricTagType.BAD -> RedSoft to NegatifRed
                            MetricTagType.NEUTRAL -> GoldSoft to WarningGold
                            MetricTagType.ACCENT -> VioletSoft to Color(0xFF7C6CF0)
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(tagBg)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = tag,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = tagText,
                                fontFamily = JetBrainsMono
                            )
                        }
                    }
                }

                Text(
                    text = value,
                    style = MaterialTheme.typography.titleMedium,
                    color = InkText,
                    fontFamily = JetBrainsMono,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (!subValue.isNullOrBlank()) {
                    Text(
                        text = subValue,
                        fontSize = 10.sp,
                        color = SubText,
                        fontFamily = Manrope
                    )
                }
            }
        }
    }
}

enum class MetricTagType {
    GOOD, BAD, NEUTRAL, ACCENT
}
