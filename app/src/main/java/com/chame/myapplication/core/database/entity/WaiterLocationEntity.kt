package com.chame.myapplication.core.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "waiter_locations",
    indices = [Index(value = ["waiterId", "timestamp"])]
)
data class WaiterLocationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val waiterId: Int,
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float,
    val timestamp: Long,
    val syncStatus: String = "PENDING" // PENDING | SYNCED | FAILED
)
