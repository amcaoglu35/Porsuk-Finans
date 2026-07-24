package com.nexus.porsuk.data.repository

import com.nexus.porsuk.data.local.dao.NotificationAutomationDao
import com.nexus.porsuk.data.local.entity.AutomationRuleEntity
import com.nexus.porsuk.data.local.entity.NotificationCenterEntity
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
            if (list.isEmpty()) {
                listOf(
                    AutomationRuleModel(
                        title = "RSI Aşırı Satım Alarmlı Alım",
                        category = AlertCategory.PERCENT_CHANGE,
                        ifConditionText = "RSI (14) < 30 VE Fiyat > 250",
                        actionText = "Bildirim Gönder & Takip Listesine Ekle",
                        priority = NotificationPriority.HIGH
                    )
                )
            } else {
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
