package com.chame.myapplication.features.pizzeriadistrito.domain.usecases

import com.chame.myapplication.features.pizzeriadistrito.domain.repositories.WaiterOrderRepository
import javax.inject.Inject

data class CreateOrderParams(
    val pizzaName: String,
    val price: Double,
    val clientName: String,
    val totalPaid: Double,
    val changeReturned: Double,
    val tableNumber: Int
)

class CreateWaiterOrderUseCase @Inject constructor(
    private val repository: WaiterOrderRepository
) {
    suspend operator fun invoke(params: CreateOrderParams): Result<Unit> {
        return repository.createOrder(params)
    }
}
