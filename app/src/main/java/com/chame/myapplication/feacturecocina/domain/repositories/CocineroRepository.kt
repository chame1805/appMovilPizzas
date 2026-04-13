package com.chame.myapplication.feacturecocina.domain.repositories

import com.chame.myapplication.feacturecocina.domain.entities.KitchenOrder

interface CocineroRepository {
    suspend fun getActiveOrders(): Result<List<KitchenOrder>>
    suspend fun updateOrderStatus(orderId: Int, newStatus: String): Result<Unit>
}
