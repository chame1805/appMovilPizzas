package com.chame.myapplication.features.pizzeriadistrito.domain.entities

data class WaiterOrder(
    val id: Int,
    val pizzaName: String,
    val price: Double,
    val clientName: String,
    val totalPaid: Double,
    val changeReturned: Double,
    val tableNumber: Int,
    val status: String,
    val createdAt: String
)
