package com.chame.myapplication.core.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

data class LocationUpdateDto(
    val waiterId: Int,
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float,
    val timestamp: Long
)

interface WaiterLocationApi {

    @POST("waiter/location")
    suspend fun updateLocation(
        @Header("Authorization") token: String,
        @Body body: LocationUpdateDto
    ): Response<Unit>
}
