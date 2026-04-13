package com.chame.myapplication.feacturecocina.data.repositories

import com.chame.myapplication.core.session.SessionManager
import com.chame.myapplication.feacturecocina.data.datasource.CocineroApi
import com.chame.myapplication.feacturecocina.data.datasource.mapper.toDomain
import com.chame.myapplication.feacturecocina.data.datasource.model.UpdateStatusRequestDto
import com.chame.myapplication.feacturecocina.domain.entities.KitchenOrder
import com.chame.myapplication.feacturecocina.domain.repositories.CocineroRepository
import javax.inject.Inject

class CocineroRepositoryImpl @Inject constructor(
    private val api: CocineroApi,
    private val sessionManager: SessionManager
) : CocineroRepository {

    private fun authHeader(): String = "Bearer ${sessionManager.token}"

    override suspend fun getActiveOrders(): Result<List<KitchenOrder>> {
        return runCatching {
            api.getActiveOrders(authHeader()).map { it.toDomain() }
        }
    }

    override suspend fun updateOrderStatus(orderId: Int, newStatus: String): Result<Unit> {
        return runCatching {
            api.updateOrderStatus(authHeader(), orderId, UpdateStatusRequestDto(newStatus)).close()
        }
    }
}
