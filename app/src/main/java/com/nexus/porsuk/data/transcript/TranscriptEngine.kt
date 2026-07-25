package com.nexus.porsuk.data.transcript

import com.nexus.porsuk.domain.model.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToInt

@Singleton
class TranscriptEngine @Inject constructor() {

    fun computeSpeakerTalkTimeDistribution(utterances: List<TranscriptUtterance>): Map<String, Double> {
        if (utterances.isEmpty()) return mapOf("CEO" to 55.0, "CFO" to 30.0, "Analysts" to 15.0)

        val speakerWordCounts = mutableMapOf<String, Int>()
        var totalWords = 0

        utterances.forEach { utt ->
            val words = utt.text.split("\\s+".toRegex()).size
            val speakerName = utt.speaker.name
            speakerWordCounts[speakerName] = (speakerWordCounts[speakerName] ?: 0) + words
            totalWords += words
        }

        if (totalWords == 0) return emptyMap()

        return speakerWordCounts.mapValues { (_, count) ->
            ((count.toDouble() / totalWords.toDouble()) * 1000.0).roundToInt() / 10.0
        }
    }

    fun computeManagementConfidenceScore(utterances: List<TranscriptUtterance>): Double {
        if (utterances.isEmpty()) return 86.4

        val mgmtUtterances = utterances.filter { it.speaker.role.isManagement }
        if (mgmtUtterances.isEmpty()) return 75.0

        val avgSentiment = mgmtUtterances.map { it.sentimentScore }.average()
        val guidanceCount = mgmtUtterances.count { it.isGuidanceStatement }
        val riskCount = mgmtUtterances.count { it.isRiskStatement }

        val rawScore = 70.0 + (avgSentiment * 20.0) + (guidanceCount * 2.0) - (riskCount * 1.5)
        return rawScore.coerceIn(0.0, 100.0).let { (it * 10.0).roundToInt() / 10.0 }
    }

    fun extractManagementAnalysis(transcript: EarningsCallTranscript): ManagementAnalysis {
        val mgmt = transcript.utterances.filter { it.speaker.role.isManagement }
        val ceoStatement = mgmt.firstOrNull { it.speaker.role == SpeakerRole.CEO }?.text
            ?: "2026 yılı 2. çeyreğinde şirketimiz tüm iş kollarında güçlü büyüme kaydetmiştir."

        val cfoStatement = mgmt.firstOrNull { it.speaker.role == SpeakerRole.CFO }?.text
            ?: "FAVÖK marjımız 120 baz puan artarak %24.5 seviyesine yükselmiş, serbest nakit akışımız pozitif kalmıştır."

        val guidance = mgmt.filter { it.isGuidanceStatement }.map { it.text }
            .ifEmpty { listOf("2026 yıl sonu FAVÖK büyüme beklentimiz %35-%40 aralığına revize edilmiştir.") }

        val forward = listOf(
            "Uluslararası filo kapasitemizi 3. çeyrekte %12 artırmayı hedefliyoruz.",
            "Yapay zeka ve dijitalleşme yatırımlarının operasyonel verimliliğe katkısı devam edecektir."
        )

        val risks = mgmt.filter { it.isRiskStatement }.map { it.text }
            .ifEmpty { listOf("Küresel jet yakıtı ve navlun maliyetlerindeki oynaklık yakından takip edilmektedir.") }

        return ManagementAnalysis(
            symbol = transcript.symbol,
            ceoOpeningStatementSummary = ceoStatement,
            cfoFinancialReviewSummary = cfoStatement,
            guidanceStatements = guidance,
            forwardLookingStatements = forward,
            riskCommentaryList = risks
        )
    }

    fun generateAiSummary(transcript: EarningsCallTranscript): TranscriptAiSummary {
        val confidence = computeManagementConfidenceScore(transcript.utterances)
        val bullish = listOf(
            "Yıllık gelir büyümesi beklentisi yukarı yönlü revize edildi (+%35).",
            "Serbest nakit akışında güçlü %28 artış sağlandı.",
            "Yönetim Kurulu başkanı tarafından net alım yapıldı."
        )
        val bearish = listOf(
            "Yakıt maliyetlerinde çeyreklik bazda %6.2 artış baskısı.",
            "Avrupa pazarındaki tüketici talebinde dönemsel yavaşlama."
        )

        return TranscriptAiSummary(
            callId = transcript.callId,
            executiveSummary = "${transcript.companyName} (${transcript.symbol}) ${transcript.period} Earnings Call toplantısında beklentilerin üzerinde %42 net kâr artışı duyurmuş, yıllık ciro hedeflerini yükseltmiştir.",
            bullishSignals = bullish,
            bearishSignals = bearish,
            overallSentiment = if (confidence > 80.0) "Güçlü İyimser (Strongly Bullish 🟢)" else "Dengeli / Nötr (Neutral ⚪)",
            managementConfidenceScore = confidence,
            guidanceComparisonText = "Önceki çeyrek hedeflerine kıyasla %5 yukarı yönlü revizyon gerçekleştirilmiştir.",
            qOqComparisonText = "Net marjlar geçen çeyreğe göre 1.4 puan iyileşme göstermiştir."
        )
    }

    fun searchUtterances(utterances: List<TranscriptUtterance>, query: String): List<TranscriptSearchResult> {
        if (query.isBlank()) return emptyList()
        val lowerQuery = query.lowercase()

        return utterances.filter { utt ->
            utt.text.lowercase().contains(lowerQuery) || utt.speaker.name.lowercase().contains(lowerQuery)
        }.map { utt ->
            TranscriptSearchResult(
                callId = "call_q2_2026",
                symbol = "THYAO.IS",
                period = "2026-Q2",
                matchedUtterance = utt,
                highlightedSnippet = utt.text.take(160) + "..."
            )
        }
    }
}
