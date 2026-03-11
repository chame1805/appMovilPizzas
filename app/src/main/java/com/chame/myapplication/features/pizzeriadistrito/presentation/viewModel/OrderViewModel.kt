package com.chame.myapplication.features.pizzeriadistrito.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chame.myapplication.features.pizzeriadistrito.domain.usecases.CreateOrderParams
import com.chame.myapplication.features.pizzeriadistrito.domain.usecases.CreateWaiterOrderUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class OrderUiState(
    val isLoading: Boolean = false,
    val orderSent: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val createWaiterOrderUseCase: CreateWaiterOrderUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(OrderUiState())
    val uiState: StateFlow<OrderUiState> = _uiState.asStateFlow()

    fun createOrder(
        pizzaName: String,
        price: Double,
        clientName: String,
        tableNumber: Int,
        totalPaid: Double,
        changeReturned: Double
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            createWaiterOrderUseCase(
                CreateOrderParams(
                    pizzaName = pizzaName,
                    price = price,
                    clientName = clientName,
                    totalPaid = totalPaid,
                    changeReturned = changeReturned,
                    tableNumber = tableNumber
                )
            ).onSuccess {
                _uiState.update { it.copy(isLoading = false, orderSent = true) }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Error al enviar orden") }
            }
        }
    }
}
