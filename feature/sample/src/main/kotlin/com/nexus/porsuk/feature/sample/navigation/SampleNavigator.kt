package com.nexus.porsuk.feature.sample.navigation

interface SampleNavigator {
    fun navigateToStockList()
    fun navigateToStockAnalysis()
    fun navigateToStockDetail(symbol: String)
    fun navigateBack()
}
