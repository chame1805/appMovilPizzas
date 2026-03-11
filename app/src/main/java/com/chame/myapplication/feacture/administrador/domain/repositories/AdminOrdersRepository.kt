package com.chame.myapplication.feacture.administrador.domain.repositories

import com.chame.myapplication.feacture.administrador.domain.entities.SaleRecord

interface AdminOrdersRepository {
    suspend fun getSalesHistory(): Result<List<SaleRecord>>
}
