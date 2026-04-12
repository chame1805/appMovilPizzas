package com.chame.myapplication.features.pizzeriadistrito.data.datasources.local

import com.chame.myapplication.features.pizzeriadistrito.domain.entities.Order
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class OrderLocalDataSource @Inject constructor(
    private val orderDao: OrderDao
) {
    
    suspend fun insertOrder(order: Order): Long {
        return orderDao.insertOrder(OrderMapper.toEntity(order))
    }
    
    fun getAllOrders(): Flow<List<Order>> {
        return orderDao.getAllOrders().map { entities ->
            OrderMapper.entitiesToDomain(entities)
        }
    }
    
    suspend fun getOrderById(orderId: Int): Order? {
        return orderDao.getOrderById(orderId)?.let {
            OrderMapper.toDomain(it)
        }
    }
    
    suspend fun deleteOrder(orderId: Int) {
        orderDao.deleteOrder(orderId)
    }
    
    suspend fun deleteAllOrders() {
        orderDao.deleteAllOrders()
    }
    
    fun getOrderCount(): Flow<Int> {
        return orderDao.getOrderCount()
    }
}
