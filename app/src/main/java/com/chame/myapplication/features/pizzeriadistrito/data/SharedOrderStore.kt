package com.chame.myapplication.features.pizzeriadistrito.data

import com.chame.myapplication.features.pizzeriadistrito.domain.entities.WaiterOrder
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedOrderStore @Inject constructor() {

    data class OrderPayment(val price: Double, val totalPaid: Double, val changeReturned: Double)

    private val payments = mutableMapOf<Int, OrderPayment>()
    private val orderCache = mutableMapOf<Int, WaiterOrder>()

    fun savePayment(orderId: Int, price: Double, totalPaid: Double, changeReturned: Double) {
        payments[orderId] = OrderPayment(price, totalPaid, changeReturned)
    }

    fun getPayment(orderId: Int): OrderPayment? = payments[orderId]

    fun mergeOrders(orders: List<WaiterOrder>): List<WaiterOrder> {
        val freshById = orders.associateBy { it.id }

        // Conserva órdenes locales que aún no han sido entregadas aunque el backend no las devuelva.
        val pendingLocal = orderCache.values.filter { cached ->
            cached.status != "DELIVERED" && freshById[cached.id] == null
        }

        val merged = (orders + pendingLocal)
            .distinctBy { it.id }
            .sortedByDescending { it.id }

        orderCache.clear()
        merged.forEach { orderCache[it.id] = it }
        return merged
    }

    fun updateOrderStatus(orderId: Int, status: String) {
        val current = orderCache[orderId] ?: return
        orderCache[orderId] = current.copy(status = status)
    }

    fun upsertOrder(order: WaiterOrder) {
        orderCache[order.id] = order
    }
}
