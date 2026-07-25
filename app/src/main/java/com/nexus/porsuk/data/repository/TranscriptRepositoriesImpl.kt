package com.nexus.porsuk.data.repository

import com.nexus.porsuk.data.transcript.TranscriptEngine
import com.nexus.porsuk.domain.model.*
import com.nexus.porsuk.domain.repository.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TranscriptRepositoryImpl @Inject constructor(
    private val engine: TranscriptEngine
) : TranscriptRepository {

    private val sampleSpeakers = listOf(
        SpeakerInfo("s_1", "Prof. Dr. Ahmet Bolat", "Yönetim Kurulu & İcra Komitesi Başkanı", "Türk Hava Yolları", SpeakerRole.CEO),
        SpeakerInfo("s_2", "Doç. Dr. Murat Şeker", "Genel Müdür (Mali) Yardımcısı (CFO)", "Türk Hava Yolları", SpeakerRole.CFO),
        SpeakerInfo("s_3", "Mehmet Akif Yılmaz", "Havacılık Sektör Analisti", "Is Yatırım", SpeakerRole.ANALYST)
    )

    private val sampleUtterances = listOf(
        TranscriptUtterance("u_1", sampleSpeakers[0], "Değerli yatırımcılarımız, 2026 yılının ikinci çeyreğinde Türk Hava Yolları olarak tarihi bir performans sergiledik. Yolcu sayımız geçen yılın aynı çeyreğine kıyasla %14 artarak 24.5 milyona ulaşmıştır.", 10000L, "02:15", 0.85, "Yolcu & Yolcu Trafiği", isGuidanceStatement = false),
        TranscriptUtterance("u_2", sampleSpeakers[1], "Finansal sonuçlarımıza baktığımızda, toplam gelirlerimiz %38 artışla 185 Milyar TL'ye yükselmiş, FAVÖK marjımız ise %24.5 seviyesinde gerçekleşmiştir. Yıl sonu kârlılık hedeflerimizi %40 büyüme yönünde yukarı revize ediyoruz.", 45000L, "08:40", 0.90, "Rehberlik (Guidance) & Gelir", isGuidanceStatement = true),
        TranscriptUtterance("u_3", sampleSpeakers[2], "Tebrikler güzel sonuçlar için. Jet yakıtı maliyetlerindeki küresel oynaklığın 3. çeyrek marjlarına etkisini nasıl değerlendiriyorsunuz?", 120000L, "18:20", 0.10, "Maliyet & Yakıt Riski", isRiskStatement = true)
    )

    private val sampleTranscript = EarningsCallTranscript(
        callId = "call_q2_2026",
        symbol = "THYAO.IS",
        companyName = "Türk Hava Yolları A.O.",
        period = "2026-Q2",
        dateLabel = "22 Temmuz 2026",
        callType = EarningsCallType.QUARTERLY,
        durationMinutes = 48,
        audioUrl = "https://ir.turkishairlines.com/q2_2026_call.mp3",
        utterances = sampleUtterances
    )

    override fun getTranscript(callId: String): Flow<EarningsCallTranscript?> = MutableStateFlow(sampleTranscript).asStateFlow()

    override fun getTranscriptUtterances(callId: String): Flow<List<TranscriptUtterance>> = MutableStateFlow(sampleUtterances).asStateFlow()

    override suspend fun getManagementAnalysis(callId: String): ManagementAnalysis {
        return engine.extractManagementAnalysis(sampleTranscript)
    }

    override suspend fun getTranscriptAiSummary(callId: String): TranscriptAiSummary {
        return engine.generateAiSummary(sampleTranscript)
    }

    override suspend fun getTranscriptVisuals(callId: String): TranscriptVisuals {
        val dist = engine.computeSpeakerTalkTimeDistribution(sampleUtterances)
        val timeline = listOf(
            TimestampValuePair(System.currentTimeMillis() - 90 * 86400000L, "2025-Q3", 0.65),
            TimestampValuePair(System.currentTimeMillis() - 60 * 86400000L, "2025-Q4", 0.72),
            TimestampValuePair(System.currentTimeMillis() - 30 * 86400000L, "2026-Q1", 0.81),
            TimestampValuePair(System.currentTimeMillis(), "2026-Q2", 0.88)
        )
        val heatmap = mapOf("Gelir & Büyüme" to 14, "Yakıt & Maliyet" to 8, "Rehberlik (Guidance)" to 12, "CapEx & Filo" to 6)

        return TranscriptVisuals(
            speakerTalkTimeDistribution = dist,
            quarterlySentimentTimeline = timeline,
            topicHeatmapGrid = heatmap
        )
    }
}

