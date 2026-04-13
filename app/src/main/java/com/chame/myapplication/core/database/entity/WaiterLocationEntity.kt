package com.chame.myapplication.core.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "waiter_locations",
    indices = [Index(value = ["waiterId", "timestamp"])]
)
data class WaiterLocationEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val waiterId: String,
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long = System.currentTimeMillis(),
    val synced: Boolean = false
)
