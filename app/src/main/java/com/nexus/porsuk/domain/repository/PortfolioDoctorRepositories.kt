package com.nexus.porsuk.domain.repository

import com.nexus.porsuk.domain.model.*
import kotlinx.coroutines.flow.Flow

/**
 * 1. Genel Portföy Doktoru Deposu Sözleşmesi (PortfolioDoctorRepository)
 */
interface PortfolioDoctorRepository {
    fun getDoctorReport(): Flow<PortfolioDoctorReport>
}

/**
 * 2. Portföy Sağlık Skoru Deposu Sözleşmesi (PortfolioHealthRepository)
 */
interface PortfolioHealthRepository {
    fun getHealthScore(): Flow<Int>
}

/**
 * 3. Çeşitlendirme Deposu Sözleşmesi (DiversificationRepository)
 */
interface DiversificationRepository {
    fun getDiversificationData(): Flow<DoctorDiversificationData>
}

/**
 * 4. Portföy Analitikleri Deposu Sözleşmesi (PortfolioAnalyticsRepository)
 */
interface PortfolioAnalyticsRepository {
    fun getPerformanceData(): Flow<DoctorPerformanceData>
    fun getIncomeData(): Flow<DoctorIncomeData>
}
