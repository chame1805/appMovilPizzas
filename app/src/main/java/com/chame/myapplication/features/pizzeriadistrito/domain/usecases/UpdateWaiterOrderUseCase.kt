package com.chame.myapplication.features.pizzeriadistrito.domain.usecases

import com.chame.myapplication.features.pizzeriadistrito.domain.repositories.WaiterOrderRepository
import javax.inject.Inject

class UpdateWaiterOrderUseCase @Inject constructor(
    private val repository: WaiterOrderRepository
) {
    suspend operator fun invoke(
        orderId: Int,
        clientName: String,
        tableNumber: Int,
        totalPaid: Double,
        changeReturned: Double
    ): Result<Unit> = repository.updateOrder(orderId, clientName, tableNumber, totalPaid, changeReturned)
}
