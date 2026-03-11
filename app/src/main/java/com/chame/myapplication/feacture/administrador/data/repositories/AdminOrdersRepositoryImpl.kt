package com.chame.myapplication.feacture.administrador.data.repositories

import com.chame.myapplication.core.session.SessionManager
import com.chame.myapplication.feacture.administrador.data.datasource.AdminOrdersApi
import com.chame.myapplication.feacture.administrador.data.datasource.mapper.toDomain
import com.chame.myapplication.feacture.administrador.domain.entities.SaleRecord
import com.chame.myapplication.feacture.administrador.domain.repositories.AdminOrdersRepository
import javax.inject.Inject

class AdminOrdersRepositoryImpl @Inject constructor(
    private val api: AdminOrdersApi,
    private val sessionManager: SessionManager
) : AdminOrdersRepository {

    override suspend fun getSalesHistory(): Result<List<SaleRecord>> {
        return runCatching {
            api.getSalesHistory("Bearer ${sessionManager.token}").map { it.toDomain() }
        }
    }
}
