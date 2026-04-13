package com.chame.myapplication.feacture.register.data.datasource

import com.chame.myapplication.feacture.register.data.datasource.model.RegisterRequestDto
import com.chame.myapplication.feacture.register.data.datasource.model.RegisterResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

interface RegisterApi {

    @POST("auth/register")
    suspend fun register(
        @Body body: RegisterRequestDto
    ): RegisterResponseDto

}
