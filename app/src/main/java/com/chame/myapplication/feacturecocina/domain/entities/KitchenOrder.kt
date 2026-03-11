package com.chame.myapplication.feacturecocina.domain.entities

data class KitchenOrder(
    val id: Int,
    val pizzaName: String,
    val tableNumber: Int,
    val clientName: String,
    val status: String,
    val createdAt: String
)
