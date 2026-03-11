package com.chame.myapplication.feacture.auth.data.datasource.model

import com.google.gson.annotations.SerializedName

data class LoginRequestDto(
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String
)

data class AuthResponseDto(
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("token_type")
    val tokenType: String,
    @SerializedName("usuario")
    val usuario: UsuarioDto
)

data class UsuarioDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("email")
    val email: String,
    @SerializedName("nombre")
    val nombre: String,
    @SerializedName("rol")
    val rol: String,
    @SerializedName("fecha_registro")
    val fechaRegistro: String
)
