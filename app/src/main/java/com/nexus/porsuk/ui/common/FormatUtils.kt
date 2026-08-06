package com.nexus.porsuk.ui.common

import java.util.Locale
import kotlin.math.abs

object PercentFormatter {
    /**
     * Formats a percentage change value cleanly without double minus signs (e.g. "^ %1.35" or "v %0.42").
     * Returns a Pair of (formattedString, isPositive).
     */
    fun formatChangePercent(pct: Double): Pair<String, Boolean> {
        val isPos = pct >= 0.0
        val arrow = if (isPos) "^ %" else "v %"
        val formattedVal = String.format(Locale.US, "%.2f", abs(pct))
        return Pair("$arrow$formattedVal", isPos)
    }

    /**
     * Formats a signed percentage change without arrow (e.g. "%1.35" or "-%0.42").
     */
    fun formatSignedPercent(pct: Double): String {
        val isPos = pct >= 0.0
        val formattedVal = String.format(Locale.US, "%.2f", abs(pct))
        return if (isPos) "%$formattedVal" else "-%$formattedVal"
    }
}
