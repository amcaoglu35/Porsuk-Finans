package com.nexus.porsuk.domain.repository

import com.nexus.porsuk.domain.model.*
import kotlinx.coroutines.flow.Flow

/**
 * 1. Bildirim Merkezi Deposu Sözleşmesi (NotificationRepository)
 */
interface NotificationRepository {
    fun getNotifications(): Flow<List<NotificationCenterItem>>
    suspend fun saveNotification(item: NotificationCenterItem)
}

/**
 * 2. Alarm Deposu Sözleşmesi (AlertRepository)
 */
interface AlertRepository {
    fun getActiveAlerts(): Flow<List<NotificationCenterItem>>
}

/**
 * 3. Otomasyon Kural Deposu Sözleşmesi (AutomationRepository)
 */
interface AutomationRepository {
    fun getAutomationRules(): Flow<List<AutomationRuleModel>>
    suspend fun saveRule(rule: AutomationRuleModel)
}

/**
 * 4. İş Akışları Deposu Sözleşmesi (WorkflowRepository)
 */
interface WorkflowRepository {
    fun getWorkflows(): Flow<List<AutomationWorkflow>>
}
