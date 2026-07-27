package com.nexus.porsuk.data.repository

import com.nexus.porsuk.data.local.dao.NotificationAutomationDao
import com.nexus.porsuk.data.local.entity.*
import com.nexus.porsuk.domain.model.*
import com.nexus.porsuk.domain.repository.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepositoryImpl @Inject constructor(
    private val dao: NotificationAutomationDao
) : NotificationRepository {

    override fun getNotifications(): Flow<List<NotificationCenterItem>> {
        return dao.getAllNotifications().map { list ->
            if (list.isEmpty()) {
                listOf(
                    NotificationCenterItem(
                        title = "🚀 THYAO.IS %4.2 Yükseldi",
                        message = "Türk Hava Yolları 284.5 TL seviyesine ulaşarak günlük zirvesini tazeledi.",
                        category = AlertCategory.PRICE,
                        priority = NotificationPriority.HIGH
                    ),
                    NotificationCenterItem(
                        title = "📅 TCMB Faiz Kararı Hatırlatması",
                        message = "Türkiye Cumhuriyet Merkez Bankası faiz kararı bugün saat 14:00'te açıklanacak.",
                        category = AlertCategory.ECONOMIC_CALENDAR,
                        priority = NotificationPriority.CRITICAL
                    )
                )
            } else {
                list.map { entity ->
                    NotificationCenterItem(
                        notificationId = entity.notificationId,
                        title = entity.title,
                        message = entity.message,
                        category = AlertCategory.valueOf(entity.categoryName),
                        priority = NotificationPriority.valueOf(entity.priorityName),
                        isRead = entity.isRead,
                        isPinned = entity.isPinned
                    )
                }
            }
        }
    }

    override suspend fun saveNotification(item: NotificationCenterItem) {
        val entity = NotificationCenterEntity(
            notificationId = item.notificationId,
            title = item.title,
            message = item.message,
            categoryName = item.category.name,
            priorityName = item.priority.name,
            isRead = item.isRead,
            isPinned = item.isPinned
        )
        dao.insertNotification(entity)
    }
}

@Singleton
class AlertRepositoryImpl @Inject constructor(
    private val notificationRepository: NotificationRepository
) : AlertRepository {
    override fun getActiveAlerts(): Flow<List<NotificationCenterItem>> {
        return notificationRepository.getNotifications()
    }
}

@Singleton
class AutomationRepositoryImpl @Inject constructor(
    private val dao: NotificationAutomationDao
) : AutomationRepository {

    override fun getAutomationRules(): Flow<List<AutomationRuleModel>> {
        return dao.getAllRules().map { list ->
            list.map { entity ->
                AutomationRuleModel(
                    ruleId = entity.ruleId,
                    title = entity.title,
                    category = AlertCategory.valueOf(entity.categoryName),
                    ifConditionText = entity.ifCondition,
                    actionText = entity.actionText,
                    priority = NotificationPriority.valueOf(entity.priorityName),
                    isEnabled = entity.isEnabled
                )
            }
        }
    }

    override suspend fun saveRule(rule: AutomationRuleModel) {
        val entity = AutomationRuleEntity(
            ruleId = rule.ruleId,
            title = rule.title,
            categoryName = rule.category.name,
            ifCondition = rule.ifConditionText,
            actionText = rule.actionText,
            priorityName = rule.priority.name,
            isEnabled = rule.isEnabled
        )
        dao.insertRule(entity)
    }

    override suspend fun deleteRule(ruleId: String) {
        dao.deleteRule(ruleId)
    }

    override fun getExecutionHistory(): Flow<List<AutomationHistoryModel>> {
        return dao.getExecutionHistory().map { list ->
            list.map { entity ->
                AutomationHistoryModel(
                    id = entity.id,
                    ruleId = entity.ruleId,
                    executionTime = entity.executionTime,
                    durationMs = entity.durationMs,
                    status = entity.status,
                    resultSummary = entity.resultSummary,
                    suggestions = entity.suggestions
                )
            }
        }
    }

    override suspend fun saveHistory(history: AutomationHistoryModel) {
        val entity = AutomationHistoryEntity(
            ruleId = history.ruleId,
            executionTime = history.executionTime,
            durationMs = history.durationMs,
            status = history.status,
            resultSummary = history.resultSummary,
            suggestions = history.suggestions
        )
        dao.insertHistory(entity)
    }

    override fun getAiSuggestions(): Flow<List<AiAutomationSuggestionModel>> {
        return dao.getAiSuggestions().map { list ->
            list.map { entity ->
                AiAutomationSuggestionModel(
                    suggestionId = entity.suggestionId,
                    title = entity.title,
                    description = entity.description,
                    type = entity.type,
                    isApplied = entity.isApplied,
                    createdAt = entity.createdAt
                )
            }
        }
    }

    override suspend fun markSuggestionApplied(suggestionId: String) {
        dao.markSuggestionAsApplied(suggestionId)
    }
}

@Singleton
class WorkflowRepositoryImpl @Inject constructor() : WorkflowRepository {
    override fun getWorkflows(): Flow<List<AutomationWorkflow>> = flow {
        emit(
            listOf(
                AutomationWorkflow("wf1", "Sabah Piyasa Açılış Bülteni", "Her Gün 09:30", "BIST ve küresel açılış haberlerini AI özeti ile sunar."),
                AutomationWorkflow("wf2", "Haftalık Portföy Sağlık Raporu", "Her Pazar 20:00", "Portfolio Doctor sağlık skorunu otomatik raporlar.")
            )
        )
    }
}
