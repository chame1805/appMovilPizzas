package com.chame.myapplication.feacture.administrador.data.datasource

import com.chame.myapplication.feacture.administrador.data.datasource.model.SaleRecordDto
import retrofit2.http.GET
import retrofit2.http.Header

interface AdminOrdersApi {

    @GET("orders/sales/history/")
    suspend fun getSalesHistoryV2(
        @Header("Authorization") token: String
    ): List<SaleRecordDto>

    @GET("sales/history/")
    suspend fun getSalesHistoryV1(
        @Header("Authorization") token: String
    ): List<SaleRecordDto>

    @GET("orders/")
    suspend fun getSalesHistoryLegacy(
        @Header("Authorization") token: String
    ): List<SaleRecordDto>
}
