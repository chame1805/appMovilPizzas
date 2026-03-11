package com.chame.myapplication.feacturecocina.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chame.myapplication.core.websocket.KitchenWebSocketManager
import com.chame.myapplication.feacturecocina.domain.entities.KitchenOrder
import com.chame.myapplication.feacturecocina.domain.usescases.GetActiveOrdersUseCase
import com.chame.myapplication.feacturecocina.domain.usescases.UpdateOrderStatusUseCase
import com.chame.myapplication.feacturecocina.presentation.screens.CocineroUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class CocineroViewModel @Inject constructor(
    private val getActiveOrdersUseCase: GetActiveOrdersUseCase,
    private val updateOrderStatusUseCase: UpdateOrderStatusUseCase,
    private val kitchenWebSocketManager: KitchenWebSocketManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(CocineroUiState())
    val uiState: StateFlow<CocineroUiState> = _uiState.asStateFlow()

    init {
        loadOrders()
        connectWebSocket()
    }

    fun loadOrders() {
        viewModelScope.launch {
            val currentCompleted = _uiState.value.orders.filter { it.status == "COMPLETED" }
            _uiState.update { it.copy(isLoading = true, error = null) }
            getActiveOrdersUseCase().onSuccess { freshOrders ->
                val freshIds = freshOrders.map { it.id }.toSet()
                val preserved = currentCompleted.filter { it.id !in freshIds }
                _uiState.update { it.copy(isLoading = false, orders = freshOrders + preserved) }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Error al cargar órdenes") }
            }
        }
    }

    private fun connectWebSocket() {
        kitchenWebSocketManager.connect()
        viewModelScope.launch {
            kitchenWebSocketManager.events.collect { event ->
                when (event.event) {
                    "NEW_ORDER" -> {
                        val newOrder = KitchenOrder(
                            id = event.id,
                            pizzaName = event.pizzaName,
                            tableNumber = event.tableNumber,
                            clientName = event.clientName,
                            status = event.status,
                            createdAt = event.createdAt
                        )
                        _uiState.update { state ->
                            state.copy(orders = listOf(newOrder) + state.orders)
                        }
                    }
                    "ORDER_STATUS_CHANGED" -> {
                        _uiState.update { state ->
                            val updated = state.orders.map { order ->
                                if (order.id == event.id) order.copy(status = event.status)
                                else order
                            }.filter { it.status != "DELIVERED" }
                            state.copy(orders = updated)
                        }
                    }
                }
            }
        }
    }

    fun updateStatus(orderId: Int, newStatus: String) {
        viewModelScope.launch {
            val previousOrders = _uiState.value.orders
            _uiState.update { state ->
                val updated = state.orders.map { order ->
                    if (order.id == orderId) order.copy(status = newStatus)
                    else order
                }.filter { it.status != "DELIVERED" }
                state.copy(orders = updated)
            }

            updateOrderStatusUseCase(orderId, newStatus).onFailure { e ->
                _uiState.update { it.copy(orders = previousOrders, error = e.message ?: "Error al actualizar estado") }
            }
        }
    }

    fun getNextStatus(currentStatus: String): String? {
        return when (currentStatus) {
            "PENDING" -> "IN_PROGRESS"
            "IN_PROGRESS" -> "COMPLETED"
            else -> null
        }
    }

    fun getStatusLabel(status: String): String {
        return when (status) {
            "PENDING" -> "Pendiente"
            "IN_PROGRESS" -> "En preparación"
            "COMPLETED" -> "Listo — esperando entrega"
            "DELIVERED" -> "Entregado"
            else -> status
        }
    }

    override fun onCleared() {
        super.onCleared()
        kitchenWebSocketManager.disconnect()
    }
}
