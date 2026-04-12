package com.chame.myapplication.features.pizzeriadistrito.data.datasources.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val pizzaName: String,
    val price: Double,
    val clientName: String,
    val totalPaid: Double,
    val changeReturned: Double,
    val date: String,
    val createdAt: Long = System.currentTimeMillis()
)
