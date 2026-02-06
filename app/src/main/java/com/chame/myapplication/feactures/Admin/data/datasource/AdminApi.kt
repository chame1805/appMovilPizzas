package com.chame.myapplication.feactures.Admin.data.datasource


import com.chame.myapplication.feactures.Admin.domain.entities.Pizza
import retrofit2.http.*

interface AdminApi {
    @GET("productos/")
    suspend fun getPizzas(): List<Pizza>

    @POST("productos/")
    suspend fun createPizza(@Body pizza: Pizza): Pizza

    @PUT("productos/{id}")
    suspend fun updatePizza(@Path("id") id: Int, @Body pizza: Pizza): Pizza

    @DELETE("productos/{id}")
    suspend fun deletePizza(@Path("id") id: Int)
}