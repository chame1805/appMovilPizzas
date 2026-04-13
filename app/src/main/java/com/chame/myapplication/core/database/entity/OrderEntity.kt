package com.chame.myapplication.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.chame.myapplication.features.pizzeriadistrito.domain.entities.WaiterOrder

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val id: Int,
    val pizzaName: String,
    val price: Double,
    val clientName: String,
    val totalPaid: Double,
    val changeReturned: Double,
    val tableNumber: Int,
    val status: String,
    val createdAt: String
)

fun OrderEntity.toDomain(): WaiterOrder = WaiterOrder(
    id = id,
    pizzaName = pizzaName,
    price = price,
    clientName = clientName,
    totalPaid = totalPaid,
    changeReturned = changeReturned,
    tableNumber = tableNumber,
    status = status,
    createdAt = createdAt
)

fun WaiterOrder.toEntity(): OrderEntity = OrderEntity(
    id = id,
    pizzaName = pizzaName,
    price = price,
    clientName = clientName,
    totalPaid = totalPaid,
    changeReturned = changeReturned,
    tableNumber = tableNumber,
    status = status,
    createdAt = createdAt
)
