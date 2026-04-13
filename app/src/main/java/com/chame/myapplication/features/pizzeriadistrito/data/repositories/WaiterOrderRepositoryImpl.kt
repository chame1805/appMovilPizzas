package com.chame.myapplication.features.pizzeriadistrito.data.repositories

import com.chame.myapplication.core.database.dao.OrderDao
import com.chame.myapplication.core.database.entity.toDomain
import com.chame.myapplication.core.database.entity.toEntity
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
    private val sharedOrderStore: SharedOrderStore,
    private val orderDao: OrderDao
) : WaiterOrderRepository {

    private fun authHeader(): String = "Bearer ${sessionManager.token}"

    override suspend fun getMyOrders(): Result<List<WaiterOrder>> {
        return try {
            // Intenta obtener del API
            val apiOrders = api.getMyOrders(authHeader()).map { it.toDomain() }
            // Guarda en Room como caché
            orderDao.upsertAll(apiOrders.map { it.toEntity() })
            Result.success(apiOrders)
        } catch (e: Exception) {
            // Si falla el API, carga desde Room (estrategia cache-first)
            val cached = orderDao.getAll().map { it.toDomain() }
            if (cached.isNotEmpty()) Result.success(cached)
            else Result.failure(e)
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
            sharedOrderStore.savePayment(response.id, params.price, params.totalPaid, params.changeReturned)
            // Persiste la nueva orden en Room
            orderDao.upsert(
                WaiterOrder(
                    id = response.id,
                    pizzaName = params.pizzaName,
                    price = params.price,
                    clientName = params.clientName,
                    totalPaid = params.totalPaid,
                    changeReturned = params.changeReturned,
                    tableNumber = params.tableNumber,
                    status = "PENDING",
                    createdAt = ""
                ).toEntity()
            )
        }
    }

    override suspend fun updateOrderStatus(orderId: Int, status: String): Result<Unit> {
        return runCatching {
            api.updateOrderStatus(authHeader(), orderId, UpdateOrderStatusRequestDto(status)).close()
            // Actualiza el estado en Room también
            orderDao.updateStatus(orderId, status)
        }
    }

    override suspend fun updateOrder(orderId: Int, clientName: String, tableNumber: Int, totalPaid: Double, changeReturned: Double): Result<Unit> {
        return runCatching {
            api.updateOrder(authHeader(), orderId, UpdateFullOrderRequestDto(clientName, tableNumber, totalPaid, changeReturned)).close()
            val prev = sharedOrderStore.getPayment(orderId)
            sharedOrderStore.savePayment(orderId, prev?.price ?: 0.0, totalPaid, changeReturned)
            // Actualiza en Room también
            orderDao.updateOrder(orderId, clientName, tableNumber, totalPaid, changeReturned)
        }
    }
}
