package com.chame.myapplication.features.pizzeriadistrito.data.datasources.remote.model

import com.google.gson.annotations.SerializedName

data class CreateOrderRequestDto(
    @SerializedName("pizza_name")
    val pizzaName: String,
    @SerializedName("price")
    val price: Double,
    @SerializedName("client_name")
    val clientName: String,
    @SerializedName("total_paid")
    val totalPaid: Double,
    @SerializedName("change_returned")
    val changeReturned: Double,
    @SerializedName("table_number")
    val tableNumber: Int,
    @SerializedName("waiter_id")
    val waiterId: Int
)

data class CreateOrderResponseDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("pizza_name")
    val pizzaName: String,
    @SerializedName("status")
    val status: String
)

data class WaiterOrderDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("pizza_name")
    val pizzaName: String,
    @SerializedName("price")
    val price: Double,
    @SerializedName("client_name")
    val clientName: String,
    @SerializedName("total_paid")
    val totalPaid: Double,
    @SerializedName("change_returned")
    val changeReturned: Double,
    @SerializedName("table_number")
    val tableNumber: Int,
    @SerializedName("status")
    val status: String,
    @SerializedName("created_at")
    val createdAt: String
)

data class UpdateOrderStatusRequestDto(
    @SerializedName("status")
    val status: String
)

data class UpdateFullOrderRequestDto(
    @SerializedName("client_name")
    val clientName: String,
    @SerializedName("table_number")
    val tableNumber: Int,
    @SerializedName("total_paid")
    val totalPaid: Double,
    @SerializedName("change_returned")
    val changeReturned: Double
)