@Singleton
class EarningsCallRepositoryImpl @Inject constructor() : EarningsCallRepository {

    private val callsList = listOf(
        EarningsCallTranscript("call_q2_2026", "THYAO.IS", "Türk Hava Yolları", "2026-Q2", "22 Temmuz 2026", EarningsCallType.QUARTERLY, 48),
        EarningsCallTranscript("call_q1_2026", "THYAO.IS", "Türk Hava Yolları", "2026-Q1", "24 Nisan 2026", EarningsCallType.QUARTERLY, 42),
        EarningsCallTranscript("call_annual_2025", "THYAO.IS", "Türk Hava Yolları", "2025-FY", "15 Şubat 2026", EarningsCallType.ANNUAL, 60),
        EarningsCallTranscript("call_inv_day_2025", "THYAO.IS", "Türk Hava Yolları", "2025-STRATEGY", "10 Kasım 2025", EarningsCallType.INVESTOR_DAY, 90)
    )

    override fun getRecentEarningsCalls(symbol: String): Flow<List<EarningsCallTranscript>> = MutableStateFlow(callsList).asStateFlow()

    override fun getCallsByType(symbol: String, type: EarningsCallType): Flow<List<EarningsCallTranscript>> {
        return MutableStateFlow(callsList.filter { it.callType == type }).asStateFlow()
    }

    override suspend fun getQnaExchanges(callId: String): List<QnaExchange> {
        val speakerAnalyst = SpeakerInfo("s_3", "Mehmet Akif Yılmaz", "Analist", "İş Yatırım", SpeakerRole.ANALYST)
        val speakerCfo = SpeakerInfo("s_2", "Doç. Dr. Murat Şeker", "CFO", "Türk Hava Yolları", SpeakerRole.CFO)

        val q = TranscriptUtterance("q_1", speakerAnalyst, "Jet yakıtı maliyetlerindeki küresel oynaklığın 3. çeyrek marjlarına etkisini nasıl değerlendiriyorsunuz?", 120000L, "18:20", 0.10, "Maliyet")
        val a = TranscriptUtterance("a_1", speakerCfo, "Hedge oranlarımızı %45 seviyesine çıkardık, bu sayede marj dalgalanması minimize edilecektir.", 145000L, "19:10", 0.80, "Hedge & Marj")

        return listOf(
            QnaExchange("qna_1", "Mehmet Akif Yılmaz", "İş Yatırım", q, listOf(a), QnaCategory.MARGINS_PROFITABILITY)
        )
    }
}

@Singleton
class SpeakerRepositoryImpl @Inject constructor() : SpeakerRepository {

    private val speakers = listOf(
        SpeakerInfo("s_1", "Prof. Dr. Ahmet Bolat", "Yönetim Kurulu Başkanı", "Türk Hava Yolları", SpeakerRole.CEO),
        SpeakerInfo("s_2", "Doç. Dr. Murat Şeker", "CFO", "Türk Hava Yolları", SpeakerRole.CFO)
    )

    override fun getSpeakersForCall(callId: String): Flow<List<SpeakerInfo>> = MutableStateFlow(speakers).asStateFlow()

    override suspend fun getSpeakerTalkTimePct(callId: String): Map<String, Double> {
        return mapOf("Prof. Dr. Ahmet Bolat (CEO)" to 52.5, "Doç. Dr. Murat Şeker (CFO)" to 34.0, "Analistler & Soru-Cevap" to 13.5)
    }
}

@Singleton
class TranscriptSearchRepositoryImpl @Inject constructor(
    private val engine: TranscriptEngine
) : TranscriptSearchRepository {

    override suspend fun searchTranscripts(query: String, symbol: String?): List<TranscriptSearchResult> {
        val dummyUtterances = listOf(
            TranscriptUtterance("u_2", SpeakerInfo("s_2", "Murat Şeker", "CFO", "THY", SpeakerRole.CFO), "Yıl sonu kârlılık hedeflerimizi %40 büyüme yönünde yukarı revize ediyoruz.", 45000L, "08:40", 0.90, "Rehberlik")
        )
        return engine.searchUtterances(dummyUtterances, query)
    }

    override suspend fun searchByTopic(topic: String): List<TranscriptSearchResult> {
        return searchTranscripts(topic)
    }

    override fun getFutureStubs(): Flow<TranscriptFutureStubs> = MutableStateFlow(TranscriptFutureStubs()).asStateFlow()
}
