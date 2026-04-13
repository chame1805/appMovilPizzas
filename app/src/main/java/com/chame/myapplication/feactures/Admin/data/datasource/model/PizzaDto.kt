package com.chame.myapplication.feactures.Admin.data.datasource.model

import com.google.gson.annotations.SerializedName

data class PizzaDto(
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("nombre")
    val nombre: String,
    @SerializedName("precio")
    val precio: Double
)
