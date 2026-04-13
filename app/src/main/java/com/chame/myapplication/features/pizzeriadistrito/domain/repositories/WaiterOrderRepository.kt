package com.chame.myapplication.features.pizzeriadistrito.domain.repositories

import com.chame.myapplication.features.pizzeriadistrito.domain.entities.WaiterOrder
import com.chame.myapplication.features.pizzeriadistrito.domain.usecases.CreateOrderParams

interface WaiterOrderRepository {
    suspend fun getMyOrders(): Result<List<WaiterOrder>>
    suspend fun createOrder(params: CreateOrderParams): Result<Unit>
    suspend fun updateOrderStatus(orderId: Int, status: String): Result<Unit>
    suspend fun updateOrder(orderId: Int, clientName: String, tableNumber: Int, totalPaid: Double, changeReturned: Double): Result<Unit>
}
