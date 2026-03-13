package com.chame.myapplication.feacture.administrador.data.repositories

import com.chame.myapplication.core.session.SessionManager
import com.chame.myapplication.feacture.administrador.data.datasource.AdminOrdersApi
import com.chame.myapplication.feacture.administrador.data.datasource.mapper.toDomain
import com.chame.myapplication.feacture.administrador.data.datasource.model.SaleRecordDto
import com.chame.myapplication.feacture.administrador.domain.entities.SaleRecord
import com.chame.myapplication.feacture.administrador.domain.repositories.AdminOrdersRepository
import com.chame.myapplication.features.pizzeriadistrito.data.SharedOrderStore
import javax.inject.Inject

class AdminOrdersRepositoryImpl @Inject constructor(
    private val api: AdminOrdersApi,
    private val sessionManager: SessionManager,
    private val sharedOrderStore: SharedOrderStore
) : AdminOrdersRepository {

    private suspend fun fetchSalesHistory(token: String): List<SaleRecordDto> {
        var firstSuccess: List<SaleRecordDto>? = null
        var lastError: Throwable? = null

        val sources = listOf<suspend () -> List<SaleRecordDto>>(
            { api.getSalesHistoryV2(token) },
            { api.getSalesHistoryV1(token) },
            { api.getSalesHistoryLegacy(token) }
        )

        for (source in sources) {
            try {
                val result = source()
                if (firstSuccess == null) firstSuccess = result
                if (result.isNotEmpty()) return result
            } catch (t: Throwable) {
                lastError = t
            }
        }

        return firstSuccess ?: throw (lastError ?: IllegalStateException("No fue posible obtener historial de ventas"))
    }

    override suspend fun getSalesHistory(): Result<List<SaleRecord>> {
        return runCatching {
            fetchSalesHistory("Bearer ${sessionManager.token}").map { dto ->
                val record = dto.toDomain()
                val local = sharedOrderStore.getPayment(record.id)
                if (local != null) {
                    record.copy(
                        price = if (record.price == 0.0) local.price else record.price,
                        totalPaid = if (record.totalPaid == 0.0) local.totalPaid else record.totalPaid,
                        changeReturned = if (record.changeReturned == 0.0) local.changeReturned else record.changeReturned
                    )
                } else {
                    record
                }
            }
        }
    }
}
