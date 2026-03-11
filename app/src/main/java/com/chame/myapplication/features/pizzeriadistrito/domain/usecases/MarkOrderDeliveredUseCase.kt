package com.chame.myapplication.features.pizzeriadistrito.domain.usecases

import com.chame.myapplication.features.pizzeriadistrito.domain.repositories.WaiterOrderRepository
import javax.inject.Inject

class MarkOrderDeliveredUseCase @Inject constructor(
    private val repository: WaiterOrderRepository
) {
    suspend operator fun invoke(orderId: Int): Result<Unit> =
        repository.updateOrderStatus(orderId, "DELIVERED")
}
