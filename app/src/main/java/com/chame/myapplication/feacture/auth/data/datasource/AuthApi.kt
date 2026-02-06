package com.chame.myapplication.feacture.auth.data.datasource

import com.chame.myapplication.feacture.auth.domian.entities.AuthResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("auth/login")
    suspend fun login(@Body credentials: Map<String, String>): AuthResponse
}