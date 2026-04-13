package com.chame.myapplication.features.pizzeriadistrito.data.datasources.remote

import com.chame.myapplication.features.pizzeriadistrito.data.datasources.remote.model.CreateOrderRequestDto
import com.chame.myapplication.features.pizzeriadistrito.data.datasources.remote.model.CreateOrderResponseDto
import com.chame.myapplication.features.pizzeriadistrito.data.datasources.remote.model.UpdateFullOrderRequestDto
import com.chame.myapplication.features.pizzeriadistrito.data.datasources.remote.model.UpdateOrderStatusRequestDto
import com.chame.myapplication.features.pizzeriadistrito.data.datasources.remote.model.WaiterOrderDto
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface WaiterOrderApi {

    @POST("orders/")
    suspend fun createOrder(
        @Header("Authorization") token: String,
        @Body body: CreateOrderRequestDto
    ): CreateOrderResponseDto

    @GET("orders/")
    suspend fun getMyOrders(
        @Header("Authorization") token: String
    ): List<WaiterOrderDto>

    @PATCH("orders/{orderId}/status/")
    suspend fun updateOrderStatus(
        @Header("Authorization") token: String,
        @Path("orderId") orderId: Int,
        @Body body: UpdateOrderStatusRequestDto
    ): ResponseBody

    @PATCH("orders/{orderId}/")
    suspend fun updateOrder(
        @Header("Authorization") token: String,
        @Path("orderId") orderId: Int,
        @Body body: UpdateFullOrderRequestDto
    ): ResponseBody
}
