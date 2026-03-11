package com.chame.myapplication.feactures.Admin.data.datasource

import com.chame.myapplication.feactures.Admin.data.datasource.model.PizzaDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface AdminApi {
    @GET("productos/")
    suspend fun getPizzas(): List<PizzaDto>

    @POST("productos/")
    suspend fun createPizza(@Body pizza: PizzaDto): PizzaDto

    @PUT("productos/{id}")
    suspend fun updatePizza(@Path("id") id: Int, @Body pizza: PizzaDto): PizzaDto

    @DELETE("productos/{id}")
    suspend fun deletePizza(@Path("id") id: Int)
}
