package com.chame.myapplication.features.pizzeriadistrito.data.repositories

import com.chame.myapplication.core.session.SessionManager
import com.chame.myapplication.features.pizzeriadistrito.data.SharedOrderStore
import com.chame.myapplication.features.pizzeriadistrito.data.datasources.remote.WaiterOrderApi
import com.chame.myapplication.features.pizzeriadistrito.data.datasources.remote.mapper.toDomain
import com.chame.myapplication.features.pizzeriadistrito.data.datasources.remote.model.CreateOrderRequestDto
import com.chame.myapplication.features.pizzeriadistrito.data.datasources.remote.model.UpdateFullOrderRequestDto
import com.chame.myapplication.features.pizzeriadistrito.data.datasources.remote.model.UpdateOrderStatusRequestDto
import com.chame.myapplication.features.pizzeriadistrito.domain.entities.WaiterOrder
import com.chame.myapplication.features.pizzeriadistrito.domain.repositories.WaiterOrderRepository
import com.chame.myapplication.features.pizzeriadistrito.domain.usecases.CreateOrderParams
import javax.inject.Inject

class WaiterOrderRepositoryImpl @Inject constructor(
    private val api: WaiterOrderApi,
    private val sessionManager: SessionManager,
    private val sharedOrderStore: SharedOrderStore
) : WaiterOrderRepository {

    private fun authHeader(): String = "Bearer ${sessionManager.token}"

    override suspend fun getMyOrders(): Result<List<WaiterOrder>> {
        return runCatching {
            api.getMyOrders(authHeader()).map { it.toDomain() }
        }
    }

    override suspend fun createOrder(params: CreateOrderParams): Result<Unit> {
        return runCatching {
            val response = api.createOrder(
                token = authHeader(),
                body = CreateOrderRequestDto(
                    pizzaName = params.pizzaName,
                    price = params.price,
                    clientName = params.clientName,
                    totalPaid = params.totalPaid,
                    changeReturned = params.changeReturned,
                    tableNumber = params.tableNumber,
                    waiterId = sessionManager.userId
                )
            )
            sharedOrderStore.savePayment(response.id, params.totalPaid, params.changeReturned)
        }
    }

    override suspend fun updateOrderStatus(orderId: Int, status: String): Result<Unit> {
        return runCatching {
            api.updateOrderStatus(authHeader(), orderId, UpdateOrderStatusRequestDto(status)).close()
        }
    }

    override suspend fun updateOrder(orderId: Int, clientName: String, tableNumber: Int, totalPaid: Double, changeReturned: Double): Result<Unit> {
        return runCatching {
            api.updateOrder(authHeader(), orderId, UpdateFullOrderRequestDto(clientName, tableNumber, totalPaid, changeReturned)).close()
            sharedOrderStore.savePayment(orderId, totalPaid, changeReturned)
        }
    }
}
