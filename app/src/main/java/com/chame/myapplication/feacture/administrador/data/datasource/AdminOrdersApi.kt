package com.chame.myapplication.feacture.administrador.data.datasource

import com.chame.myapplication.feacture.administrador.data.datasource.model.SaleRecordDto
import retrofit2.http.GET
import retrofit2.http.Header

interface AdminOrdersApi {

    @GET("orders/")
    suspend fun getSalesHistory(
        @Header("Authorization") token: String
    ): List<SaleRecordDto>
}
