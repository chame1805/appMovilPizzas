package com.chame.myapplication.feacture.auth.domian.entities

data class AuthResponse(
    val accessToken: String,
    val tokenType: String,
    val userId: Int,
    val email: String,
    val nombre: String,
    val rol: String
)