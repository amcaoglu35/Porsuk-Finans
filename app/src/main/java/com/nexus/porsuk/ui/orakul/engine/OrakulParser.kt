package com.nexus.porsuk.ui.orakul.engine

import com.nexus.porsuk.ui.orakul.OrakulDecision
import com.nexus.porsuk.ui.orakul.OrakulStressScenario

object OrakulParser {

    fun parseBullBearCases(text: String): Pair<String?, String?> {
        val bull = text.substringAfter("---BOĞA SENARYOSU---", "")
            .substringBefore("---AYI SENARYOSU---", "")
            .substringBefore("---SENARYOLAR SONU---", "")
            .trim()
        val bear = text.substringAfter("---AYI SENARYOSU---", "")
            .substringBefore("---SENARYOLAR SONU---", "")
            .trim()
        return Pair(bull.ifBlank { null }, bear.ifBlank { null })
    }

    fun parseDecisions(text: String): List<OrakulDecision> {
        val decisions = mutableListOf<OrakulDecision>()
        val block = text
            .substringAfter("---ORAKUL KARARLARI---", "")
            .substringBefore("---SON---", "")
            .trim()
        if (block.isBlank()) return emptyList()

        block.lines().forEach { line ->
            val trimmed = line.trim()
            if (trimmed.isBlank() || trimmed.startsWith("SEPET ADI")) return@forEach
            val parts = trimmed.split("|").map { it.trim() }
            if (parts.size >= 3) {
                val symbol = parts[0].replace("•", "").replace("-", "").trim()
                val decision = when {
                    parts[1].contains("AL") && !parts[1].contains("SAT") -> "AL"
                    parts[1].contains("SAT") -> "SAT"
                    else -> "BEKLE"
                }
                val reasonRaw = parts.getOrElse(2) { "" }
                val formulaLayer = if (reasonRaw.startsWith("Katman:")) {
                    reasonRaw.substringAfter("Katman:").substringBefore("–").trim()
                } else ""
                val reason = if (formulaLayer.isNotBlank()) {
                    reasonRaw.substringAfter("–").trim()
                } else reasonRaw
                
                val weightMatch = Regex("Ağırlık\\s*%\\s*(\\d+)").find(reasonRaw)
                val weight = weightMatch?.groupValues?.get(1)?.toDoubleOrNull() ?: 0.0

                val rsiMatch = Regex("RSI:\\s*([0-9]+(?:\\.[0-9]+)?)").find(reasonRaw)
                val rsi = rsiMatch?.groupValues?.get(1)?.toDoubleOrNull() ?: 50.0

                val crossSignal = when {
                    reasonRaw.contains("GOLDEN_CROSS", ignoreCase = true) -> "GOLDEN_CROSS"
                    reasonRaw.contains("DEATH_CROSS", ignoreCase = true) -> "DEATH_CROSS"
                    else -> "NÖTR"
                }

                val confidenceRaw = parts.getOrElse(3) { "" }
                val confidence = Regex("\\d+").find(confidenceRaw)?.value?.toIntOrNull() ?: 70
                if (symbol.isNotBlank()) {
                    decisions.add(
                        OrakulDecision(
                            symbol = symbol,
                            decision = decision,
                            reason = reason,
                            confidence = confidence.coerceIn(0, 100),
                            formulaLayer = formulaLayer,
                            weight = weight,
                            rsi = rsi,
                            crossSignal = crossSignal
                        )
                    )
                }
            }
        }
        return decisions
    }

    fun parseStressScenarios(text: String): List<OrakulStressScenario> {
        val scenarios = mutableListOf<OrakulStressScenario>()
        val block = text
            .substringAfter("---STRES TESTİ---", "")
            .substringBefore("---STRES SONU---", "")
            .trim()
        if (block.isBlank()) return emptyList()

        block.lines().forEach { line ->
            val trimmed = line.trim()
            if (trimmed.isBlank() || !trimmed.startsWith("SENARYO:", ignoreCase = true)) return@forEach
            val parts = trimmed.split("|").map { it.trim() }
            if (parts.size >= 3) {
                val scenario = parts[0].substringAfter("SENARYO:").trim()
                val impact = parts[1].substringAfter("ETKİ:").trim()
                val advice = parts[2].substringAfter("TAVSİYE:").trim()
                if (scenario.isNotBlank()) {
                    scenarios.add(OrakulStressScenario(scenario, impact, advice))
                }
            }
        }
        return scenarios
    }
}
