package com.nexus.porsuk.data.repository

import com.nexus.porsuk.data.engine.*
import com.nexus.porsuk.domain.model.*
import com.nexus.porsuk.domain.repository.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PortfolioDoctorRepositoryImpl @Inject constructor(
    private val reportGeneratorEngine: PortfolioDoctorReportGeneratorEngine
) : PortfolioDoctorRepository {
    override fun getDoctorReport(): Flow<PortfolioDoctorReport> = flow {
        emit(reportGeneratorEngine.generateDoctorReport())
    }
}

@Singleton
class PortfolioHealthRepositoryImpl @Inject constructor(
    private val reportGeneratorEngine: PortfolioDoctorReportGeneratorEngine
) : PortfolioHealthRepository {
    override fun getHealthScore(): Flow<Int> = flow {
        emit(reportGeneratorEngine.generateDoctorReport().healthScore)
    }
}

@Singleton
class DiversificationRepositoryImpl @Inject constructor(
    private val diversificationEngine: DoctorDiversificationEngine
) : DiversificationRepository {
    override fun getDiversificationData(): Flow<DoctorDiversificationData> = flow {
        emit(diversificationEngine.calculateDiversification())
    }
}

@Singleton
class PortfolioAnalyticsRepositoryImpl @Inject constructor(
    private val performanceEngine: DoctorPerformanceEngine,
    private val incomeEngine: DoctorIncomeEngine
) : PortfolioAnalyticsRepository {

    override fun getPerformanceData(): Flow<DoctorPerformanceData> = flow {
        emit(performanceEngine.calculatePerformance())
    }

    override fun getIncomeData(): Flow<DoctorIncomeData> = flow {
        emit(incomeEngine.calculateIncome())
    }
}
