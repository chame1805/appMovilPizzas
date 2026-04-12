package com.chame.myapplication.features.pizzeriadistrito.data.datasources.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {
    
    @Insert
    suspend fun insertOrder(order: OrderEntity): Long
    
    @Query("SELECT * FROM orders ORDER BY createdAt DESC")
    fun getAllOrders(): Flow<List<OrderEntity>>
    
    @Query("SELECT * FROM orders WHERE id = :orderId")
    suspend fun getOrderById(orderId: Int): OrderEntity?
    
    @Query("DELETE FROM orders WHERE id = :orderId")
    suspend fun deleteOrder(orderId: Int)
    
    @Delete
    suspend fun deleteOrderEntity(order: OrderEntity)
    
    @Query("DELETE FROM orders")
    suspend fun deleteAllOrders()
    
    @Query("SELECT COUNT(*) FROM orders")
    fun getOrderCount(): Flow<Int>
}
