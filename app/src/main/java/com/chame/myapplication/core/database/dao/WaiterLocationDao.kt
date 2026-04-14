package com.chame.myapplication.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.chame.myapplication.core.database.entity.WaiterLocationEntity

@Dao
interface WaiterLocationDao {

    @Insert
    suspend fun insert(location: WaiterLocationEntity)

    @Query("SELECT * FROM waiter_locations WHERE syncStatus = 'PENDING' ORDER BY timestamp DESC LIMIT 50")
    suspend fun getPending(): List<WaiterLocationEntity>

    @Query("UPDATE waiter_locations SET syncStatus = 'SYNCED' WHERE id IN (:ids)")
    suspend fun markSynced(ids: List<Long>)

    @Query("UPDATE waiter_locations SET syncStatus = 'FAILED' WHERE id IN (:ids)")
    suspend fun markFailed(ids: List<Long>)

    @Query("DELETE FROM waiter_locations WHERE syncStatus = 'SYNCED'")
    suspend fun clearSynced()

    @Query("SELECT * FROM waiter_locations WHERE waiterId = :waiterId ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLastLocation(waiterId: Int): WaiterLocationEntity?
}
