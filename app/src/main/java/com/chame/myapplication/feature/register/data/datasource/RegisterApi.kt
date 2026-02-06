package com.chame.myapplication.feacture.register.data.datasource

import com.chame.myapplication.feacture.register.domain.entities.RegisteResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface RegisterApi {

    @POST("auth/register")
    suspend fun register(
        @Body body: Map<String, String>
    ): RegisteResponse

}
