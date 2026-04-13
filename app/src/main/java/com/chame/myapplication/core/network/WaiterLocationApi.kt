package com.chame.myapplication.core.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * API endpoint for waiter location updates
 */
interface WaiterLocationApi {

    data class LocationUpdate(
        val waiterId: String,
        val latitude: Double,
        val longitude: Double,
        val timestamp: Long
    )

    @POST("waiter/location")
    suspend fun updateLocation(@Body location: LocationUpdate): Response<Unit>
}
