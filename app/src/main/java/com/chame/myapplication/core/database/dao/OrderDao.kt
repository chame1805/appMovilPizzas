package com.chame.myapplication.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.chame.myapplication.core.database.entity.OrderEntity

@Dao
interface OrderDao {

    @Query("SELECT * FROM orders ORDER BY id DESC")
    suspend fun getAll(): List<OrderEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(orders: List<OrderEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(order: OrderEntity)

    @Query("UPDATE orders SET status = :status WHERE id = :orderId")
    suspend fun updateStatus(orderId: Int, status: String)

    @Query("UPDATE orders SET clientName = :clientName, tableNumber = :tableNumber, totalPaid = :totalPaid, changeReturned = :changeReturned WHERE id = :orderId")
    suspend fun updateOrder(orderId: Int, clientName: String, tableNumber: Int, totalPaid: Double, changeReturned: Double)

    @Query("DELETE FROM orders")
    suspend fun clearAll()

    // Sync Strategy Methods
    @Query("SELECT * FROM orders WHERE syncStatus = 'PENDING' ORDER BY localUpdatedAt ASC")
    suspend fun getPendingSync(): List<OrderEntity>

    @Query("UPDATE orders SET syncStatus = :syncStatus, serverUpdatedAt = :serverUpdatedAt WHERE id = :orderId")
    suspend fun updateSyncStatus(orderId: Int, syncStatus: String, serverUpdatedAt: Long)

    @Query("SELECT * FROM orders WHERE id = :orderId")
    suspend fun getById(orderId: Int): OrderEntity?
}
