package com.nexus.porsuk.data.remote.dto

import com.google.gson.annotations.SerializedName

data class IncomeStatementDto(
    val date: String,
    val symbol: String,
    val revenue: Double,
    val costOfRevenue: Double,
    val grossProfit: Double,
    val ebitda: Double,
    val operatingIncome: Double,
    val netIncome: Double,
    val eps: Double,
    val weightedAverageShsOut: Double
)

data class BalanceSheetDto(
    val date: String,
    val symbol: String,
    val cashAndCashEquivalents: Double,
    val shortTermInvestments: Double,
    val inventory: Double,
    val totalAssets: Double,
    val accountPayables: Double,
    val totalLiabilities: Double,
    val totalStockholdersEquity: Double,
    val totalDebt: Double,
    val netDebt: Double
)

data class CashFlowDto(
    val date: String,
    val symbol: String,
    val netIncome: Double,
    val depreciationAndAmortization: Double,
    val inventory: Double,
    val netCashProvidedByOperatingActivities: Double,
    val capitalExpenditure: Double,
    val freeCashFlow: Double
)

data class KeyMetricsDto(
    val symbol: String,
    val date: String,
    val revenuePerShare: Double,
    val netIncomePerShare: Double,
    val operatingCashFlowPerShare: Double,
    val freeCashFlowPerShare: Double,
    val cashPerShare: Double,
    val bookValuePerShare: Double,
    val tangibleBookValuePerShare: Double,
    val shareholdersEquityPerShare: Double,
    val interestCoverage: Double?,
    val debtToEquity: Double,
    val debtToAssets: Double,
    val netDebtToEBITDA: Double?,
    val currentRatio: Double,
    val quickRatio: Double,
    val roe: Double,
    val roic: Double?
)

data class CompanyRatioDto(
    val symbol: String,
    val date: String,
    val currentRatio: Double,
    val quickRatio: Double,
    val cashRatio: Double,
    val debtEquityRatio: Double,
    val netProfitMargin: Double,
    val returnOnEquity: Double,
    val returnOnAssets: Double,
    val returnOnCapitalEmployed: Double,
    val priceEarningsRatio: Double,
    val priceToBookRatio: Double,
    val priceToSalesRatio: Double,
    val dividendYield: Double?
)

data class CompanyProfileDto(
    val symbol: String,
    val price: Double,
    val beta: Double,
    val volAvg: Long,
    val mktCap: Double,
    val lastDiv: Double,
    val range: String,
    val changes: Double,
    val companyName: String,
    val currency: String,
    val cik: String?,
    val isin: String?,
    val cusip: String?,
    val exchange: String,
    val exchangeShortName: String,
    val industry: String,
    val website: String,
    val description: String,
    val ceo: String,
    val sector: String,
    val country: String,
    val fullTimeEmployees: String?,
    val phone: String?,
    val address: String?,
    val city: String?,
    val state: String?,
    val zip: String?,
    val image: String?
)
