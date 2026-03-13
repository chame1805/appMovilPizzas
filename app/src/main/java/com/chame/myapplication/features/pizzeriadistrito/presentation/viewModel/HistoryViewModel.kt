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
    val editError: String? = null,
    val editingOrder: WaiterOrder? = null,
    val editClientName: String = "",
    val editTableText: String = "",
    val editPaidText: String = ""
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
                        order.copy(
                            price = if (order.price == 0.0) local.price else order.price,
                            totalPaid = local.totalPaid,
                            changeReturned = local.changeReturned
                        )
                    } else order
                }
                val mergedOrders = sharedOrderStore.mergeOrders(enriched)
                _uiState.update { it.copy(isLoading = false, orders = sortForWaiter(mergedOrders)) }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Error al cargar pedidos") }
            }
        }
    }

    fun markAsDelivered(orderId: Int) {
        sharedOrderStore.updateOrderStatus(orderId, "DELIVERED")
        _uiState.update { state ->
            val updated = state.orders.map { order ->
                if (order.id == orderId) order.copy(status = "DELIVERED") else order
            }
            state.copy(orders = sortForWaiter(updated))
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
                orders = sortForWaiter(state.orders.map { order ->
                    if (order.id == orderId) {
                        order.copy(clientName = clientName, tableNumber = tableNumber, totalPaid = totalPaid, changeReturned = changeReturned)
                            .also { sharedOrderStore.upsertOrder(it) }
                    } else order
                })
            )
        }
        viewModelScope.launch {
            updateWaiterOrderUseCase(orderId, clientName, tableNumber, totalPaid, changeReturned).onFailure { e ->
                _uiState.update { it.copy(orders = previous, editError = e.message ?: "Error al editar pedido") }
            }
        }
    }

    fun openEditDialog(order: WaiterOrder) {
        _uiState.update { it.copy(
            editingOrder = order,
            editClientName = order.clientName,
            editTableText = order.tableNumber.toString(),
            editPaidText = if (order.totalPaid > 0) order.totalPaid.toString() else "",
            editError = null
        )}
    }

    fun closeEditDialog() {
        _uiState.update { it.copy(editingOrder = null, editClientName = "", editTableText = "", editPaidText = "", editError = null) }
    }

    fun setEditClientName(value: String) = _uiState.update { it.copy(editClientName = value) }
    fun setEditTableText(value: String) { if (value.all { c -> c.isDigit() }) _uiState.update { it.copy(editTableText = value) } }
    fun setEditPaidText(value: String) { if (value.all { c -> c.isDigit() || c == '.' }) _uiState.update { it.copy(editPaidText = value) } }

    fun clearEditError() = _uiState.update { it.copy(editError = null) }


    private fun sortForWaiter(orders: List<WaiterOrder>): List<WaiterOrder> {
        fun priority(status: String): Int = when (status) {
            "COMPLETED" -> 0
            "IN_PROGRESS" -> 1
            "PENDING" -> 2
            "DELIVERED" -> 3
            else -> 4
        }
        return orders.sortedWith(compareBy<WaiterOrder> { priority(it.status) }.thenByDescending { it.id })
    }

    private fun resolveEventStatus(eventName: String, rawStatus: String): String {
        val normalized = rawStatus.trim().uppercase().replace(' ', '_')
        if (normalized.isNotEmpty()) {
            return when (normalized) {
                "LISTA", "READY" -> "COMPLETED"
                "PREPARANDO" -> "IN_PROGRESS"
                else -> normalized
            }
        }
        return when (eventName) {
            "ORDER_COMPLETED" -> "COMPLETED"
            else -> "IN_PROGRESS"
        }
    }

    private fun collectWebSocketEvents() {
        viewModelScope.launch {
            waiterWebSocketManager.events.collect { event ->
                if (event.event == "ORDER_STATUS_CHANGED" || event.event == "ORDER_COMPLETED") {
                    val newStatus = resolveEventStatus(event.event, event.status)
                    sharedOrderStore.updateOrderStatus(event.id, newStatus)
                    var found = false
                    _uiState.update { state ->
                        val updated = state.orders.map { order ->
                            if (order.id == event.id) {
                                found = true
                                order.copy(status = newStatus)
                            } else order
                        }
                        state.copy(orders = sortForWaiter(updated))
                    }
                    if (!found) {
                        loadOrders()
                    }
                }
            }
        }
    }
}
