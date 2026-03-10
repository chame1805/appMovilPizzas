package com.chame.myapplication.feacture.register.data.datasource.model

import com.google.gson.annotations.SerializedName

data class RegisterRequestDto(
    @SerializedName("name")
    val name: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String
)

data class RegisterResponseDto(
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("token_type")
    val tokenType: String
)
