package com.nexus.porsuk.ui.ledger

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexus.porsuk.data.local.entity.PortfolioTransaction
import com.nexus.porsuk.data.repository.FinanceRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class LedgerUiState(
    val transactions: List<PortfolioTransaction> = emptyList(),
    val totalRealizedPnL: Double = 0.0
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TransactionLedgerViewModel @Inject constructor(
    private val repository: FinanceRepository
) : ViewModel() {

    val uiState: StateFlow<LedgerUiState> = repository.getAllTransactionsFlow()
        .map { list ->
            val totalPnL = list.sumOf { it.realizedPnL }
            LedgerUiState(
                transactions = list,
                totalRealizedPnL = totalPnL
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = LedgerUiState()
        )

    fun deleteTransaction(transaction: PortfolioTransaction) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
        }
    }

    fun updateTransaction(transaction: PortfolioTransaction) {
        viewModelScope.launch {
            repository.updateTransaction(transaction)
        }
    }
}
