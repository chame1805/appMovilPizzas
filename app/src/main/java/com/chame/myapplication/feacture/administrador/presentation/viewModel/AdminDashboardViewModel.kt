package com.chame.myapplication.feacture.administrador.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chame.myapplication.feacture.administrador.domain.usescases.GetSalesHistoryUseCase
import com.chame.myapplication.feacture.administrador.presentation.screens.AdminDashboardUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class AdminDashboardViewModel @Inject constructor(
    private val getSalesHistoryUseCase: GetSalesHistoryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminDashboardUiState())
    val uiState: StateFlow<AdminDashboardUiState> = _uiState.asStateFlow()

    init {
        loadSales()
    }

    fun loadSales() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            getSalesHistoryUseCase().onSuccess { sales ->
                _uiState.update { it.copy(isLoading = false, sales = sales) }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Error al cargar ventas") }
            }
        }
    }

    fun setFilterStatus(status: String?) {
        _uiState.update { it.copy(filterStatus = status) }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun exportCsv(): String {
        val sales = _uiState.value.sales
        val sb = StringBuilder()
        sb.appendLine("ID,Pizza,Cliente,Mesa,Precio,Total Pagado,Cambio,Estado,Fecha")
        sales.forEach { s ->
            sb.appendLine("${s.id},\"${s.pizzaName}\",\"${s.clientName}\",${s.tableNumber},${s.price},${s.totalPaid},${s.changeReturned},${s.status},${s.createdAt}")
        }
        return sb.toString()
    }
}
