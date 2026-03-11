package com.chame.myapplication.features.pizzeriadistrito.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chame.myapplication.core.websocket.WaiterWebSocketManager
import com.chame.myapplication.features.pizzeriadistrito.data.SharedOrderStore
import com.chame.myapplication.features.pizzeriadistrito.domain.entities.WaiterOrder
import com.chame.myapplication.features.pizzeriadistrito.domain.usecases.GetWaiterOrdersUseCase
import com.chame.myapplication.features.pizzeriadistrito.domain.usecases.MarkOrderDeliveredUseCase
import com.chame.myapplication.features.pizzeriadistrito.domain.usecases.UpdateWaiterOrderUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HistoryUiState(
    val isLoading: Boolean = false,
    val orders: List<WaiterOrder> = emptyList(),
    val error: String? = null,
    val editError: String? = null
)

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val getWaiterOrdersUseCase: GetWaiterOrdersUseCase,
    private val markOrderDeliveredUseCase: MarkOrderDeliveredUseCase,
    private val updateWaiterOrderUseCase: UpdateWaiterOrderUseCase,
    private val waiterWebSocketManager: WaiterWebSocketManager,
    private val sharedOrderStore: SharedOrderStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        loadOrders()
        collectWebSocketEvents()
    }

    fun loadOrders() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            getWaiterOrdersUseCase().onSuccess { apiOrders ->
                val enriched = apiOrders.map { order ->
                    val local = sharedOrderStore.getPayment(order.id)
                    if (local != null && order.totalPaid == 0.0) {
                        order.copy(totalPaid = local.first, changeReturned = local.second)
                    } else order
                }
                _uiState.update { it.copy(isLoading = false, orders = enriched) }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Error al cargar pedidos") }
            }
        }
    }

    fun markAsDelivered(orderId: Int) {
        _uiState.update { state ->
            state.copy(orders = state.orders.map { order ->
                if (order.id == orderId) order.copy(status = "DELIVERED") else order
            })
        }
        viewModelScope.launch {
            markOrderDeliveredUseCase(orderId).onFailure {
                loadOrders()
            }
        }
    }

    fun updateOrder(orderId: Int, clientName: String, tableNumber: Int, totalPaid: Double, changeReturned: Double) {
        val previous = _uiState.value.orders
        _uiState.update { state ->
            state.copy(
                editError = null,
                orders = state.orders.map { order ->
                    if (order.id == orderId) order.copy(clientName = clientName, tableNumber = tableNumber, totalPaid = totalPaid, changeReturned = changeReturned)
                    else order
                }
            )
        }
        viewModelScope.launch {
            updateWaiterOrderUseCase(orderId, clientName, tableNumber, totalPaid, changeReturned).onFailure { e ->
                _uiState.update { it.copy(orders = previous, editError = e.message ?: "Error al editar pedido") }
            }
        }
    }

    fun clearEditError() = _uiState.update { it.copy(editError = null) }

    private fun collectWebSocketEvents() {
        viewModelScope.launch {
            waiterWebSocketManager.events.collect { event ->
                if (event.event == "ORDER_STATUS_CHANGED" || event.event == "ORDER_COMPLETED") {
                    val newStatus = event.status.ifEmpty { "COMPLETED" }
                    _uiState.update { state ->
                        state.copy(orders = state.orders.map { order ->
                            if (order.id == event.id) order.copy(status = newStatus) else order
                        })
                    }
                }
            }
        }
    }
}
