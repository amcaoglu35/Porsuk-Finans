package com.nexus.porsuk.data.remote

import com.google.ai.client.generativeai.type.content
import com.nexus.porsuk.data.local.entity.*
import com.nexus.porsuk.ui.common.GeminiErrorParser
import com.nexus.porsuk.ui.common.GeminiModels
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

/**
 * Centralized AI Service for Porsuk Finans.
 * Integrates AiPredictionEngine for probabilistic forecasting without explicit price target guessing.
 */
class GeminiService(private val apiKey: String) {

    private val systemInstructionContent = content {
        text(GeminiPromptBuilder.buildSystemInstruction())
    }

    private suspend fun executeWithFallback(prompt: String): String {
        return GeminiModels.generateContentWithFallback(
            apiKey = apiKey,
            prompt = prompt,
            systemInstruction = systemInstructionContent
        )
    }

    /**
     * Parse raw response into structured AiAnalysisResponse, falling back to rawText if JSON parsing fails.
     */
    private fun formatResponse(rawResult: String): String {
        return AiAnalysisResponse.parseFromJsonOrRaw(rawResult).toFormattedMarkdown()
    }

    /**
     * Clear / invalidate portfolio cache entries when holdings or user transactions change.
     */
    fun invalidatePortfolioCache() {
        AiCacheManager.invalidatePortfolioCache()
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // 1. CHAT AI (Injected with Single-Paragraph Market Intelligence & Cached)
    // ─────────────────────────────────────────────────────────────────────────────
    suspend fun chat(prompt: String, portfolioContext: String = "", webContext: String = ""): String = withContext(Dispatchers.IO) {
        val macroParagraph = MarketIntelligenceEngine.getMarketSummaryParagraph()
        val brainContext = PorsukBrainManager.buildBrainContext(null)

        // 1. Run Profesör AI 2.0 Multi-Agent & Consensus Engine (0 AI Token Cost)
        val agentReq = com.nexus.porsuk.data.remote.agents.AgentRequest(
            symbol = prompt.take(10).trim()
        )
        val multiAgentSummary = com.nexus.porsuk.data.remote.agents.MasterAiOrchestrator.runMultiAgentPipeline(agentReq)
        val consensus = com.nexus.porsuk.data.remote.agents.ProfesorConsensusEngine.evaluate(agentReq, multiAgentSummary)

        val combinedWeb = "$brainContext\n$macroParagraph\n${consensus.structuredSummary}" + (if (webContext.isNotBlank()) "\n$webContext" else "")

        val pHash = portfolioContext.hashCode()
        val cacheKey = AiCacheManager.generateKey("chat", prompt = prompt, portfolioHash = pHash)
        val cached = AiCacheManager.get(cacheKey)
        if (cached != null) return@withContext cached

        try {
            val fullPrompt = GeminiPromptBuilder.buildChatPrompt(prompt, portfolioContext, combinedWeb)
            val result = executeWithFallback(fullPrompt)
            AiCacheManager.put(cacheKey, result, isPortfolioRelated = portfolioContext.isNotBlank())
            result
        } catch (e: Exception) {
            GeminiErrorParser.parse(e)
        }
    }

    fun chatStream(prompt: String, portfolioContext: String = "", webContext: String = ""): Flow<String> {
        val fullPrompt = GeminiPromptBuilder.buildChatPrompt(prompt, portfolioContext, webContext)
        return GeminiModels.generateContentStreamWithFallback(
            apiKey = apiKey,
            prompt = fullPrompt,
            systemInstruction = systemInstructionContent
        )
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // 2. PORTFOLIO AI (AI Portfolio Doctor Engine & Cached)
    // ─────────────────────────────────────────────────────────────────────────────
    suspend fun getPortfolioHealthCheck(
        holdings: List<BasketItem>,
        companies: List<Company>
    ): String = withContext(Dispatchers.IO) {
        val pHash = holdings.map { "${it.symbol}_${it.quantity}" }.hashCode()
        val cacheKey = AiCacheManager.generateKey("portfolio_health", portfolioHash = pHash)
        val cached = AiCacheManager.get(cacheKey)
        if (cached != null) return@withContext cached

        try {
            val macroParagraph = MarketIntelligenceEngine.getMarketSummaryParagraph()
            val doctorMetrics = PortfolioDoctorEngine.analyze(holdings, companies)
            val combinedSummary = "$macroParagraph\n${doctorMetrics.diagnosisSummary}"

            val prompt = GeminiPromptBuilder.buildPortfolioDoctorPrompt(combinedSummary)
            val result = executeWithFallback(prompt)
            AiCacheManager.put(cacheKey, result, isPortfolioRelated = true)
            result
        } catch (e: Exception) {
            GeminiErrorParser.parse(e)
        }
    }

    suspend fun getPortfolioRebalanceReport(
        holdings: List<BasketItem>,
        companies: List<Company>
    ): String = withContext(Dispatchers.IO) {
        val pHash = holdings.map { "${it.symbol}_${it.quantity}_${it.buyPrice}" }.hashCode()
        val cacheKey = AiCacheManager.generateKey("portfolio_rebalance", portfolioHash = pHash)
        val cached = AiCacheManager.get(cacheKey)
        if (cached != null) return@withContext cached

        try {
            val prompt = GeminiPromptBuilder.buildPortfolioRebalancePrompt(holdings, companies)
            val raw = executeWithFallback(prompt)
            val formatted = formatResponse(raw)
            AiCacheManager.put(cacheKey, formatted, isPortfolioRelated = true)
            formatted
        } catch (e: Exception) {
            GeminiErrorParser.parse(e)
        }
    }

    suspend fun getInvestmentRecommendations(companies: List<Company>): String = withContext(Dispatchers.IO) {
        val compHash = companies.take(10).map { it.symbol }.hashCode()
        val cacheKey = AiCacheManager.generateKey("recommendations", portfolioHash = compHash)
        val cached = AiCacheManager.get(cacheKey)
        if (cached != null) return@withContext cached

        try {
            val prompt = GeminiPromptBuilder.buildInvestmentRecommendationsPrompt(companies)
            val raw = executeWithFallback(prompt)
            val formatted = formatResponse(raw)
            AiCacheManager.put(cacheKey, formatted)
            formatted
        } catch (e: Exception) {
            GeminiErrorParser.parse(e)
        }
    }

    suspend fun runFundamentalScreener(template: String, companies: List<Company>): String = withContext(Dispatchers.IO) {
        val cacheKey = AiCacheManager.generateKey("screener", prompt = template)
        val cached = AiCacheManager.get(cacheKey)
        if (cached != null) return@withContext cached

        try {
            val prompt = GeminiPromptBuilder.buildFundamentalScreenerPrompt(template, companies)
            val raw = executeWithFallback(prompt)
            val formatted = formatResponse(raw)
            AiCacheManager.put(cacheKey, formatted)
            formatted
        } catch (e: Exception) {
            GeminiErrorParser.parse(e)
        }
    }

    suspend fun getBasketOptimization(portfolioText: String): String = withContext(Dispatchers.IO) {
        val pHash = portfolioText.hashCode()
        val cacheKey = AiCacheManager.generateKey("basket_opt", portfolioHash = pHash)
        val cached = AiCacheManager.get(cacheKey)
        if (cached != null) return@withContext cached

        try {
            val prompt = GeminiPromptBuilder.buildBasketOptimizationPrompt(portfolioText)
            val raw = executeWithFallback(prompt)
            val formatted = formatResponse(raw)
            AiCacheManager.put(cacheKey, formatted, isPortfolioRelated = true)
            formatted
        } catch (e: Exception) {
            GeminiErrorParser.parse(e)
        }
    }

    suspend fun getBasketOrakulComment(
        finalBasketReturn: Double,
        bistReturn: Double,
        usdReturn: Double,
        holdings: List<BasketItem>
    ): String = withContext(Dispatchers.IO) {
        val pHash = holdings.map { "${it.symbol}_${it.quantity}" }.hashCode()
        val cacheKey = AiCacheManager.generateKey("basket_orakul_comment", portfolioHash = pHash)
        val cached = AiCacheManager.get(cacheKey)
        if (cached != null) return@withContext cached

        try {
            val prompt = GeminiPromptBuilder.buildBasketOrakulCommentPrompt(finalBasketReturn, bistReturn, usdReturn, holdings)
            val raw = executeWithFallback(prompt)
            val formatted = formatResponse(raw)
            AiCacheManager.put(cacheKey, formatted, isPortfolioRelated = true)
            formatted
        } catch (e: Exception) {
            GeminiErrorParser.parse(e)
        }
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // 3. NEWS AI & SENTIMENT (Cached)
    // ─────────────────────────────────────────────────────────────────────────────
    suspend fun analyzeNewsSentiment(titles: List<String>): List<String> = withContext(Dispatchers.IO) {
        val nHash = titles.hashCode()
        val cacheKey = AiCacheManager.generateKey("news_sentiment", newsHash = nHash)
        val cached = AiCacheManager.get(cacheKey)
        if (cached != null) {
            return@withContext cached.split(",").map { it.trim().uppercase() }
        }

        try {
            val prompt = GeminiPromptBuilder.buildNewsAnalysisPrompt(titles)
            val result = executeWithFallback(prompt)
            if (result.isNotBlank()) {
                AiCacheManager.put(cacheKey, result)
                result.split(",").map { it.trim().uppercase() }
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Stock Analysis powered by MarketIntelligenceEngine & DecisionEngine.
     * Pre-computes market macro summary and technical metrics in Kotlin to minimize token usage.
     */
    suspend fun getStockAnalysis(
        symbol: String,
        companyInfo: CachedCompanyInfo?,
        price: PriceSnapshot?,
        news: List<NewsItemEntity>,
        userCost: Double = 0.0,
        historicalPrices: List<Double> = emptyList(),
        volumes: List<Double> = emptyList()
    ): String = withContext(Dispatchers.IO) {
        val nHash = news.map { it.title }.hashCode()
        val cacheKey = AiCacheManager.generateKey("stock_analysis", symbol = symbol, newsHash = nHash)
        val cached = AiCacheManager.get(cacheKey)
        if (cached != null) return@withContext cached

        // 1. RUN MULTI-AGENT PIPELINE (PORTFOLIO, NEWS, TECHNICAL, MACRO, RISK, BRAIN AGENTS - 0 AI TOKEN COST)
        val newsTitles = news.map { it.title }
        val priceList = if (historicalPrices.isNotEmpty()) historicalPrices else listOfNotNull(price?.price ?: companyInfo?.week52Low)
        val agentRequest = com.nexus.porsuk.data.remote.agents.AgentRequest(
            symbol = symbol,
            historicalPrices = priceList,
            volumes = volumes,
            newsTitles = newsTitles
        )
        val multiAgentSummary = com.nexus.porsuk.data.remote.agents.MasterAiOrchestrator.runMultiAgentPipeline(agentRequest)

        // 2. BUILD PROMPT WITH MULTI-AGENT DIAGNOSIS
        val prompt = GeminiPromptBuilder.buildStockAnalysisPrompt(
            symbol = symbol,
            companyInfo = companyInfo,
            price = price,
            news = news,
            userCost = userCost,
            decisionSummary = multiAgentSummary
        )

        // 3. GEMINI SYNTHESIZES EXPERT ANALYSIS (MINIMUM TOKENS)
        try {
            val raw = executeWithFallback(prompt)
            val formatted = formatResponse(raw)
            AiCacheManager.put(cacheKey, formatted)
            formatted
        } catch (e: Exception) {
            "O-EAGI SKORU: 0\nGÜVENLİK MARJI: 0\nHABER ENTROPİSİ: 0\nMOMENTUM: 0\nSEKTÖR ALFA: 0\n---\n${GeminiErrorParser.parse(e)}"
        }
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // 4. TECHNICAL AI SUMMARY (Powered by MarketIntelligenceEngine & DecisionEngine)
    // ─────────────────────────────────────────────────────────────────────────────
    suspend fun getTechnicalSummary(
        symbol: String,
        technicalData: Map<String, String>,
        historicalPrices: List<Double> = emptyList(),
        volumes: List<Double> = emptyList()
    ): String = withContext(Dispatchers.IO) {
        val cacheKey = AiCacheManager.generateKey("technical_summary", symbol = symbol, prompt = technicalData.toString())
        val cached = AiCacheManager.get(cacheKey)
        if (cached != null) return@withContext cached

        val macroParagraph = MarketIntelligenceEngine.getMarketSummaryParagraph()
        val decisionResult = DecisionEngine.analyze(historicalPrices, volumes)
        val summaryText = "$macroParagraph\n" + (if (historicalPrices.isNotEmpty()) decisionResult.preComputedSummary else technicalData.entries.joinToString("\n") { "${it.key}: ${it.value}" })
        val prompt = GeminiPromptBuilder.buildTechnicalSummaryPrompt(symbol, summaryText)

        try {
            val raw = executeWithFallback(prompt)
            val formatted = formatResponse(raw)
            AiCacheManager.put(cacheKey, formatted)
            formatted
        } catch (e: Exception) {
            GeminiErrorParser.parse(e)
        }
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // 5. RISK AI (JSON Structured & Cached)
    // ─────────────────────────────────────────────────────────────────────────────
    suspend fun getRiskAnalysis(symbol: String, riskMetrics: Map<String, String>): String = withContext(Dispatchers.IO) {
        val cacheKey = AiCacheManager.generateKey("risk_analysis", symbol = symbol, prompt = riskMetrics.toString())
        val cached = AiCacheManager.get(cacheKey)
        if (cached != null) return@withContext cached

        try {
            val prompt = GeminiPromptBuilder.buildRiskAnalysisPrompt(symbol, riskMetrics)
            val raw = executeWithFallback(prompt)
            val formatted = formatResponse(raw)
            AiCacheManager.put(cacheKey, formatted)
            formatted
        } catch (e: Exception) {
            GeminiErrorParser.parse(e)
        }
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // 6. AI PREDICTION ENGINE (Probabilistic Forecasts & Cached)
    // ─────────────────────────────────────────────────────────────────────────────
    suspend fun getFutureForecast(symbol: String, historicalPrices: List<Double>): String = withContext(Dispatchers.IO) {
        val cacheKey = AiCacheManager.generateKey("future_forecast", symbol = symbol, prompt = historicalPrices.takeLast(10).toString())
        val cached = AiCacheManager.get(cacheKey)
        if (cached != null) return@withContext cached

        try {
            val macroParagraph = MarketIntelligenceEngine.getMarketSummaryParagraph()
            val predictionSignals = AiPredictionEngine.analyze(symbol, historicalPrices, macroParagraph = macroParagraph)
            val prompt = GeminiPromptBuilder.buildPredictionEnginePrompt(symbol, predictionSignals.singleParagraphSummary)

            val raw = executeWithFallback(prompt)
            val formatted = formatResponse(raw)
            AiCacheManager.put(cacheKey, formatted)
            formatted
        } catch (e: Exception) {
            GeminiErrorParser.parse(e)
        }
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // 7. ORAKUL & KAZI & WORKER TASKS
    // ─────────────────────────────────────────────────────────────────────────────
    fun getOrakulStream(
        prompt: String
    ): Flow<String> {
        return GeminiModels.generateContentStreamWithFallback(
            apiKey = apiKey,
            prompt = prompt,
            systemInstruction = systemInstructionContent
        )
    }

    suspend fun generateKaziThesis(symbol: String, kaziRunName: String, reasoningDepth: String): String = withContext(Dispatchers.IO) {
        val cacheKey = AiCacheManager.generateKey("kazi_thesis", symbol = symbol, prompt = "$kaziRunName:$reasoningDepth")
        val cached = AiCacheManager.get(cacheKey)
        if (cached != null) return@withContext cached

        try {
            val prompt = GeminiPromptBuilder.buildKaziThesisPrompt(symbol, kaziRunName, reasoningDepth)
            val result = executeWithFallback(prompt)
            AiCacheManager.put(cacheKey, result)
            result
        } catch (e: Exception) {
            GeminiErrorParser.parse(e)
        }
    }

    suspend fun getMorningInsight(symbols: String): String = withContext(Dispatchers.IO) {
        val cacheKey = AiCacheManager.generateKey("morning_insight", prompt = symbols)
        val cached = AiCacheManager.get(cacheKey)
        if (cached != null) return@withContext cached

        try {
            val prompt = GeminiPromptBuilder.buildMorningInsightPrompt(symbols)
            val result = executeWithFallback(prompt)
            AiCacheManager.put(cacheKey, result)
            result
        } catch (e: Exception) {
            "Piyasalar açılıyor, bol kazançlar!"
        }
    }

    suspend fun generatePortfolioAiInsight(
        assets: List<com.nexus.porsuk.domain.model.PortfolioAsset>,
        metrics: PortfolioDoctorMetrics
    ): String = withContext(Dispatchers.IO) {
        val pHash = assets.hashCode() + metrics.healthScore
        val cacheKey = AiCacheManager.generateKey("portfolio_insight", portfolioHash = pHash)
        val cached = AiCacheManager.get(cacheKey)
        if (cached != null) return@withContext cached

        try {
            val prompt = GeminiPromptBuilder.buildPortfolioInsightPrompt(assets, metrics)
            val result = executeWithFallback(prompt)
            val formatted = formatResponse(result)
            AiCacheManager.put(cacheKey, formatted, isPortfolioRelated = true)
            formatted
        } catch (e: Exception) {
            GeminiErrorParser.parse(e)
        }
    }

    suspend fun getDetailedCompanyAnalysis(
        symbol: String,
        income: List<IncomeStatementEntity>,
        balance: List<BalanceSheetEntity>,
        cashFlow: List<CashFlowEntity>,
        ratios: List<CompanyRatioEntity>
    ): String = withContext(Dispatchers.IO) {
        val cacheKey = AiCacheManager.generateKey("detailed_analysis", symbol = symbol)
        val cached = AiCacheManager.get(cacheKey)
        if (cached != null) return@withContext cached

        try {
            val prompt = GeminiPromptBuilder.buildDetailedCompanyAnalysisPrompt(symbol, income, balance, cashFlow, ratios)
            val result = executeWithFallback(prompt)
            AiCacheManager.put(cacheKey, result)
            result
        } catch (e: Exception) {
            GeminiErrorParser.parse(e)
        }
    }

    suspend fun getAiOracleReport(
        symbol: String,
        currentPrice: Double,
        income: List<IncomeStatementEntity>,
        ratios: List<CompanyRatioEntity>
    ): String = withContext(Dispatchers.IO) {
        val cacheKey = AiCacheManager.generateKey("ai_oracle_v2", symbol = symbol)
        val cached = AiCacheManager.get(cacheKey)
        if (cached != null) return@withContext cached

        try {
            val prompt = GeminiPromptBuilder.buildAiOraclePrompt(symbol, currentPrice, income, ratios)
            val result = executeWithFallback(prompt)
            AiCacheManager.put(cacheKey, result)
            result
        } catch (e: Exception) {
            GeminiErrorParser.parse(e)
        }
    }

    suspend fun runLabTool(toolName: String, contextData: String): String = withContext(Dispatchers.IO) {
        val cacheKey = AiCacheManager.generateKey("lab_tool", prompt = "$toolName:$contextData")
        val cached = AiCacheManager.get(cacheKey)
        if (cached != null) return@withContext cached

        try {
            val prompt = GeminiPromptBuilder.buildLabToolPrompt(toolName, contextData)
            val result = executeWithFallback(prompt)
            val formatted = formatResponse(result)
            AiCacheManager.put(cacheKey, formatted)
            formatted
        } catch (e: Exception) {
            GeminiErrorParser.parse(e)
        }
    }

    suspend fun generateRawContent(prompt: String): String = withContext(Dispatchers.IO) {
        val cacheKey = AiCacheManager.generateKey("raw", prompt = prompt)
        val cached = AiCacheManager.get(cacheKey)
        if (cached != null) return@withContext cached

        try {
            val result = executeWithFallback(prompt)
            AiCacheManager.put(cacheKey, result)
            result
        } catch (e: Exception) {
            GeminiErrorParser.parse(e)
        }
    }
}
