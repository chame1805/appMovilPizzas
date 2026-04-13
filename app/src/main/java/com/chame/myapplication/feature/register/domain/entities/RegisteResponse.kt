package com.chame.myapplication.feacture.register.domain.entities

data class RegisteResponse(
    val access_token: String,
    val token_type: String,
    val userId: Int,
    val nombre: String,
    val email: String,
    val rol: String
)
