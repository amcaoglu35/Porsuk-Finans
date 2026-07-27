package com.nexus.porsuk.di

import com.nexus.porsuk.data.local.PorsukDatabase
import com.nexus.porsuk.data.local.dao.NotificationAutomationDao
import com.nexus.porsuk.data.repository.*
import com.nexus.porsuk.domain.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NotificationAutomationDaoModule {

    @Provides
    fun provideNotificationAutomationDao(db: PorsukDatabase): NotificationAutomationDao {
        return db.notificationAutomationDao()
    }
}

@Module
@InstallIn(SingletonComponent::class)
object NotificationAutomationRepositoriesModule {

    @Provides
    @Singleton
    fun provideNotificationRepository(impl: NotificationRepositoryImpl): NotificationRepository = impl

    @Provides
    @Singleton
    fun provideAlertRepository(impl: AlertRepositoryImpl): AlertRepository = impl

    @Provides
    @Singleton
    fun provideAutomationRepository(impl: AutomationRepositoryImpl): AutomationRepository = impl

    @Provides
    @Singleton
    fun provideWorkflowRepository(impl: WorkflowRepositoryImpl): WorkflowRepository = impl

    @Provides
    @Singleton
    fun provideAutomationEngine(
        automationRepository: AutomationRepository,
        notificationRepository: NotificationRepository,
        financeRepository: com.nexus.porsuk.data.repository.FinanceRepository,
        orakulRepository: FinancialAnalysisRepository,
        technicalRepository: TechnicalAnalysisRepository,
        riskRepository: OrakulRiskRepository
    ): com.nexus.porsuk.data.engine.AutomationEngine {
        return com.nexus.porsuk.data.engine.AutomationEngine(
            automationRepository,
            notificationRepository,
            financeRepository,
            orakulRepository,
            technicalRepository,
            riskRepository
        )
    }
}
