package com.nexus.porsuk.data.engine

import com.nexus.porsuk.domain.model.FundIntelligence
import com.nexus.porsuk.domain.model.FundScreenerCriteria
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FundScreenerEngine @Inject constructor() {
    fun screen(funds: List<FundIntelligence>, criteria: FundScreenerCriteria): List<FundIntelligence> {
        return funds.filter { fund ->
            var matches = true
            
            criteria.categories?.let {
                if (!it.contains(fund.type)) matches = false
            }
            
            criteria.maxExpenseRatio?.let {
                if (fund.expenseRatio > it) matches = false
            }
            
            criteria.minDividendYield?.let {
                if ((fund.dividendYield ?: 0.0) < it) matches = false
            }
            
            criteria.maxRiskLevel?.let {
                if (fund.riskLevel > it) matches = false
            }
            
            matches
        }
    }
}
