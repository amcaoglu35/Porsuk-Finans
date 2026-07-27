package com.nexus.porsuk.data.engine

import com.nexus.porsuk.data.repository.FinanceRepository
import com.nexus.porsuk.domain.model.*
import com.nexus.porsuk.domain.repository.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AutomationEngine @Inject constructor(
    private val automationRepository: AutomationRepository,
    private val notificationRepository: NotificationRepository,
    private val financeRepository: FinanceRepository,
    private val orakulRepository: FinancialAnalysisRepository,
    private val technicalRepository: TechnicalAnalysisRepository,
    private val riskRepository: OrakulRiskRepository
) {
    suspend fun runAutomations() {
        val rules = automationRepository.getAutomationRules().first()
        for (rule in rules) {
            if (rule.isEnabled) {
                evaluateAndExecute(rule)
            }
        }
    }

    private suspend fun evaluateAndExecute(rule: AutomationRuleModel) {
        val startTime = System.currentTimeMillis()
        var status = "SKIPPED"
        var summary = "Koşullar sağlanmadı."
        
        try {
            val isConditionMet = checkCondition(rule.ifConditionText)
            
            if (isConditionMet) {
                executeAction(rule.actionText, rule.title)
                status = "SUCCESS"
                summary = "Aksiyon başarıyla tamamlandı: ${rule.actionText}"
            }
            
            automationRepository.saveHistory(
                AutomationHistoryModel(
                    ruleId = rule.ruleId,
                    executionTime = startTime,
                    durationMs = System.currentTimeMillis() - startTime,
                    status = status,
                    resultSummary = summary
                )
            )
        } catch (e: Exception) {
            automationRepository.saveHistory(
                AutomationHistoryModel(
                    ruleId = rule.ruleId,
                    executionTime = startTime,
                    durationMs = System.currentTimeMillis() - startTime,
                    status = "FAILED",
                    resultSummary = "Hata: ${e.localizedMessage}"
                )
            )
        }
    }

    private suspend fun checkCondition(condition: String): Boolean {
        // Advanced parsing logic would be here
        // Example: IF AI Score > 90 AND Risk < 25
        
        if (condition.contains("AI Score > 90")) {
            val symbols = listOf("THYAO.IS", "EREGL.IS", "TUPRS.IS")
            for (symbol in symbols) {
                orakulRepository.getFinancialAnalysis(symbol).firstOrNull()
            }
        }
        
        if (condition.contains("RSI < 30")) {
            technicalRepository.getTechnicalAnalysis("THYAO.IS").firstOrNull()
        }

        // Default to true for template demo
        return true
    }

    private suspend fun executeAction(action: String, ruleTitle: String) {
        when {
            action.contains("NOTIFY") || action.contains("Bildirim") -> {
                notificationRepository.saveNotification(
                    NotificationCenterItem(
                        title = "🤖 Otomasyon: $ruleTitle",
                        message = "Belirlenen kural tetiklendi ve işleminiz başarıyla tamamlandı.",
                        category = AlertCategory.AI_ORAKUL_STUB,
                        priority = NotificationPriority.MEDIUM
                    )
                )
            }
            action.contains("WATCHLIST") -> {
                financeRepository.addToWatchlist("THYAO")
            }
            action.contains("ALARM") -> {
                financeRepository.insertPriceAlert(
                    com.nexus.porsuk.data.local.entity.PriceAlert(
                        symbol = "THYAO",
                        market = "BIST",
                        targetPrice = 300.0,
                        isAbove = true
                    )
                )
            }
        }
    }
}
