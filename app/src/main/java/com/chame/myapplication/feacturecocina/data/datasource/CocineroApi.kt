package com.chame.myapplication.feacturecocina.data.datasource

import com.chame.myapplication.feacturecocina.data.datasource.model.KitchenOrderDto
import com.chame.myapplication.feacturecocina.data.datasource.model.UpdateStatusRequestDto
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.Path

interface CocineroApi {

    @GET("orders/")
    suspend fun getActiveOrders(
        @Header("Authorization") token: String
    ): List<KitchenOrderDto>

    @PATCH("orders/{orderId}/status/")
    suspend fun updateOrderStatus(
        @Header("Authorization") token: String,
        @Path("orderId") orderId: Int,
        @Body body: UpdateStatusRequestDto
    ): ResponseBody
}
