package com.chame.myapplication.feacture.auth.data.datasource

import com.chame.myapplication.feacture.auth.data.datasource.model.AuthResponseDto
import com.chame.myapplication.feacture.auth.data.datasource.model.LoginRequestDto
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("auth/login")
    suspend fun login(@Body credentials: LoginRequestDto): AuthResponseDto
}
