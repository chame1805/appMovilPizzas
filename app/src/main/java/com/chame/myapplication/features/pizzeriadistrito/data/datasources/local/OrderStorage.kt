package com.chame.myapplication.features.pizzeriadistrito.data.datasources.local

import androidx.compose.runtime.mutableStateListOf
import com.chame.myapplication.features.pizzeriadistrito.domain.entities.Order
import kotlinx.coroutines.flow.Flow

/**
 * OrderStorage es ahora un delegador a Room Database
 * Mantiene compatibilidad con código existente
 */
object OrderStorage {
    
    // Para UI reactiva (en memoria - se sincroniza desde Room)
    val orders = mutableStateListOf<Order>()
    
    // Este será inyectado por Hilt cuando sea necesario
    var localDataSource: OrderLocalDataSource? = null
    
    fun addOrder(order: Order) {
        // Agrega a la lista en memoria
        orders.add(0, order)
    }
    
    /**
     * Obtiene todos los órdenes como Flow desde Room
     * Útil para colectar en ViewModels
     */
    fun getAllOrdersFlow(): Flow<List<Order>>? {
        return localDataSource?.getAllOrders()
    }
}
