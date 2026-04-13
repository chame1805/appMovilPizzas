package com.chame.myapplication.feacture.administrador.data.datasource.model

import com.google.gson.annotations.SerializedName

data class SaleRecordDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName(value = "pizza_name", alternate = ["pizzaName", "nombre_pizza"])
    val pizzaName: String,
    @SerializedName(value = "price", alternate = ["precio", "monto", "total"])
    val price: Double?,
    @SerializedName(value = "client_name", alternate = ["clientName", "nombre_cliente"])
    val clientName: String,
    @SerializedName(value = "total_paid", alternate = ["totalPaid", "total_pagado", "pagado"])
    val totalPaid: Double?,
    @SerializedName(value = "change_returned", alternate = ["changeReturned", "cambio", "vuelto"])
    val changeReturned: Double?,
    @SerializedName(value = "table_number", alternate = ["tableNumber", "mesa"])
    val tableNumber: Int,
    @SerializedName("status")
    val status: String,
    @SerializedName(value = "created_at", alternate = ["createdAt"])
    val createdAt: String
)
