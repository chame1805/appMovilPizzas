package com.chame.myapplication.feacture.administrador.data.datasource.model

import com.google.gson.annotations.SerializedName

data class SaleRecordDto(
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
