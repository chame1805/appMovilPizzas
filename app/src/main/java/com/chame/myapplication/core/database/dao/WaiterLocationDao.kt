package com.chame.myapplication.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.chame.myapplication.core.database.entity.WaiterLocationEntity

@Dao
interface WaiterLocationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(location: WaiterLocationEntity)

    @Query("SELECT * FROM waiter_locations WHERE waiterId = :waiterId AND synced = 0 ORDER BY timestamp DESC")
    suspend fun getUnsynced(waiterId: String): List<WaiterLocationEntity>

    @Query("SELECT * FROM waiter_locations WHERE waiterId = :waiterId ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatest(waiterId: String): WaiterLocationEntity?

    @Query("UPDATE waiter_locations SET synced = 1 WHERE id = :id")
    suspend fun markSynced(id: Int)

    @Query("DELETE FROM waiter_locations WHERE timestamp < :beforeTimestamp")
    suspend fun deleteOldLocations(beforeTimestamp: Long)

    @Query("DELETE FROM waiter_locations")
    suspend fun clearAll()
}
