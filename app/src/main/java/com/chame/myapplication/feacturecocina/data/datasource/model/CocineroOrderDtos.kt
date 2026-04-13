package com.chame.myapplication.feacturecocina.data.datasource.model

import com.google.gson.annotations.SerializedName

data class KitchenOrderDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("pizza_name")
    val pizzaName: String,
    @SerializedName("table_number")
    val tableNumber: Int,
    @SerializedName("client_name")
    val clientName: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("created_at")
    val createdAt: String
)

data class UpdateStatusRequestDto(
    @SerializedName("status")
    val status: String
)

data class UpdateStatusResponseDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("status")
    val status: String,
    @SerializedName("updated_at")
    val updatedAt: String
)
