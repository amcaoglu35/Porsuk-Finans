package com.nexus.porsuk.worker

import android.content.Context
import android.util.Log
import androidx.work.*
import com.nexus.porsuk.data.local.entity.*
import com.nexus.porsuk.data.repository.FinanceRepository
import com.nexus.porsuk.data.repository.KaziRepository
import com.nexus.porsuk.ui.FinanceViewModelFactory
import com.nexus.porsuk.ui.common.NotificationHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class KaziAnalysisWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    private val TAG = "KaziAnalysisWorker"

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val runId = inputData.getInt("runId", -1)
        if (runId == -1) return@withContext Result.failure()

        val repository = FinanceViewModelFactory.getKaziRepository(applicationContext)
        val financeRepo = FinanceViewModelFactory.getRepository(applicationContext)
        val settings = FinanceViewModelFactory.getSettingsManager(applicationContext)
        val apiKey = settings.getGeminiApiKey() ?: return@withContext Result.failure()

        try {
            val kaziRun = repository.getLatestKaziRun().first() ?: return@withContext Result.failure()
            
            // Parse strategyFocus from horizon (format: "HORIZON|STRATEGY|STOCK_COUNT|DEPTH|CASH_BUFFER|MIN_SCORE")
            val parts = kaziRun.horizon.split("|")
            val baseHorizon = parts.getOrNull(0) ?: "MEDIUM"
            val strategyFocus = parts.getOrNull(1) ?: "VALUE"
            val targetStockCount = parts.getOrNull(2)?.toIntOrNull() ?: 5
            val reasoningDepth = parts.getOrNull(3) ?: "DEEP"
            val cashBufferPct = parts.getOrNull(4)?.toDoubleOrNull() ?: 10.0
            val minQualityScore = parts.getOrNull(5)?.toIntOrNull() ?: 0

            // Step 1: Universe screening (K)
            repository.updateKaziRunStatus(runId, "RUNNING", 1)
            kotlinx.coroutines.delay(2500L)
            val candidates = performStep1Screening(financeRepo, strategyFocus)
            repository.saveCandidates(candidates.map { it.copy(runId = runId) })

            // Step 2: News & Disclosures (A)
            repository.updateKaziRunStatus(runId, "RUNNING", 2)
            kotlinx.coroutines.delay(2500L)
            val updatedCandidatesWithA = performStep2NewsAnalysis(candidates, financeRepo)
            repository.saveCandidates(updatedCandidatesWithA.map { it.copy(runId = runId) })

            // Step 3: Timing & Momentum (Z)
            repository.updateKaziRunStatus(runId, "RUNNING", 3)
            kotlinx.coroutines.delay(2500L)
            val updatedCandidatesWithZ = performStep3Timing(updatedCandidatesWithA, financeRepo)
            repository.saveCandidates(updatedCandidatesWithZ.map { it.copy(runId = runId) })

            // Step 4: Macro scenario (İ)
            repository.updateKaziRunStatus(runId, "RUNNING", 4)
            kotlinx.coroutines.delay(2500L)
            val updatedCandidatesWithI = performStep4Macro(updatedCandidatesWithZ, kaziRun.riskProfile, strategyFocus, financeRepo)
            repository.saveCandidates(updatedCandidatesWithI.map { it.copy(runId = runId) })

            // Step 5: Basket optimization
            repository.updateKaziRunStatus(runId, "RUNNING", 5)
            kotlinx.coroutines.delay(2500L)
            val basketResult = performStep5Optimization(updatedCandidatesWithI, kaziRun, targetStockCount, minQualityScore, cashBufferPct)
            
            // Generate custom thesis for selected candidates
            var selectedIndex = 0
            val optimizedCandidates = updatedCandidatesWithI.map { candidate ->
                val isSelected = basketResult.second.any { it.symbol == candidate.symbol }
                if (isSelected) {
                    // Add a delay between API calls to avoid 429 Rate Limit (Free tier is 15 RPM)
                    if (selectedIndex > 0) {
                        kotlinx.coroutines.delay(3000L)
                    }
                    selectedIndex++
                    val thesis = generateThesisWithGemini(candidate.symbol, kaziRun, apiKey, reasoningDepth)
                    candidate.copy(selected = true, bullCase = thesis.first, bearCase = thesis.second)
                } else {
                    candidate.copy(
                        selected = false,
                        rejectionReason = when {
                            candidate.compositeScore < minQualityScore -> "Minimum Kalite Skoru barajını aşamadı (Sınır: $minQualityScore, Skor: ${candidate.compositeScore})."
                            candidate.compositeScore < 50 -> "Düşük KAZI kompozit skoru (${candidate.compositeScore})."
                            candidate.kScore < 45 -> "Graham Kalite/Değer puanı yetersiz (${candidate.kScore})."
                            else -> "Puan sıralamasında seçilen ilk $targetStockCount hissenin gerisinde kaldı."
                        }
                    )
                }
            }
            repository.saveCandidates(optimizedCandidates.map { it.copy(runId = runId) })

            repository.saveKaziBasket(
                basketResult.first.copy(runId = runId),
                basketResult.second
            )

            // Step 6: Rationale report
            repository.updateKaziRunStatus(runId, "RUNNING", 6)
            kotlinx.coroutines.delay(2500L)
            repository.completeKaziRun(runId)
            
            NotificationHelper.sendNotification(
                applicationContext,
                "Mining Tamamlandı!",
                "Derin Kazı başarıyla bitti. Yeni sepetini inceleyebilirsin.",
                999
            )

            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Mining failure", e)
            repository.failKaziRun(runId)
            Result.failure()
        }
    }

    private suspend fun calculateKScore(company: Company, strategyFocus: String, financeRepo: FinanceRepository): Int {
        val info = try {
            financeRepo.getCachedInfo(company.symbol).first()
        } catch (e: Exception) {
            null
        }
        var score = 50 // Base score

        if (info != null) {
            val pe = info.peRatio
            if (pe != null) {
                if (pe in 0.0..12.0) {
                    score += if (strategyFocus == "VALUE") 25 else 15
                } else if (pe in 12.0..20.0) {
                    score += 8
                } else if (pe > 25.0) {
                    score -= if (strategyFocus == "VALUE") 20 else 10
                }
            }

            val div = info.dividendYield
            if (div != null) {
                if (div > 5.0) {
                    score += if (strategyFocus == "DIVIDEND") 30 else 15
                } else if (div > 2.0) {
                    score += 8
                }
            }

            val low = info.week52Low
            val high = info.week52High
            val price = company.currentPrice
            if (low != null && high != null && price > 0.0) {
                val range = high - low
                if (range > 0.0) {
                    val position = (price - low) / range
                    if (position < 0.3) {
                        score += 10
                    } else if (position > 0.8) {
                        score -= 5
                    }
                }
            }
        } else {
            score = (45..65).random()
        }

        return score.coerceIn(0, 100)
    }

    private suspend fun calculateAScore(symbol: String, financeRepo: FinanceRepository): Int {
        val news = try {
            financeRepo.getNews(symbol).first()
        } catch (e: Exception) {
            emptyList()
        }
        
        val baseScore = if (news.isEmpty()) {
            (40..60).random()
        } else {
            var positiveCount = 0
            var negativeCount = 0
            news.forEach { item ->
                when (item.sentiment?.uppercase()) {
                    "POSITIVE" -> positiveCount++
                    "NEGATIVE" -> negativeCount++
                }
            }
            val total = positiveCount + negativeCount
            if (total == 0) 50 else {
                val ratio = positiveCount.toDouble() / total
                (30 + (ratio * 60).toInt()).coerceIn(0, 100)
            }
        }
        
        // Simulated KAP announcement sentiment bonus for extra depth
        val kapBonus = when (symbol) {
            "THYAO", "TUPRS", "PGSUS" -> 12 // KAP: Kapasite artışı ve güçlü yolcu trafiği
            "EREGL" -> -8 // KAP: Enerji maliyetleri ve planlı fırın bakımı duruşu
            else -> 0
        }
        
        return (baseScore + kapBonus).coerceIn(0, 100)
    }

    private suspend fun calculateZScore(symbol: String, financeRepo: FinanceRepository): Int {
        val company = financeRepo.getCompany(symbol) ?: return (40..65).random()
        var score = 55 // Base score
        val change = company.changePercent
        if (change > 0.0) {
            score += (change * 5).toInt().coerceIn(0, 25)
        } else if (change < 0.0) {
            score -= (kotlin.math.abs(change) * 4).toInt().coerceIn(0, 20)
        }
        score += (-5..5).random()
        return score.coerceIn(0, 100)
    }

    private suspend fun calculateIScore(symbol: String, riskProfile: String, strategyFocus: String, financeRepo: FinanceRepository): Int {
        val company = financeRepo.getCompany(symbol) ?: return (50..75).random()
        var score = 60 // Base score
        val sector = company.sector ?: "Diğer"
        
        when (riskProfile.uppercase()) {
            "CONSERVATIVE" -> {
                when (sector) {
                    "Finans", "Sağlık", "Perakende", "Gıda" -> score += 15
                    "Sanayi" -> score += 10
                    "Teknoloji" -> score -= 15
                }
            }
            "AGGRESSIVE" -> {
                when (sector) {
                    "Teknoloji" -> score += 20
                    "Enerji" -> score += 15
                    "Gıda", "Sağlık" -> score -= 5
                }
            }
            "BALANCED" -> {
                when (sector) {
                    "Teknoloji", "Enerji", "Sanayi" -> score += 10
                    "Finans", "Sağlık" -> score += 5
                }
            }
        }

        // Apply strategy focus adjustments to the macro score
        when (strategyFocus.uppercase()) {
            "GROWTH" -> {
                if (sector == "Teknoloji" || sector == "Enerji") score += 12
            }
            "DIVIDEND" -> {
                if (sector == "Finans" || sector == "Sanayi" || sector == "Perakende") score += 8
            }
        }
        
        score += (-7..7).random()
        return score.coerceIn(0, 100)
    }

    private suspend fun generateThesisWithGemini(symbol: String, run: KaziRun, apiKey: String, reasoningDepth: String): Pair<String, String> {
        try {
            // Parse out the focus from horizon if encoded
            val parts = run.horizon.split("|")
            val baseHorizon = parts.getOrNull(0) ?: "MEDIUM"
            val strategyFocus = parts.getOrNull(1) ?: "VALUE"

            val depthInstruction = when (reasoningDepth.uppercase()) {
                "STANDART" -> """
                    1. NEDEN SEÇİLDİ? (Yatırım Gerekçesi): Bu hissenin neden sepet için seçildiğini 2-3 paragraflık hızlı ve özlü bir yatırım teziyle açıkla.
                    2. RİSK ANALİZİ (Risk Senaryoları): Hisse için en kritik 2-3 risk faktörünü içeren kısa bir risk özeti yaz.
                """.trimIndent()
                "ULTRA" -> """
                    1. NEDEN SEÇİLDİ? (Yatırım Gerekçesi): Bu hissenin neden sepet için seçildiğini O-EAGI 2.0 Quant Modeli, İndirgenmiş Nakit Akışı (DCF), Serbest Nakit Akışı Verimi (FCF Yield), DuPont ROE Kırılımı (Net Marj × Devir Hızı × Kaldıraç), Piotroski F-Score (0-9) ve Benjamin Graham güvenlik marjı hesaplamalarıyla detaylıca açıkla.
                    2. RİSK ANALİZİ (Adli Muhasebe & Risk): Altman Z-Score (iflas riski), Beneish M-Score (bilanço manipülasyon tespiti), TCMB makro politika faizi duyarlılığı ve sektörel risk faktörlerini içeren kapsamlı adli muhasebe ve risk raporu yaz.
                """.trimIndent()
                else -> """
                    1. NEDEN SEÇİLDİ? (Yatırım Gerekçesi): Bu hissenin neden sepet için seçildiğini O-EAGI 2.0 Quant Modeli, Serbest Nakit Akışı (FCF), DuPont ROE Ayrıştırması ve büyüme rasyolarıyla detaylıca açıkla.
                    2. RİSK ANALİZİ (Risk Senaryoları): Altman Z-Score, Beneish M-Score kırmızı bayrakları ve makro/sektörel risk senaryolarını açıklayan derin bir risk raporu yaz.
                """.trimIndent()
            }

            val prompt = """
                Şirket: $symbol
                Yatırım Profili: Risk: ${run.riskProfile}, Ufuk: $baseHorizon, Strateji Odak: $strategyFocus, Analiz Derinliği: $reasoningDepth
                Makro Rejimi: TCMB Politika Faizi %50.0, Enflasyon %61.8, Ters Tahvil Getiri Eğrisi
                
                Sen Wall Street ve BIST Baş Kantitatif Stratejistisin. Bu hisse senedi için şu kantitatif analizi gerçekleştir:
                $depthInstruction
                
                Çıktıyı tam olarak şu formatta ver:
                NEDEN:
                [Yatırım gerekçesi raporunu buraya yaz]
                
                RİSK:
                [Risk analizi raporunu buraya yaz]
            """.trimIndent()

            val text = com.nexus.porsuk.ui.common.GeminiModels.generateContentWithFallback(apiKey, prompt)
            if (text.isBlank()) {
                throw Exception("Gerekçe oluşturulamadı.")
            }
            
            val nedenStart = text.indexOf("NEDEN:", ignoreCase = true)
            val riskStart = text.indexOf("RİSK:", ignoreCase = true)
            
            val neden = if (nedenStart != -1 && riskStart != -1 && nedenStart < riskStart) {
                text.substring(nedenStart + 6, riskStart).trim()
            } else if (nedenStart != -1 && riskStart == -1) {
                text.substring(nedenStart + 6).trim()
            } else {
                "Yatırım profiline ve temel kalite kriterlerine uygun büyüme potansiyeli."
            }

            val risk = if (riskStart != -1) {
                if (nedenStart != -1 && riskStart < nedenStart) {
                    text.substring(riskStart + 5, nedenStart).trim()
                } else {
                    text.substring(riskStart + 5).trim()
                }
            } else {
                "Piyasa koşullarındaki dalgalanmalar ve sektörel rekabet artışı."
            }
            return neden to risk
        } catch (e: Exception) {
            return "Yatırım profiline ve temel kalite kriterlerine uygun büyüme potansiyeli." to "Piyasa koşullarındaki dalgalanmalar ve sektörel rekabet artışı."
        }
    }

    private suspend fun performStep1Screening(financeRepo: FinanceRepository, strategyFocus: String): List<KaziCandidate> {
        val companies = financeRepo.allCompanies.first()
        return companies.filter { it.market == "BIST" }.shuffled().take(30).map { company ->
            val kScore = calculateKScore(company, strategyFocus, financeRepo)
            KaziCandidate(
                runId = 0,
                symbol = company.symbol,
                kScore = kScore,
                aScore = 0,
                zScore = 0,
                iScore = 0,
                compositeScore = 0,
                bullCase = "",
                bearCase = "",
                selected = false
            )
        }
    }

    private suspend fun performStep2NewsAnalysis(candidates: List<KaziCandidate>, financeRepo: FinanceRepository): List<KaziCandidate> {
        return candidates.map {
            val aScore = calculateAScore(it.symbol, financeRepo)
            it.copy(aScore = aScore)
        }
    }

    private suspend fun performStep3Timing(candidates: List<KaziCandidate>, financeRepo: FinanceRepository): List<KaziCandidate> {
        return candidates.map {
            val zScore = calculateZScore(it.symbol, financeRepo)
            it.copy(zScore = zScore)
        }
    }

    private suspend fun performStep4Macro(candidates: List<KaziCandidate>, riskProfile: String, strategyFocus: String, financeRepo: FinanceRepository): List<KaziCandidate> {
        return candidates.map { 
            val iScore = calculateIScore(it.symbol, riskProfile, strategyFocus, financeRepo)
            val composite = (it.kScore * 0.3 + it.aScore * 0.25 + it.zScore * 0.2 + iScore * 0.25).toInt()
            it.copy(iScore = iScore, compositeScore = composite, bullCase = "Yatırım gerekçesi...", bearCase = "Risk senaryosu...")
        }
    }

    private suspend fun performStep5Optimization(
        candidates: List<KaziCandidate>,
        run: KaziRun,
        targetStockCount: Int,
        minQualityScore: Int,
        cashBufferPct: Double
    ): Pair<KaziBasket, List<KaziBasketItem>> {
        val filtered = candidates.filter { it.compositeScore >= minQualityScore }
        val selected = filtered.sortedByDescending { it.compositeScore }.take(targetStockCount)
        val basket = KaziBasket(
            runId = 0,
            basketName = "KAZI: ${run.riskProfile} - ${System.currentTimeMillis()}",
            totalWeight = 100.0,
            cashBufferPct = cashBufferPct
        )
        val remainingWeight = 100.0 - cashBufferPct
        val weightPerStock = if (selected.isNotEmpty()) remainingWeight / selected.size else 0.0
        val items = selected.map { 
            KaziBasketItem(basketId = 0, symbol = it.symbol, weightPct = weightPerStock)
        }
        return basket to items
    }
}
